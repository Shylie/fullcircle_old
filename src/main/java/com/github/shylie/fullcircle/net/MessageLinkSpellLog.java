package com.github.shylie.fullcircle.net;

import java.io.File;
import java.util.function.Supplier;

import com.github.shylie.fullcircle.proxy.CommonProxy;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageLinkSpellLog {
    public MessageLinkSpellLog() {
    }

    public MessageLinkSpellLog(PacketBuffer buffer) {
    }

    public void encode(PacketBuffer buffer) {
    }

    public boolean recieve(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(MessageLinkSpellLog::linkSpellLog);
        context.get().setPacketHandled(true);
        return true;
    }

    public static void linkSpellLog() {
        ITextComponent textComponent = null;

        File fcdebuglog = CommonProxy.PROXY.getFCDebugFile();

        if (fcdebuglog == null || !fcdebuglog.exists()) {
            textComponent = new StringTextComponent("No existing spell debug log");
        }
        else {
            textComponent = (new StringTextComponent("Click to open fullcircle spell debug log").mergeStyle(TextFormatting.UNDERLINE).modifyStyle((style) -> {
                return style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, fcdebuglog.getAbsolutePath()));
            }));
        }

        CommonProxy.PROXY.addToChat(textComponent);
    }
}
