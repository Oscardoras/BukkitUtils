package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

/** Represents a string argument for a Mojang Brigadier command. */
public class StringArgument extends Argument<String> {
	
	/** Represents a string argument for a Mojang Brigadier command. */
	public StringArgument() {
		super(StringArgumentType.word());
	}
	
	@Override
	protected String parse(String key, CommandContext<?> context) {
		return context.getArgument(key, String.class);
	}
	
}