package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

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