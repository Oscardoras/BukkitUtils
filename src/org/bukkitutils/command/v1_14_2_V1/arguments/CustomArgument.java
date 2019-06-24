package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/** Represents a custom argument for a Mojang Brigadier command */
public abstract class CustomArgument<T> extends Argument<String> {
	
	private final TranslatableMessage error;
	
	/**
	 * Represents a custom argument for a Mojang Brigadier command
	 * @param error an error to send if getArg returns null. A null value will send a unknow argument error message
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
	
	@Override
	public abstract Collection<String> getSuggestions(CommandSender executor, Location location, Object[] args);
	
	/**
	 * The argument parser.
	 * @param arg the string argument written by the command sender
	 * @param executor the CommandSender object representing the executor of this command
	 * @param location the location where this command is performed
	 * @throws CommandSyntaxException if the command is malformed
	 * @throws Exception if an exception occure
	 */
	public abstract T getArg(String arg, CommandSender executor, Location location) throws Exception;
	
	@Override
	public final String getArg(String key, CommandContext<?> context, CommandSender executor, Location location) {
		return context.getArgument(key, String.class);
	}
	
}