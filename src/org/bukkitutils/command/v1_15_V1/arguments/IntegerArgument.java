package org.bukkitutils.command.v1_15_V1.arguments;

import org.bukkitutils.command.v1_15_V1.Argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

/** Represents an integer argument for a Mojang Brigadier command. */
public class IntegerArgument extends Argument<Integer> {
	
	/** Represents an integer argument for a Mojang Brigadier command. */
	public IntegerArgument() {
		super(IntegerArgumentType.integer());
	}
	
	/**
	 * Represents an integer argument with a min value for a Mojang Brigadier command.
	 * @param min the min value
	 */
	public IntegerArgument(int min) {
		super(IntegerArgumentType.integer(min));
	}
	
	/**
	 * Represents an integer argument with a min and a max value for a Mojang Brigadier command.
	 * @param min the min value
	 * @param max the max value
	 */
	public IntegerArgument(int min, int max) {
		super(IntegerArgumentType.integer(min, max));
	}
	
	@Override
	protected Integer parse(String key, CommandContext<?> context) throws Exception {
		return context.getArgument(key, int.class);
	}
	
}