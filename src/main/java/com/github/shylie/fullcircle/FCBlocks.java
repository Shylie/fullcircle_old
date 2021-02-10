package com.github.shylie.fullcircle;

import com.github.shylie.fullcircle.blocks.OtherPickBlockGlazedTerracottaBlock;
import com.github.shylie.fullcircle.blocks.RedGlazedTerracottaSign;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FullCircle.MOD_ID);

    // unused right now
    public static final RegistryObject<Block> WHITE_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("white_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.WHITE_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> ORANGE_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("orange_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.ORANGE_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("light_blue_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> YELLOW_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("yellow_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.YELLOW_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> PINK_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("pink_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.PINK_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> GRAY_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("gray_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.GRAY_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("light_gray_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> CYAN_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("cyan_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.CYAN_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> PURPLE_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("purple_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.PURPLE_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> BLUE_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("blue_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.BLUE_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> BROWN_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("brown_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.BROWN_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> GREEN_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("green_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.GREEN_GLAZED_TERRACOTTA));
    public static final RegistryObject<Block> BLACK_GLAZED_TERRACOTTA_DEBUG = BLOCKS.register("black_glazed_terracotta_debug", () -> new OtherPickBlockGlazedTerracottaBlock(Blocks.BLACK_GLAZED_TERRACOTTA));

    public static final RegistryObject<Block> RED_GLAZED_TERRACOTTA_SIGN = BLOCKS.register("red_glazed_terracotta_sign", () -> new RedGlazedTerracottaSign());

    // unused right now
    public static final RegistryObject<Block> RED_GLAZED_TERRACOTTA_SIGN_DEBUG = BLOCKS.register("red_glazed_terracotta_sign_debug", () -> new RedGlazedTerracottaSign());
}
