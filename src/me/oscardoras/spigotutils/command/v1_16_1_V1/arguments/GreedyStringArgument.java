package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

/** Represents a greedy string argument for a Mojang Brigadier command. */
public class GreedyStringArgument extends Argument<String> {
	
	/** Represents a greedy string argument for a Mojang Brigadier command. */
	public GreedyStringArgument() {
		super(StringArgumentType.greedyString());
	}
	
	@Override
	protected String parse(String key, CommandContext<?> context) throws Exception {
		return context.getArgument(key, String.class);
	}
	
}