package org.bukkitutils.command.v1_14_3_V1.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.context.CommandContext;

/** Represents a literal argument for a Mojang Brigadier command */
public class LiteralArgument extends Argument<Object> {

	protected final String literal;
	
	/**
	 * Represents a literal argument for a Mojang Brigadier command
	 * @param literal the name of the literal argument
	 */
	public LiteralArgument(final String literal) {
		super(null);
		if(literal == null || literal.isEmpty()) throw new IllegalArgumentException("Cannot create a LiteralArgument with an empty string");
		this.literal = literal;
	}
	
	/**
	 * Gets the name of the literal argument.
	 * @return the name of the literal argument
	 */
	public String getLiteral() {
		return literal;
	}

	@Override
	public Object getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		return null;
	}
	
}