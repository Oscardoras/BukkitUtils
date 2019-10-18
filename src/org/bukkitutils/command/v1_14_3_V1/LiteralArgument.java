package org.bukkitutils.command.v1_14_3_V1;

import com.mojang.brigadier.context.CommandContext;
import com.sun.istack.internal.NotNull;

/** Represents a literal argument for a Mojang Brigadier command. */
public class LiteralArgument extends Argument<Object> {

	protected final String literal;
	
	/**
	 * Represents a literal argument for a Mojang Brigadier command.
	 * @param literal the name of the literal argument
	 */
	public LiteralArgument(@NotNull final String literal) {
		super(null);
		if(literal == null || literal.isEmpty()) throw new IllegalArgumentException("Cannot create a LiteralArgument with an empty string");
		this.literal = literal;
	}
	
	/**
	 * Gets the name of the literal argument.
	 * @return the name of the literal argument
	 */
	public @NotNull String getLiteral() {
		return literal;
	}

	@Override
	protected Object parse(String key, CommandContext<?> context) {
		return null;
	}
	
}