package org.bukkitutils.command.v1_15_V1;

import java.lang.reflect.Method;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Represents a custom argument for a Mojang Brigadier command. */
public abstract class CustomArgument<T> extends Argument<String> {
	
	/** Represents a custom argument for a Mojang Brigadier command. */
	public CustomArgument() {
		super(StringArgumentType.string());
	}
	
	/**
	 * The argument parser.
	 * @param arg the string argument written by the command sender
	 * @param cmd the data for the argument parser
	 * @throws CommandSyntaxException if the command is malformed
	 * @throws Exception if another exception occurs
	 */
	protected abstract @Nullable T parse(@NotNull String arg, @NotNull SuggestedCommand cmd) throws Exception;
	
	@Override
	protected final String parse(String key, CommandContext<?> context) {
		return context.getArgument(key, String.class);
	}
	
	
	/**
	 * Returns a custom CommandSyntaxException.
	 * @param message the string message
	 * @return the custom CommandSyntaxException
	 */
	public static CommandSyntaxException getCustomException(@NotNull String message) {
		return new SimpleCommandExceptionType(new Message() {
			public String getString() {
				return message;
			}
		}).create();
	}
	
	/**
	 * Returns a custom CommandSyntaxException.
	 * @param message the base component message
	 * @return the custom CommandSyntaxException
	 */
	public static CommandSyntaxException getCustomException(@NotNull BaseComponent[] message) {
		try {
			Class<?> IChatBaseComponent = Reflector.getNmsClass("IChatBaseComponent");
			Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
			Method a = Reflector.getMethod(ChatSerializer, "a", String.class);
			Message msg = (Message) a.invoke(null, ComponentSerializer.toString(message));
			return new SimpleCommandExceptionType(msg).create();
		} catch (Exception e) {
			e.printStackTrace();
			return getCustomException(ComponentSerializer.toString(message));
		}
	}
	
}