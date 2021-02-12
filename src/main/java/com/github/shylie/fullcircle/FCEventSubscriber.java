package com.github.shylie.fullcircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.shylie.fullcircle.command.LinkFCDebugFileCommand;
import com.github.shylie.fullcircle.command.RequestFCDebugFileCommand;
import com.github.shylie.fullcircle.command.StopSpellsCommand;
import com.github.shylie.fullcircle.lang.OpCode;
import com.github.shylie.fullcircle.lang.VM;
import com.github.shylie.fullcircle.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = FullCircle.MOD_ID)
public class FCEventSubscriber {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LinkFCDebugFileCommand.register(event.getDispatcher());
        RequestFCDebugFileCommand.register(event.getDispatcher());
        StopSpellsCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        for (int i = VMManager.MANAGER.size() - 1; i >= 0; i--) {
            if (VMManager.MANAGER.get(i).getCaster().getUniqueID().equals(event.getEntity().getUniqueID())) {
                VMManager.MANAGER.remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        for (int i = VMManager.MANAGER.size() - 1; i >= 0; i--) {
            if (VMManager.MANAGER.get(i).dimension == event.getWorld().getDimensionType()) {
                VMManager.MANAGER.remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        for (int i = VMManager.MANAGER.size() - 1; i >= 0; i--) {
            VMManager.MANAGER.removeRequest(event.getPlayer().getUniqueID());
            if (VMManager.MANAGER.get(i).getCaster().getUniqueID().equals(event.getPlayer().getUniqueID())) {
                VMManager.MANAGER.remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        final World world = event.getWorld();
        final Item item = event.getItemStack().getItem();
        final CompoundNBT nbt = event.getItemStack().getTag();

        if (item != Items.BOOK) {
            return;
        }

        boolean run = event.getPlayer().isCreative();
        for (int i = 0; !run && i < event.getPlayer().inventory.getSizeInventory(); i++) {
            final ItemStack stack = event.getPlayer().inventory.getStackInSlot(i);
            if (stack.isItemEqual(new ItemStack(Items.BLAZE_POWDER))) {
                stack.shrink(1);
                if (stack.getCount() == 0) {
                    event.getPlayer().inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
                run = true;
            }
        }

        if (nbt != null && nbt.contains("fcspelldata") && run) {
            event.getPlayer().swingArm(Hand.MAIN_HAND);
            if (event.getSide().isServer()) {
                Map<String, String> strings = new HashMap<>();
                for (String key : nbt.keySet()) {
                    if (key.startsWith("fcspellstring")) {
                        strings.put(key, nbt.getString(key));
                    }
                }
                String[] split = nbt.getString("fcspellstart").split("\n");
                int cx = Integer.parseInt(split[0]);
                int cy = Integer.parseInt(split[1]);
                switch (event.getPlayer().getHorizontalFacing())
                {
                    case EAST:
                        VMManager.MANAGER.add(new VM(nbt.getString("fcspelldata").split("\n"), strings, cx, cy, com.github.shylie.fullcircle.lang.Direction.RIGHT, event, world.getDimensionType()));
                        break;

                    case WEST:
                        VMManager.MANAGER.add(new VM(nbt.getString("fcspelldata").split("\n"), strings, cx, cy, com.github.shylie.fullcircle.lang.Direction.LEFT, event, world.getDimensionType()));
                        break;

                    case NORTH:
                        VMManager.MANAGER.add(new VM(nbt.getString("fcspelldata").split("\n"), strings, cx, cy, com.github.shylie.fullcircle.lang.Direction.DOWN, event, world.getDimensionType()));
                        break;

                    case SOUTH:
                        VMManager.MANAGER.add(new VM(nbt.getString("fcspelldata").split("\n"), strings, cx, cy, com.github.shylie.fullcircle.lang.Direction.UP, event, world.getDimensionType()));
                        break;

                    case UP:
                    case DOWN:
                        // don't run code, but shouldn't happen
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        final Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        final Item item = event.getItemStack().getItem();

        if (block == Blocks.LIME_GLAZED_TERRACOTTA && item == Items.BOOK && event.getItemStack().getCount() == 1) {
            event.getPlayer().swingArm(event.getHand());

            CompoundNBT nbt = event.getItemStack().getOrCreateTag();

            List<String> toRemove = new ArrayList<>();
            for (String key : nbt.keySet()) {
                if (key.startsWith("fcspell")) {
                    toRemove.add(key);
                }
            }
            for (String key : toRemove) {
                nbt.remove(key);
            }

            int dist = 1;
            boolean found = false;
            ArrayList<String> data = new ArrayList<>();
            data.add("A");

            final World world = event.getWorld();
            final int cx = event.getPos().getX();
            final int cy = event.getPos().getY();
            final int cz = event.getPos().getZ();

            nbt.putString("fcspellstart", String.format("%d\n%d", cx, cz));

            do {
                found = false;

                String toAdd = " ";
                for (int i = 0; i < dist - 1; i++) {
                    toAdd += "  ";
                }
                data.add(0, toAdd);
                data.add(toAdd);

                for (int i = 0; i < data.size(); i++) {
                    data.set(i, " " + data.get(i) + " ");
                }

                for (int i = -dist; i <= dist; i++) {
                    for (int j = -dist; j <= dist; j++) {
                        if (i > -dist && i < dist && j > -dist && j < dist) { continue; }

                        final BlockPos offsetPos = new BlockPos(cx + i, cy, cz + j);
                        final BlockState offsetBlockState = world.getBlockState(offsetPos);
                        final Block offsetBlock = offsetBlockState.getBlock();
                        
                        if (offsetBlock == Blocks.BLACK_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + "S" + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.RED_GLAZED_TERRACOTTA || offsetBlock == FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get()) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + "C" + data.get(j + dist).substring(i + dist + 1));
                            nbt.putString(String.format("fcspellstring %d %d", cx + i, cz + j), event.getWorld().getTileEntity(offsetPos) instanceof RedGlazedTerracottaSignTileEntity ? ((RedGlazedTerracottaSignTileEntity)event.getWorld().getTileEntity(offsetPos)).text : "");
                        }
                        else if (offsetBlock == Blocks.GREEN_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_GREEN + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.BROWN_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_BROWN + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.BLUE_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_BLUE + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.PURPLE_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_PURPLE + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.CYAN_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_CYAN + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_LIGHT_GRAY + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.GRAY_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_GRAY + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.PINK_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + "T" + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.LIME_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + "B" + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.YELLOW_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_YELLOW + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_LIGHT_BLUE + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.MAGENTA_GLAZED_TERRACOTTA) {
                            found = true;
                            String insert = null;
                            if (offsetBlockState.get(BlockStateProperties.HORIZONTAL_FACING) == Direction.EAST) {
                                insert = "<";
                            }
                            else if (offsetBlockState.get(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
                                insert = ">";
                            }
                            else if (offsetBlockState.get(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH) {
                                insert = "v";
                            }
                            else if (offsetBlockState.get(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
                                insert = "^";
                            }
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + insert + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.ORANGE_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + OpCode.ID_ORANGE + data.get(j + dist).substring(i + dist + 1));
                        }
                        else if (offsetBlock == Blocks.WHITE_GLAZED_TERRACOTTA) {
                            found = true;
                            data.set(j + dist, data.get(j + dist).substring(0, i + dist) + "L" + data.get(j + dist).substring(i + dist + 1));
                        }
                    }
                }
                dist++;
            } while (found);

            data.remove(0);
            data.remove(data.size() - 1);
            for (int i = 0; i < data.size(); i++) {
                data.set(i, data.get(i).substring(1, data.get(i).length() - 1));
            }

            nbt.putString("fcspelldata", String.join("\n", data));

            event.getItemStack().setTag(nbt);
        }
        else if (event.getHand() == Hand.MAIN_HAND && (block == Blocks.RED_GLAZED_TERRACOTTA || block == FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get()) && item == Items.PAPER && !event.getPlayer().isSneaking()) {
            event.getPlayer().swingArm(event.getHand());
            
            if (!event.getSide().isClient()) {
                if (block == Blocks.RED_GLAZED_TERRACOTTA) {
                    event.getWorld().setBlockState(event.getPos(), FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get().getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, event.getWorld().getBlockState(event.getPos()).get(BlockStateProperties.HORIZONTAL_FACING)), 2);
                }

                if (event.getWorld().getTileEntity(event.getPos()) instanceof RedGlazedTerracottaSignTileEntity) {
                    RedGlazedTerracottaSignTileEntity tileEntity = (RedGlazedTerracottaSignTileEntity)event.getWorld().getTileEntity(event.getPos());
                    tileEntity.text = event.getPlayer().getHeldItem(event.getHand()).getDisplayName().getString();
                    tileEntity.markDirty();
                    event.getWorld().notifyBlockUpdate(event.getPos(), event.getWorld().getBlockState(event.getPos()), event.getWorld().getBlockState(event.getPos()), 2);
                }
            }
        }
        else if (event.getHand() == Hand.MAIN_HAND && block == FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get() && item == Items.PAPER && event.getPlayer().isSneaking()) {
            if (event.getWorld().getTileEntity(event.getPos()) instanceof RedGlazedTerracottaSignTileEntity) {
                RedGlazedTerracottaSignTileEntity tileEntity = (RedGlazedTerracottaSignTileEntity)event.getWorld().getTileEntity(event.getPos());
                CommonProxy.PROXY.addToChat(new StringTextComponent("Stored text: " + tileEntity.text));
                event.getItemStack().setDisplayName(new StringTextComponent(tileEntity.text));
            }
        }
        else if (event.getHand() == Hand.MAIN_HAND && block == FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get() && event.getItemStack().isEmpty() && event.getPlayer().isSneaking()) {
            if (event.getWorld().getTileEntity(event.getPos()) instanceof RedGlazedTerracottaSignTileEntity) {
                RedGlazedTerracottaSignTileEntity tileEntity = (RedGlazedTerracottaSignTileEntity)event.getWorld().getTileEntity(event.getPos());
                CommonProxy.PROXY.addToChat(new StringTextComponent("Stored text: " + tileEntity.text));
            }
        }
    }

    @SubscribeEvent
    public static void onTick(WorldTickEvent event) {
        if (event.side.isClient()) {
            return;
        }

        for (int i = VMManager.MANAGER.size() - 1; i >= 0; i--) {
            if (VMManager.MANAGER.get(i).dimension != event.world.getDimensionType()) {
                continue;
            }
            
            for (int j = 0; j < 1250; j++) {
                boolean stop = false;
                switch (VMManager.MANAGER.get(i).run(event)) {
                    case CONTINUE:
                        stop = VMManager.MANAGER.get(i).delay();
                        break;

                    case OK:
                    case COMPILE_ERROR:
                    case RUNTIME_ERROR:
                        VMManager.MANAGER.remove(i);
                        stop = true;
                }

                if (stop) {
                    break;
                }
            }
        }
    }
}