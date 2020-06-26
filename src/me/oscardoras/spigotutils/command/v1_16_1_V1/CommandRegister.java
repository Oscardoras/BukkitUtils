package me.oscardoras.spigotutils.command.v1_16_1_V1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument.SuggestedCommand;

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
		int run(PerformedCommand cmd) throws Exception;
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
		public void sendMessage(Object... message) {
			Reflector.sendMessage(source, sender, message, -1);

		}
		
		/**
		 * Broadcasts a command message.
		 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
		 */
		public void broadcastMessage(Object... message) {
			Reflector.broadcastMessage(source, sender, message);

		}
		
		/**
		 * Sends a failure message.
		 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
		 */
		public void sendFailureMessage(Object... message) {
			Reflector.sendFailureMessage(source, sender, message);
		}
		
		/**
		 * Sends a list message.
		 * @param list the list to send
		 * @param listMessage an array, which can contain BaseComponent, TranslatableMessage and any other type of object, to send if the list is not empty
		 * @param emptyMessage an array, which can contain BaseComponent, TranslatableMessage and any other type of object, to send if the list is empty
		 */
		public void sendListMessage(Collection<? extends Object> list, Object[] listMessage, Object[] emptyMessage) {
			if (!list.isEmpty()) {
				if (listMessage != null) Reflector.sendMessage(source, sender, listMessage, list.size());
				if (!list.isEmpty()) {
					List<Object> objects = new ArrayList<Object>();
					int i = 0;
					for (Object element : list) {
						if (i == 0) {
							objects.add(element);
						} else {
							objects.add(", ");
							objects.add(element);
						}
						i++;
					}
					Reflector.sendMessage(source, sender, objects.toArray(), -1);
				}
			} else Reflector.sendMessage(source, sender, new Object[] {emptyMessage}, -1);
		}
		
	}
	
	
	/**
	 * Unregisters a command.
	 * @param name the name of the command
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
