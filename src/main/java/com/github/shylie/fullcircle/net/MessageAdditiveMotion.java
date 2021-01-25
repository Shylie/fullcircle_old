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

    public static void encode(MessageAdditiveMotion message, PacketBuffer buffer) {
        buffer.writeInt(message.eid);
        buffer.writeDouble(message.dx);
        buffer.writeDouble(message.dy);
        buffer.writeDouble(message.dz);
    }

    public static MessageAdditiveMotion decode(PacketBuffer buffer) {
        int eid = buffer.readInt();
        double dx = buffer.readDouble();
        double dy = buffer.readDouble();
        double dz = buffer.readDouble();
        return new MessageAdditiveMotion(eid, dx, dy, dz);
    }

    public static void handle(MessageAdditiveMotion message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> message.execute());
        });
        context.get().setPacketHandled(true);
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