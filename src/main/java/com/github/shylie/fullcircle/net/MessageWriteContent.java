package com.github.shylie.fullcircle.net;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import com.github.shylie.fullcircle.proxy.CommonProxy;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageWriteContent {
    private String content;

    public MessageWriteContent(String content) {
        this.content = content;
    }

    public MessageWriteContent(PacketBuffer buffer) {
        content = buffer.readString();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeString(content);
    }

    public boolean recieve(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            try (FileWriter fw = new FileWriter(CommonProxy.PROXY.getFCDebugFile(), false)) {
                fw.write(content);
            }
            catch (IOException ioException) {

            }
            catch (NullPointerException npe) {
                
            }
        });
        context.get().setPacketHandled(true);
        return true;
    }
}
