package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;

/** Represents a double argument for a Mojang Brigadier command. */
public class DoubleArgument extends Argument<Double> {
	
	/** Represents a double argument for a Mojang Brigadier command. */
	public DoubleArgument() {
		super(DoubleArgumentType.doubleArg());
	}
	
	/**
	 * Represents a double argument with a min value for a Mojang Brigadier command.
	 * @param min the min value
	 */
	public DoubleArgument(int min) {
		super(DoubleArgumentType.doubleArg(min));
	}
	
	/**
	 * Represents a double argument with a min and a max value for a Mojang Brigadier command.
	 * @param min the min value
	 * @param max the max value
	 */
	public DoubleArgument(int min, int max) {
		super(DoubleArgumentType.doubleArg(min, max));
	}
	
	@Override
	protected Double parse(String key, CommandContext<?> context) {
		return context.getArgument(key, double.class);
	}
	
}