package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

/** Represents a float argument for a Mojang Brigadier command. */
public class FloatArgument extends Argument<Float> {
	
	/** Represents a float argument for a Mojang Brigadier command. */
	public FloatArgument() {
		super(FloatArgumentType.floatArg());
	}
	
	/**
	 * Represents a float argument with a min value for a Mojang Brigadier command.
	 * @param min the min value
	 */
	public FloatArgument(int min) {
		super(FloatArgumentType.floatArg(min));
	}
	
	/**
	 * Represents a float argument with a min and a max value for a Mojang Brigadier command.
	 * @param min the min value
	 * @param max the max value
	 */
	public FloatArgument(int min, int max) {
		super(FloatArgumentType.floatArg(min, max));
	}
	
	@Override
	protected Float parse(String key, CommandContext<?> context) throws Exception {
		return context.getArgument(key, float.class);
	}
	
}