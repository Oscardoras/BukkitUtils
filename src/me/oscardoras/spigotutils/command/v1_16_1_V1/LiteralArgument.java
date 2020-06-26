package me.oscardoras.spigotutils.command.v1_16_1_V1;

import com.mojang.brigadier.context.CommandContext;

/** Represents a literal argument for a Mojang Brigadier command. */
public class LiteralArgument extends Argument<Object> {

	protected final String literal;
	
	/**
	 * Represents a literal argument for a Mojang Brigadier command.
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
	protected Object parse(String key, CommandContext<?> context) {
		return null;
	}
	
}