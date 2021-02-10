package com.github.shylie.fullcircle.blocks;

import com.github.shylie.fullcircle.RedGlazedTerracottaSignTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class RedGlazedTerracottaSign extends OtherPickBlockGlazedTerracottaBlock {
    public RedGlazedTerracottaSign() {
        super(Blocks.RED_GLAZED_TERRACOTTA);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RedGlazedTerracottaSignTileEntity();
    }
}