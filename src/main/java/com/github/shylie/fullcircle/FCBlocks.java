package com.github.shylie.fullcircle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FullCircle.MOD_ID);

    public static final RegistryObject<Block> RED_GLAZED_TERRACOTTA_SIGN = BLOCKS.register("red_glazed_terracotta_sign", () -> new RedGlazedTerracottaSign(Block.Properties.from(Blocks.RED_GLAZED_TERRACOTTA)));
}
