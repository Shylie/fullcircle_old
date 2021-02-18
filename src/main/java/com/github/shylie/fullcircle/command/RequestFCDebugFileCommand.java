package com.github.shylie.fullcircle.command;

import com.github.shylie.fullcircle.VMManager;
import com.github.shylie.fullcircle.proxy.CommonProxy;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class RequestFCDebugFileCommand {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("fcrequestdebugfile").executes(RequestFCDebugFileCommand::requestDebugFile));
	}

	private static int requestDebugFile(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
		VMManager.MANAGER.request(commandContext.getSource().asPlayer().getUniqueID());
		CommonProxy.PROXY.addToChat(new StringTextComponent("Requested debug file"));
		return 1;
	}
}
