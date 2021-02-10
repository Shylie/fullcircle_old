package com.github.shylie.fullcircle.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;

public class OtherPickBlockGlazedTerracottaBlock extends GlazedTerracottaBlock {
    private final Block pick;

    public OtherPickBlockGlazedTerracottaBlock(Block pick) {
        super(Properties.from(pick));
        this.pick = pick;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(pick);
    }
}
