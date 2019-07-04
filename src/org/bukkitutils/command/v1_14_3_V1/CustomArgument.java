package org.bukkitutils.command.v1_14_3_V1;

import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/** Represents a custom argument for a Mojang Brigadier command */
public abstract class CustomArgument<T> extends Argument<String> {
	
	private final TranslatableMessage error;
	
	/**
	 * Represents a custom argument for a Mojang Brigadier command
	 * @param error an error message to send if getArg returns null. A null value will send an unknown error message
	 */
	public CustomArgument(TranslatableMessage error) {
		super(StringArgumentType.word());
		this.error = error;
	}
	
	/**
	 * Gets the translatable message to send if getArg returns null
	 * @return the translatable message, null if not set
	 */
	public final TranslatableMessage getMessage() {
		return error;
	}
	
	/**
	 * The argument parser.
	 * @param arg the string argument written by the command sender
	 * @param cmd the data for the argument parser
	 * @throws CommandSyntaxException if the command is malformed
	 * @throws Exception if an exception occurs
	 */
	protected abstract T parse(String arg, SuggestedCommand cmd) throws Exception;
	
	@Override
	protected final String parse(String key, CommandContext<?> context) {
		return context.getArgument(key, String.class);
	}
	
}