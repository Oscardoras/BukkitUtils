package org.bukkitutils.command.v1_14_3_V1;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Represents a custom argument for a Mojang Brigadier command. */
public abstract class CustomArgument<T> extends Argument<String> {
	
	/** Represents a custom argument for a Mojang Brigadier command. */
	public CustomArgument() {
		super(StringArgumentType.word());
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
	
	
	/** An argument error. */
	public static class CustomArgumentException extends CommandSyntaxException {
		
		private static final long serialVersionUID = -8129872152836820740L;
		
		
		/**
		 * An argument error.
		 * @param message the message to send.
		 */
		public CustomArgumentException(String message) {
			super(new CommandExceptionType() {}, new Message() {
				public String getString() {
					return ChatColor.RED + message;
				}
			});
		}
		
		/**
		 * An argument error.
		 * @param message the message to send.
		 */
		public CustomArgumentException(BaseComponent... message) {
			super(new CommandExceptionType() {}, new Message() {
				public String getString() {
					for (BaseComponent msg : message) msg.setColor(ChatColor.RED);
					return ComponentSerializer.toString(message);
				}
			});
		}
		
	}
	
}