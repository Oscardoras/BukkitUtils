package org.bukkitutils.command.v1_14_2_V1.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

/** Represents a quoted string argument for a Mojang Brigadier command */
public class QuotedStringArgument extends Argument<String> {
	
	/** Represents a quoted string argument for a Mojang Brigadier command */
	public QuotedStringArgument() {
		super(StringArgumentType.string());
	}
	
	@Override
	public String getArg(String key, CommandContext<?> context, CommandSender executor, Location location) {
		return context.getArgument(key, String.class);
	}
	
}