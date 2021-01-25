package com.github.shylie.fullcircle;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.registries.ForgeRegistries;

public class FCBlockLootTables extends BlockLootTables {
    @Override
    protected void addTables() {
        registerDropping(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get(), Blocks.RED_GLAZED_TERRACOTTA);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return StreamSupport.stream(ForgeRegistries.BLOCKS.spliterator(), false)
            .filter(entry -> entry.getRegistryName() != null && entry.getRegistryName().getNamespace().equals(FullCircle.MOD_ID))
            .collect(Collectors.toSet());
    }
}
