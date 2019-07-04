package org.bukkitutils.io;

import java.util.Map;
import java.util.Properties;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;
import org.bukkitutils.BukkitPlugin;

/** Represents a translatable message */
public class TranslatableMessage {
	
	protected final BukkitPlugin plugin;
	protected final String path;
	
	/**
	 * Represents a translatable message
	 * @param plugin the plugin containing the translation files
	 * @param path the path of the translatable message in translation files
	 */
	public TranslatableMessage(BukkitPlugin plugin, String path) {
		this.plugin = plugin;
		this.path = path.toLowerCase();
	}
	
	/**
	 * Gets the plugin containing the translation files
	 * @return the plugin containing the translation files
	 */
	public BukkitPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the path of the translatable message in translation files
	 * @return the path of the translatable message in translation files
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the translated message
	 * @param language the language to translate the message
	 * @param args the arguments for the translatable message
	 * @return the String translated message
	 */
	public String getMessage(String language, String... args) {
		String msg = getMessage(language, path, plugin.getTranslations());
		for (String arg : args) msg = msg.replaceFirst("%arg%", arg);
		return msg;
	}
	
	/**
	 * Gets the translated message
	 * @param sender the sender to translate the message
	 * @param args the arguments for the translatable message
	 * @return the String translated message
	 */
	public String getMessage(CommandSender sender, String... args) {
		return getMessage(getLanguage(sender), args);
	}
	
	protected String getMessage(String language, String path, Map<String, Properties> translates) {
		path = path.toLowerCase();
		if (translates.containsKey(language)) {
			Properties properties = translates.get(language);
			if (properties.containsKey(path)) return properties.getProperty(path);
		}
		if (translates.containsKey("en")) {
			Properties properties = translates.get("en");
			if (properties.containsKey(path)) return properties.getProperty(path);
		}
		for (String name : translates.keySet()) if (!name.equals(language) && !name.equals("en")) {
			Properties properties = translates.get(name);
			if (properties.containsKey(path)) return properties.getProperty(path);
		}
		throw new TranslatableMessageException(path);
	}
	
	public static String getLanguage(CommandSender sender) {
		if (sender instanceof ProxiedCommandSender) sender = ((ProxiedCommandSender) sender).getCaller();
		if (sender instanceof Player) return ((Player) sender).getLocale().split("_")[0];
		else return "en";
	}
	
}