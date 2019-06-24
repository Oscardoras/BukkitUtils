package org.bukkitutils.command.v1_14_2_V1.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

/** Represents a boolean argument for a Mojang Brigadier command */
public class BooleanArgument extends Argument<Boolean> {
	
	/** Represents a boolean argument for a Mojang Brigadier command */
	public BooleanArgument() {
		super(BoolArgumentType.bool());
	}
	
	@Override
	public Boolean getArg(String key, CommandContext<?> context, CommandSender executor, Location location) {
		return context.getArgument(key, boolean.class);
	}
	
}