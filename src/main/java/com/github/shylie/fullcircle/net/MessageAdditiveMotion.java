package com.github.shylie.fullcircle.net;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageAdditiveMotion {
	private int eid;
	private double dx;
	private double dy;
	private double dz;

	public MessageAdditiveMotion(int eid, double dx, double dy, double dz) {
		this.eid = eid;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	public MessageAdditiveMotion(PacketBuffer buffer) {
		eid = buffer.readVarInt();
		dx = buffer.readDouble();
		dy = buffer.readDouble();
		dz = buffer.readDouble();
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(eid);
		buffer.writeDouble(dx);
		buffer.writeDouble(dy);
		buffer.writeDouble(dz);
	}

	public boolean recieve(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> execute());
		});
		context.get().setPacketHandled(true);
		return true;
	}

	private DistExecutor.SafeRunnable execute() {
		return new DistExecutor.SafeRunnable() {
			@Override
			public void run() {
				Minecraft.getInstance().world.getEntityByID(eid).addVelocity(dx, dy, dz);
			}
		};
	}
}