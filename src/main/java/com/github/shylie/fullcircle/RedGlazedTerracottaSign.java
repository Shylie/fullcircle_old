package com.github.shylie.fullcircle;

import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;

public class RedGlazedTerracottaSign extends GlazedTerracottaBlock {
    RedGlazedTerracottaSign(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(Items.RED_GLAZED_TERRACOTTA);
    };

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RedGlazedTerracottaSignTileEntity();
    }
}