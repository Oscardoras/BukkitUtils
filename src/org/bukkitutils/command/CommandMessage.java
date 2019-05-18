package org.bukkitutils.command;

import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkitutils.io.Message;
import org.bukkitutils.io.Translate;

public final class CommandMessage {
	private CommandMessage() {}
	
	private static boolean broadcastConsoleToOps = getSetting("broadcast-console-to-ops");
	private static boolean broadcastRconToOps = getSetting("broadcast-rcon-to-ops");
	
	private static boolean getSetting(String key) {
		Properties properties = new DataFile("server.properties").getProperties();
		try {
			if (properties.containsKey(key)) return Boolean.parseBoolean(properties.getProperty(key));
		} catch (IllegalArgumentException ex) {}
		return true;
	}
	
	
	public static void send(CommandSender sender, Message message, String... args) {
		World world = Bukkit.getWorlds().get(0);
		if (sender instanceof ProxiedCommandSender) {
			CommandSender caller = ((ProxiedCommandSender) sender).getCaller();
			if (caller instanceof BlockCommandSender) {
				sender.sendMessage(Translate.getPluginMessage(sender, message, args));
	    		if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
	    			broadcastToConsole(sender, world, message, args);
	    			broadcastToOps(sender, world, message, args);
	    		}
			} else if (caller instanceof Entity) {
				String msg = Translate.getPluginMessage(sender, message, args);
				if (msg.startsWith(ChatColor.RED + "") || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) sender.sendMessage(msg);
				broadcastToConsole(sender, world, message, args);
				broadcastToOps(sender, world, message, args);
			} else if (caller instanceof ConsoleCommandSender) {
				if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
					sender.sendMessage(ChatColor.stripColor(Translate.getPluginMessage(sender, message, args)));
				    if (broadcastConsoleToOps) broadcastToOps(sender, world, message, args);
				}
			} else if (caller instanceof RemoteConsoleCommandSender) {
				sender.sendMessage(Translate.getPluginMessage(sender, message, args));
				broadcastToConsole(sender, world, message, args);
				if (broadcastRconToOps) broadcastToOps(sender, world, message, args);
			}
		} else if (sender instanceof BlockCommandSender) {
    		sender.sendMessage(Translate.getPluginMessage(sender, message, args));
    		if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
    			broadcastToConsole(sender, world, message, args);
    			broadcastToOps(sender, world, message, args);
    		}
		} else if (sender instanceof Entity) {
			String msg = Translate.getPluginMessage(sender, message, args);
			if (msg.startsWith(ChatColor.RED + "") || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) sender.sendMessage(msg);
			broadcastToConsole(sender, world, message, args);
			broadcastToOps(sender, world, message, args);
		} else if (sender instanceof ConsoleCommandSender) {
		    sender.sendMessage(ChatColor.stripColor(Translate.getPluginMessage(sender, message, args)));
		    if (broadcastConsoleToOps) broadcastToOps(sender, world, message, args);
		} else if (sender instanceof RemoteConsoleCommandSender) {
			sender.sendMessage(Translate.getPluginMessage(sender, message, args));
			broadcastToConsole(sender, world, message, args);
			if (broadcastRconToOps) broadcastToOps(sender, world, message, args);
		}
	}
	
	private static void broadcastToConsole(CommandSender sender, World world, Message message, String... args) {
		if (!(sender instanceof ConsoleCommandSender) && world.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) {
			CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
			CommandSender console = Bukkit.getConsoleSender();
			String msg = Translate.getPluginMessage(console, message, args);
			if (!msg.startsWith(ChatColor.RED + "")) console.sendMessage(ChatColor.stripColor("[" + callee.getName() + ": " + msg + "]"));
		}
	}
	
	private static void broadcastToOps(CommandSender sender, World world, Message message, String... args) {
		CommandSender caller = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCaller() : sender;
		CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
		if (world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (other.hasPermission("bukkit.broadcast.scradmin") && !other.equals(caller)) {
					String msg = Translate.getPluginMessage(other, message, args);
					if (!msg.startsWith(ChatColor.RED + "")) other.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + callee.getName() + ": " + msg + "]");
				}
			}
	    }
	}
	
	public static void sendStringList(CommandSender sender, List<String> list, Message message, Message empty, String... args) {
		if (!list.isEmpty()) {
			if (message != null) sender.sendMessage(Translate.getPluginMessage(sender, message, args).replaceAll("%list%", ""+list.size()));
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
				sender.sendMessage(string);
			}
		} else sender.sendMessage(Translate.getPluginMessage(sender, empty, args));
	}
	
}