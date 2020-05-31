package org.bukkitutils.command.v1_15_V1.arguments;

import org.bukkitutils.command.v1_15_V1.Argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

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