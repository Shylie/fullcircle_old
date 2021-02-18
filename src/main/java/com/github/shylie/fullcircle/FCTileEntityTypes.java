package com.github.shylie.fullcircle;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FCTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, FullCircle.MOD_ID);

	public static final RegistryObject<TileEntityType<RedGlazedTerracottaSignTileEntity>> RED_GLAZED_TERRACOTTA_SIGN = TILE_ENTITY_TYPES.register("red_glazed_terracotta_sign", () -> TileEntityType.Builder.create(RedGlazedTerracottaSignTileEntity::new, FCBlocks.RED_GLAZED_TERRACOTTA_SIGN.get()).build(null));
}
