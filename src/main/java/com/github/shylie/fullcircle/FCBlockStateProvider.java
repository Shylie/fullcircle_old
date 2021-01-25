package com.github.shylie.fullcircle;

import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraft.data.DataGenerator;

public class FCBlockStateProvider extends BlockStateProvider {
    public FCBlockStateProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, FullCircle.MOD_ID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get(), models().withExistingParent(FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get().getRegistryName().getPath(), mcLoc("template_glazed_terracotta")).texture("pattern", mcLoc("block/red_glazed_terracotta")), 0);
    }

    @Override
    public String getName() {
        return FullCircle.MOD_ID + "_blockStates";
    }
}
