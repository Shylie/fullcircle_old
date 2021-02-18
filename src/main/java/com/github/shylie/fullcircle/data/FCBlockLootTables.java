package com.github.shylie.fullcircle.data;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.shylie.fullcircle.FCBlocks;
import com.github.shylie.fullcircle.FullCircle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.registries.ForgeRegistries;

public class FCBlockLootTables extends BlockLootTables {
	@Override
	protected void addTables() {
		registerDropping(FCBlocks.WHITE_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.WHITE_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.ORANGE_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.ORANGE_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.YELLOW_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.YELLOW_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.PINK_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.PINK_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.GRAY_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.GRAY_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.CYAN_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.CYAN_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.PURPLE_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.PURPLE_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.BLUE_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.BLUE_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.BROWN_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.BROWN_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.GREEN_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.GREEN_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.BLACK_GLAZED_TERRACOTTA_DEBUG.get(), Blocks.BLACK_GLAZED_TERRACOTTA);

		registerDropping(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get(), Blocks.RED_GLAZED_TERRACOTTA);
		registerDropping(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN_DEBUG.get(), Blocks.RED_GLAZED_TERRACOTTA);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return StreamSupport.stream(ForgeRegistries.BLOCKS.spliterator(), false)
			.filter(entry -> entry.getRegistryName() != null && entry.getRegistryName().getNamespace().equals(FullCircle.MOD_ID))
			.collect(Collectors.toSet());
	}
}
