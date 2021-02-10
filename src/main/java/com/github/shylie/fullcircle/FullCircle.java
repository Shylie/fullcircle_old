package com.github.shylie.fullcircle;

import com.github.shylie.fullcircle.net.MessageAdditiveMotion;
import com.github.shylie.fullcircle.net.MessageLinkSpellLog;
import com.github.shylie.fullcircle.net.MessageWriteContent;
import com.github.shylie.fullcircle.proxy.ClientProxy;
import com.github.shylie.fullcircle.proxy.CommonProxy;
import com.github.shylie.fullcircle.proxy.ServerProxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FullCircle.MOD_ID)
public class FullCircle {
    public static final String MOD_ID = "fullcircle";

    public static final String FC_SPELL_DEBUG_FILENAME = "fcdebug.txt";

    private static final Logger LOGGER = LogManager.getLogger();

    public FullCircle() {
        LOGGER.debug("We've come full circle!");
        FCPacketHandler.INSTANCE.registerMessage(FCPacketHandler.id++, MessageAdditiveMotion.class, MessageAdditiveMotion::encode, MessageAdditiveMotion::new, MessageAdditiveMotion::recieve);
        FCPacketHandler.INSTANCE.registerMessage(FCPacketHandler.id++, MessageLinkSpellLog.class, MessageLinkSpellLog::encode, MessageLinkSpellLog::new, MessageLinkSpellLog::recieve);
        FCPacketHandler.INSTANCE.registerMessage(FCPacketHandler.id++, MessageWriteContent.class, MessageWriteContent::encode, MessageWriteContent::new, MessageWriteContent::recieve);

        FCBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FCTileEntityTypes.TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        CommonProxy.PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    }
}