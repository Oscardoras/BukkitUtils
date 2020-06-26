package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

/** Represents a boolean argument for a Mojang Brigadier command. */
public class BooleanArgument extends Argument<Boolean> {
	
	/** Represents a boolean argument for a Mojang Brigadier command. */
	public BooleanArgument() {
		super(BoolArgumentType.bool());
	}
	
	@Override
	protected Boolean parse(String key, CommandContext<?> context) {
		return context.getArgument(key, boolean.class);
	}
	
}