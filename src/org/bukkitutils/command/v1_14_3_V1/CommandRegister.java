package org.bukkitutils.command.v1_14_3_V1;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_14_3_V1.Argument.SuggestedCommand;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sun.istack.internal.NotNull;

/** Registers and unregisters Mojang Brigadier commands. */
public final class CommandRegister {
	private CommandRegister() {}
	
	
	@FunctionalInterface
	public static interface CommandRunnable {
		/**
		 * The code to run when this command is performed.
		 * @param cmd the data for the command
		 * @return the result of the command
		 * @throws CommandSyntaxException if the command is malformed
		 * @throws Exception if another exception occurs
		 */
		int run(@NotNull PerformedCommand cmd) throws Exception;
	}
	
	/** The type required for the command executor. */
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
		 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
		 */
		public void sendMessage(@NotNull Object... message) {
			Reflector.sendMessage(source, sender, message, -1);

		}
		
		/**
		 * Broadcasts a command message.
		 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
		 */
		public void broadcastMessage(@NotNull Object... message) {
			Reflector.broadcastMessage(source, sender, message);

		}
		
		/**
		 * Sends a failure message.
		 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
		 */
		public void sendFailureMessage(@NotNull Object... message) {
			Reflector.sendFailureMessage(source, sender, message);
		}
		
		/**
		 * Sends a list message.
		 * @param list the list to send
		 * @param listMessage an array, which can contain BaseComponent, TranslatableMessage and any other type of object, to send if the list is not empty
		 * @param emptyMessage an array, which can contain BaseComponent, TranslatableMessage and any other type of object, to send if the list is empty
		 */
		public void sendListMessage(@NotNull Collection<String> list, @NotNull Object[] listMessage, @NotNull Object[] emptyMessage) {
			if (!list.isEmpty()) {
				if (listMessage != null) Reflector.sendMessage(source, sender, listMessage, list.size());
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
					Reflector.sendMessage(source, sender, new Object[] {string}, -1);
				}
			} else Reflector.sendMessage(source, sender, new Object[] {emptyMessage}, -1);
		}
		
	}
	
	
	/**
	 * Unregisters a command.
	 * @param name the name of the command
	 */
	public static void unregister(@NotNull String name) {
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
	public static void register(@NotNull String name, @NotNull LinkedHashMap<String, Argument<?>> arguments, @NotNull Permission permission, @NotNull CommandExecutorType executorType, @NotNull CommandRunnable runnable, @NotNull String... aliases) {
		Reflector.register(name, arguments, permission, executorType, runnable, aliases);
	}
	
}
