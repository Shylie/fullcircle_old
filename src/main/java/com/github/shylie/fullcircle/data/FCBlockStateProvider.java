package com.github.shylie.fullcircle.data;

import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.github.shylie.fullcircle.FCBlocks;
import com.github.shylie.fullcircle.FullCircle;

import net.minecraft.data.DataGenerator;

public class FCBlockStateProvider extends BlockStateProvider {
	public FCBlockStateProvider(DataGenerator gen, ExistingFileHelper efh) {
		super(gen, FullCircle.MOD_ID, efh);
	}

	@Override
	protected void registerStatesAndModels() {
		horizontalBlock(FCBlocks.WHITE_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.WHITE_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/white_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.ORANGE_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.ORANGE_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/orange_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/light_blue_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.YELLOW_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.YELLOW_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/yellow_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.PINK_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.PINK_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/pink_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.GRAY_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.GRAY_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/gray_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/light_gray_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.CYAN_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.CYAN_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/cyan_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.PURPLE_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.PURPLE_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/purple_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.BLUE_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.BLUE_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/blue_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.BROWN_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.BROWN_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/brown_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.GREEN_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.GREEN_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/green_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.BLACK_GLAZED_TERRACOTTA_DEBUG.get(), models().withExistingParent(FCBlocks.BLACK_GLAZED_TERRACOTTA_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/black_glazed_terracotta")), 0);

		horizontalBlock(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get(), models().withExistingParent(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/red_glazed_terracotta")), 0);
		horizontalBlock(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN_DEBUG.get(), models().withExistingParent(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN_DEBUG.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/red_glazed_terracotta")), 0);
	}

	@Override
	public String getName() {
		return FullCircle.MOD_ID + "_blockStates";
	}
}
