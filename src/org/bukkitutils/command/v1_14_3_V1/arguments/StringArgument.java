package org.bukkitutils.command.v1_14_3_V1.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

/** Represents a string argument for a Mojang Brigadier command */
public class StringArgument extends Argument<String> {
	
	/** Represents a string argument for a Mojang Brigadier command */
	public StringArgument() {
		super(StringArgumentType.word());
	}
	
	@Override
	public String getArg(String key, CommandContext<?> context, CommandSender executor, Location location) {
		return context.getArgument(key, String.class);
	}
	
}