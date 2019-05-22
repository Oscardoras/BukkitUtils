package org.bukkitutils.command;

import java.util.LinkedHashMap;

import org.bukkit.permissions.Permission;
import org.bukkitutils.command.arguments.Argument;
import org.bukkitutils.command.arguments.GreedyStringArgument;
import org.bukkitutils.command.exceptions.GreedyStringException;
import org.bukkitutils.command.exceptions.InvalidCommandNameException;

public final class CommandAPI {
	private CommandAPI() {}
	
	
	public static enum CommandSenderType {
		PLAYER, ENTITY, ALL;
	}
	
	
	private static SemiReflector reflector;
	
	public static void initialize() {
		if(reflector == null)
			try {
				reflector = new SemiReflector();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	
	public static void unregister(String command) {
		reflector.unregister(command, false);
	}
	
	public static void unregister(String command, boolean force) {
		reflector.unregister(command, force);
	}
	
	
	public static void register(String commandName, LinkedHashMap<String, Argument> args, CommandExecutor executor, String... aliases) {
		register(commandName, args, null, CommandSenderType.ALL, executor, aliases);
	}
	
	public static void register(String commandName, LinkedHashMap<String, Argument> args, Permission permissions, CommandExecutor executor, String... aliases) {
		register(commandName, args, permissions, CommandSenderType.ALL, executor, aliases);
	}
	
	public static void register(String commandName, LinkedHashMap<String, Argument> args, CommandSenderType senderType, CommandExecutor executor, String... aliases) {
		register(commandName, args, null, senderType, executor, aliases);
	}
	
	public static void register(String commandName, LinkedHashMap<String, Argument> args, Permission permissions, CommandSenderType senderType, CommandExecutor executor, String... aliases) {
		try {
			
			//Sanitize commandNames
			if(commandName.length() == 0 || commandName == null) {
				throw new InvalidCommandNameException(commandName);
			}
			
			if(args == null) {
				args = new LinkedHashMap<>();
			}
			
			//Make a local copy of args to deal with
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Argument> copyOfArgs = (LinkedHashMap<String, Argument>) args.clone();
			
			//if args contains a GreedyString && args.getLast != GreedyString
			long numGreedyArgs = copyOfArgs.values().stream().filter(arg -> arg instanceof GreedyStringArgument).count();
			if(numGreedyArgs >= 1) {
				//A GreedyString has been found
				if(!(copyOfArgs.values().toArray(new Argument[copyOfArgs.size()])[copyOfArgs.size() - 1] instanceof GreedyStringArgument)) {
					throw new GreedyStringException();
				}
				
				if(numGreedyArgs > 1) {
					throw new GreedyStringException();
				}
			}
			reflector.register(commandName, permissions, senderType, aliases, copyOfArgs, executor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
