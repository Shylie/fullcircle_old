package com.github.shylie.fullcircle.command;

import java.util.UUID;

import com.github.shylie.fullcircle.VMManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class StopSpellsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("fcstopspells").executes(StopSpellsCommand::stopSpells));
    }

    private static int stopSpells(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        UUID uuid = commandContext.getSource().asPlayer().getUniqueID();
        VMManager.MANAGER.removeIf(vm -> vm.getCaster().getUniqueID().equals(uuid));
        return 1;
    }
}
