package org.bukkitutils.command.v1_15_V1.arguments;

import org.bukkitutils.command.v1_15_V1.Argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

/** Represents a quoted string argument for a Mojang Brigadier command. */
public class QuotedStringArgument extends Argument<String> {
	
	/** Represents a quoted string argument for a Mojang Brigadier command. */
	public QuotedStringArgument() {
		super(StringArgumentType.string());
	}
	
	@Override
	protected String parse(String key, CommandContext<?> context) {
		return context.getArgument(key, String.class);
	}
	
}