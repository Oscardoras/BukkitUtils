package org.bukkitutils.command.v1_14_3_V1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_14_3_V1.Argument.SuggestedCommand;
import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/** Registers and unregisters Mojang Brigadier commands */
public final class CommandRegister {
	private CommandRegister() {}
	
	
	@FunctionalInterface
	public static interface CommandRunnable {
		/**
		 * The code to run when this command is performed.
		 * @param cmd the data for the command
		 * @return the result of the command
		 * @throws CommandSyntaxException if the command is malformed
		 */
		int run(PerformedCommand cmd) throws CommandSyntaxException;
	}
	
	/** The type required for the command executor */
	public static enum CommandExecutorType {
		/** Represents a player command executor. */
		PLAYER,
		
		/** Represents an entity command executor. */
		ENTITY,
		
		/** Represents any command executor. */
		ALL;
	}
	
	public static class PerformedCommand extends SuggestedCommand {
		
		protected PerformedCommand(Object source, CommandSender sender, CommandSender executor, Location location, Object[] args) {
			super(source, sender, executor, location, args);
		}
		
		/**
		 * Sends a command message.
		 * @param message the base component to send
		 */
		public void sendMessage(BaseComponent message) {
			Reflector.sendMessage(source, message, false);

		}
		
		/**
		 * Sends a command message.
		 * @param message the translatable message to send
		 * @param args the arguments for the translatable message
		 */
		public void sendMessage(TranslatableMessage message, String... args) {
			Reflector.sendMessage(source, new TextComponent(message.getMessage(sender, args)), false);
		}
		
		/**
		 * Sends a broadcast command message.
		 * @param message the base component to send
		 */
		public void broadcastMessage(BaseComponent message) {
			Reflector.sendMessage(source, message, true);

		}
		
		/**
		 * Sends a broadcast command message.
		 * @param message the translatable message to send
		 * @param args the arguments for the translatable message
		 */
		public void broadcastMessage(TranslatableMessage message, String... args) {
			Reflector.broadcastMessage(source, sender, message, args);
		}
		
		/**
		 * Sends a failure command message.
		 * @param message the base component to send
		 */
		public void sendFailureMessage(BaseComponent message) {
			Reflector.sendFailureMessage(source, message);
		}
		
		/**
		 * Sends a failure command message.
		 * @param message the translatable message to send
		 * @param args the arguments for the translatable message
		 */
		public void sendFailureMessage(TranslatableMessage message, String... args) {
			Reflector.sendFailureMessage(source, new TextComponent(message.getMessage(sender, args)));
		}
		
		/**
		 * Sends a list command message.
		 * @param list the list to send
		 * @param listMessage the translatable message to send if the list is not empty
		 * @param emptyMessage the translatable message to send if the list is empty
		 * @param args the arguments for the translatable message
		 */
		public void sendListMessage(Collection<String> list, TranslatableMessage listMessage, TranslatableMessage emptyMessage, String... args) {
			List<String> messages = new ArrayList<String>();
			if (!list.isEmpty()) {
				if (listMessage != null) messages.add(listMessage.getMessage(sender, args).replaceAll("%list%", ""+list.size()));
				if (!list.isEmpty()) {
					String string = "";
					int i = 0;
					for (String element : list) {
						if (i == 0) {
							string += element;
						} else {
							string += ", " + element;
						}
						i++;
					}
					messages.add(string);
				}
			} else messages.add(emptyMessage.getMessage(sender, args));
			
			for (String message : messages) Reflector.sendMessage(source, new TextComponent(message), false);
		}
		
	}
	
	
	/**
	 * Unregisters a command.
	 * @param name the name of the command
	 * @param force if forcing the unregistration
	 */
	public static void unregister(String name) {
		Reflector.unregister(name);
	}
	
	/**
	 * Registers a command.
	 * @param name the name of the command
	 * @param arguments the map of the arguments
	 * @param permission the permission required to perform the command
	 * @param executorType the type required for the command executor
	 * @param runnable the CommandRunnable to run when this command is performed
	 * @param aliases the aliases for the command
	 */
	public static void register(String name, LinkedHashMap<String, Argument<?>> arguments, Permission permission, CommandExecutorType executorType, CommandRunnable runnable, String... aliases) {
		Reflector.register(name, arguments, permission, executorType, runnable, aliases);
	}
	
}
