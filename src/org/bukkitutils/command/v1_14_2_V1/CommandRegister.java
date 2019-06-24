package org.bukkitutils.command.v1_14_2_V1;

import java.util.LinkedHashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_14_2_V1.arguments.Argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

/** Registers and unregisters Mojang Brigadier commands */
public final class CommandRegister {
	private CommandRegister() {}
	
	
	@FunctionalInterface
	public static interface CommandRunnable {
		/**
		 * The code to run when this command is performed.
		 * @param sender the CommandSender object representing the sender of this command
		 * @param executor the CommandSender object representing the executor of this command
		 * @param location the location where this command is performed
		 * @param args the arguments given to this command
		 * @throws CommandSyntaxException if the command is malformed
		 */
		int run(CommandSender sender, CommandSender executor, Location location, Object[] args) throws CommandSyntaxException;
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
