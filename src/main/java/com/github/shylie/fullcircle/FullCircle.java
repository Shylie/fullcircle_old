package com.github.shylie.fullcircle;

import com.github.shylie.fullcircle.net.MessageAdditiveMotion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FullCircle.MOD_ID)
public class FullCircle {
    public static final String MOD_ID = "fullcircle";
    private static final Logger LOGGER = LogManager.getLogger();

    public FullCircle() {
        LOGGER.debug("We've come full circle!");
        FCPacketHandler.INSTANCE.registerMessage(FCPacketHandler.id++, MessageAdditiveMotion.class, MessageAdditiveMotion::encode, MessageAdditiveMotion::decode, MessageAdditiveMotion::handle);

        FCBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FCTileEntityTypes.TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}