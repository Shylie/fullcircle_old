package com.github.shylie.fullcircle.net;

import java.util.function.Supplier;

import com.github.shylie.fullcircle.proxy.CommonProxy;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageWriteContent {
	private String content;
	private int index;
	private int total;
	private int vmHash;

	public MessageWriteContent(String content, int index, int total, int vmHash) {
		this.content = content;
		this.index = index;
		this.total = total;
		this.vmHash = vmHash;
	}

	public MessageWriteContent(PacketBuffer buffer) {
		content = buffer.readString();
		index = buffer.readVarInt();
		total = buffer.readVarInt();
		vmHash = buffer.readVarInt();
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeString(content);
		buffer.writeVarInt(index);
		buffer.writeVarInt(total);
		buffer.writeVarInt(vmHash);
	}

	public boolean recieve(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			CommonProxy.PROXY.writeToFCDebugFile(content, index, total, vmHash);
		});
		context.get().setPacketHandled(true);
		return true;
	}
}
