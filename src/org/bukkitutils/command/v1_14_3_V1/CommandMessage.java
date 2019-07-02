package org.bukkitutils.command.v1_14_3_V1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkitutils.io.DataFile;
import org.bukkitutils.io.TranslatableMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;

/** Contains methods to broadcast command messages */
public final class CommandMessage {
	private CommandMessage() {}
	
	
	private static final boolean broadcastConsoleToOps;
	private static final boolean broadcastRconToOps;
	
	static {
		boolean console = true;;
		boolean rcon = true;
		
		Properties properties = new DataFile("server.properties").getProperties();
		try {
			if (properties.containsKey("broadcast-console-to-ops")) console = Boolean.parseBoolean(properties.getProperty("broadcast-console-to-ops"));
		} catch (IllegalArgumentException e) {}
		try {
			if (properties.containsKey("broadcast-rcon-to-ops")) rcon = Boolean.parseBoolean(properties.getProperty("broadcast-rcon-to-ops"));
		} catch (IllegalArgumentException e) {}
		
		broadcastConsoleToOps = console;
		broadcastRconToOps = rcon;
	}
	
	
	/**
	 * Broadcasts a command translatable message if it have to.
	 * @param sender the CommandSender who performed this command
	 * @param broadcast if the message needs to be broadcast
	 * @param message the TranslatableMessage object representing the translatable message to broadcast
	 * @param args the arguments for the translatable message
	 */
	public static void send(CommandSender sender, boolean broadcast, TranslatableMessage message, String... args) {
		CommandSender caller = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCaller() : sender;
		World world = Bukkit.getWorlds().get(0);
		
		if (caller instanceof BlockCommandSender) {
			caller.sendMessage(message.getMessage(caller, args));
			if (broadcast)
    		if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
    			broadcastToConsole(caller, world, message, args);
    			broadcastToOps(caller, world, message, args);
    		}
		} else if (caller instanceof Entity) {
			String msg = message.getMessage(caller, args);
			if (!broadcast || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) caller.sendMessage(msg);
			if (broadcast) {
				broadcastToConsole(caller, world, message, args);
				broadcastToOps(caller, world, message, args);
			}
		} else if (caller instanceof ConsoleCommandSender) {
			if (!(sender instanceof ProxiedCommandSender) || world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
				caller.sendMessage(ChatColor.stripColor(message.getMessage(caller, args)));
				if (broadcast) if (broadcastConsoleToOps) broadcastToOps(caller, world, message, args);
			}
		} else if (caller instanceof RemoteConsoleCommandSender) {
			caller.sendMessage(message.getMessage(caller, args));
			if (broadcast) {
				broadcastToConsole(caller, world, message, args);
				if (broadcastRconToOps) broadcastToOps(caller, world, message, args);
			}
		}
	}
	
	private static void broadcastToConsole(CommandSender sender, World world, TranslatableMessage message, String... args) {
		if (!(sender instanceof ConsoleCommandSender) && world.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) {
			CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
			CommandSender console = Bukkit.getConsoleSender();
			String msg = message.getMessage(console, args);
			String name = callee instanceof ConsoleCommandSender ? "Server" : callee.getName();
			
			TranslatableComponent component = new TranslatableComponent("chat.type.admin");
			component.addWith(name);
			component.addWith(msg);
			component.setColor(ChatColor.GRAY);
			component.setItalic(true);
			
			console.spigot().sendMessage(component);
		}
	}
	
	private static void broadcastToOps(CommandSender sender, World world, TranslatableMessage message, String... args) {
		CommandSender caller = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCaller() : sender;
		CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
		String name = callee instanceof ConsoleCommandSender ? "Server" : callee.getName();
		if (world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (other.hasPermission("bukkit.broadcast.scradmin") && !other.equals(caller)) {
					String msg = message.getMessage(other, args);
					
					TranslatableComponent component = new TranslatableComponent("chat.type.admin");
					component.addWith(name);
					component.addWith(msg);
					component.setColor(ChatColor.GRAY);
					component.setItalic(true);
					
					other.spigot().sendMessage(component);
				}
			}
	    }
	}
	
	/**
	 * Sends a String list to the command sender.
	 * @param sender the CommandSender who performed this command
	 * @param the String list to send
	 * @param listMessage the translatable message to send if the list is not empty
	 * @param emptyMessage the translatable message to send if the list is empty
	 * @param args the arguments for the translatable messages
	 */
	public static void sendStringList(CommandSender sender, Collection<String> list, TranslatableMessage listMessage, TranslatableMessage emptyMessage, String... args) {
		CommandSender caller = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCaller() : sender;
		
		List<String> messages = new ArrayList<String>();
		if (!list.isEmpty()) {
			if (listMessage != null) messages.add(listMessage.getMessage(caller, args).replaceAll("%list%", ""+list.size()));
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
		} else messages.add(emptyMessage.getMessage(caller, args));
		
		
		World world = Bukkit.getWorlds().get(0);
		
		for (String message : messages) {
			if (caller instanceof BlockCommandSender) {
				caller.sendMessage(message);
			} else if (caller instanceof Entity) {
				if (world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) caller.sendMessage(message);
			} else if (caller instanceof ConsoleCommandSender) {
				if (!(sender instanceof ProxiedCommandSender) || world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) caller.sendMessage(ChatColor.stripColor(message));
			} else if (caller instanceof RemoteConsoleCommandSender) {
				caller.sendMessage(message);
			}
		}
	}
	
}