package com.github.shylie.fullcircle.command;

import com.github.shylie.fullcircle.FCPacketHandler;
import com.github.shylie.fullcircle.net.MessageLinkSpellLog;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class LinkFCDebugFileCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("fcdebugfile").executes(LinkFCDebugFileCommand::sendLinkFileMessage));
    }

    private static int sendLinkFileMessage(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().asPlayer();
        FCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageLinkSpellLog());
        return 1;
    }
}
