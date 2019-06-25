package org.bukkitutils.command.v1_14_3_V1;

import java.util.Collection;
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
import org.bukkitutils.io.TranslatableMessage;

/** Contains methods to broadcast command messages */
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
	
	
	/**
	 * Broadcasts a command translatable message if it have to.
	 * @param sender the CommandSender who performed this command
	 * @param message the TranslatableMessage object representing the translatable message to broadcast
	 * @param args the arguments for the translatable message
	 */
	public static void send(CommandSender sender, TranslatableMessage message, String... args) {
		World world = Bukkit.getWorlds().get(0);
		if (sender instanceof ProxiedCommandSender) {
			CommandSender caller = ((ProxiedCommandSender) sender).getCaller();
			if (caller instanceof BlockCommandSender) {
				sender.sendMessage(message.getMessage(sender, args));
	    		if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
	    			broadcastToConsole(sender, world, message, args);
	    			broadcastToOps(sender, world, message, args);
	    		}
			} else if (caller instanceof Entity) {
				String msg = message.getMessage(sender, args);
				if (msg.startsWith(ChatColor.RED + "") || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) sender.sendMessage(msg);
				broadcastToConsole(sender, world, message, args);
				broadcastToOps(sender, world, message, args);
			} else if (caller instanceof ConsoleCommandSender) {
				if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
					sender.sendMessage(ChatColor.stripColor(message.getMessage(sender, args)));
				    if (broadcastConsoleToOps) broadcastToOps(sender, world, message, args);
				}
			} else if (caller instanceof RemoteConsoleCommandSender) {
				sender.sendMessage(message.getMessage(sender, args));
				broadcastToConsole(sender, world, message, args);
				if (broadcastRconToOps) broadcastToOps(sender, world, message, args);
			}
		} else if (sender instanceof BlockCommandSender) {
    		sender.sendMessage(message.getMessage(sender, args));
    		if (world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
    			broadcastToConsole(sender, world, message, args);
    			broadcastToOps(sender, world, message, args);
    		}
		} else if (sender instanceof Entity) {
			String msg = message.getMessage(sender, args);
			if (msg.startsWith(ChatColor.RED + "") || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) sender.sendMessage(msg);
			broadcastToConsole(sender, world, message, args);
			broadcastToOps(sender, world, message, args);
		} else if (sender instanceof ConsoleCommandSender) {
		    sender.sendMessage(ChatColor.stripColor(message.getMessage(sender, args)));
		    if (broadcastConsoleToOps) broadcastToOps(sender, world, message, args);
		} else if (sender instanceof RemoteConsoleCommandSender) {
			sender.sendMessage(message.getMessage(sender, args));
			broadcastToConsole(sender, world, message, args);
			if (broadcastRconToOps) broadcastToOps(sender, world, message, args);
		}
	}
	
	private static void broadcastToConsole(CommandSender sender, World world, TranslatableMessage message, String... args) {
		if (!(sender instanceof ConsoleCommandSender) && world.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) {
			CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
			CommandSender console = Bukkit.getConsoleSender();
			String msg = message.getMessage(console, args);
			if (!msg.startsWith(ChatColor.RED + "")) console.sendMessage(ChatColor.stripColor("[" + callee.getName() + ": " + msg + "]"));
		}
	}
	
	private static void broadcastToOps(CommandSender sender, World world, TranslatableMessage message, String... args) {
		CommandSender caller = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCaller() : sender;
		CommandSender callee = sender instanceof ProxiedCommandSender ? ((ProxiedCommandSender) sender).getCallee() : sender;
		if (world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (other.hasPermission("bukkit.broadcast.scradmin") && !other.equals(caller)) {
					String msg = message.getMessage(other, args);
					if (!msg.startsWith(ChatColor.RED + "")) other.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + callee.getName() + ": " + msg + "]");
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
		if (!list.isEmpty()) {
			if (listMessage != null) sender.sendMessage(listMessage.getMessage(sender, args).replaceAll("%list%", ""+list.size()));
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
		} else sender.sendMessage(emptyMessage.getMessage(sender, args));
	}
	
}