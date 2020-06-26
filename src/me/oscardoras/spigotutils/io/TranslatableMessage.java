package me.oscardoras.spigotutils.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;

import me.oscardoras.spigotutils.BukkitPlugin;

/** Represents a translatable message. */
public class TranslatableMessage {
	
	protected final BukkitPlugin plugin;
	protected final String key;
	protected String[] args;
	
	/**
	 * Represents a translatable message.
	 * @param plugin the plugin containing the translation files
	 * @param key the key of the translatable message in translation files
	 * @param args the arguments
	 */
	public TranslatableMessage(BukkitPlugin plugin, String key, String... args) {
		this.plugin = plugin;
		this.key = key.toLowerCase();
		this.args = args;
	}
	
	/**
	 * Gets the plugin containing the translation files.
	 * @return the plugin containing the translation files
	 */
	public BukkitPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the key of the translatable message in translation files.
	 * @return the key of the translatable message in translation files
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Gets the given arguments.
	 * @return the given arguments
	 */
	public String[] getArgs() {
		return args;
	}
	
	/**
	 * Sets the given arguments.
	 * @param args the arguments to set
	 */
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	/**
	 * Gets the translated message.
	 * @param language the language to translate the message
	 * @param args the arguments
	 * @return the translated message
	 */
	public String getMessage(String language, String... args) {
		String msg = getMessage(language, key, plugin.getTranslations());
		
		List<String> newArgs = new ArrayList<String>();
		int i1 = 0;
		for (String arg : this.args) {
			newArgs.add(arg);
			i1++;
		}
		int i2 = 0;
		for (String arg : args) {
			if (arg != null) {
				if (i2 < i1) newArgs.set(i2, arg);
				else newArgs.add(arg);
			}
			i2++;
		}
		
		for (String arg : newArgs) msg = msg.replaceFirst("%arg%", arg);
		
		return msg;
	}
	
	/**
	 * Gets the translated message.
	 * @param sender the sender to translate the message
	 * @param args the arguments
	 * @return the translated message
	 */
	public String getMessage(CommandSender sender, String... args) {
		return getMessage(getLanguage(sender), args);
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof TranslatableMessage) {
			TranslatableMessage o = (TranslatableMessage) object;
			return plugin.equals(o.plugin) && key.equals(o.key);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 12 + plugin.hashCode();
		hash *= 6 + key.hashCode();
		return hash;
	}
	
	
	/**
	 * Gets the language of a command sender.
	 * @param sender the command sender
	 * @return the language of the command sender
	 */
	public static String getLanguage(CommandSender sender) {
		if (sender instanceof ProxiedCommandSender) sender = ((ProxiedCommandSender) sender).getCaller();
		if (sender instanceof Player) return ((Player) sender).getLocale().split("_")[0].toLowerCase();
		else return "en";
	}
	
	private static String getMessage(String language, String key, Map<String, Properties> translates) {
		key = key.toLowerCase();
		if (translates.containsKey(language)) {
			Properties properties = translates.get(language);
			if (properties.containsKey(key)) return properties.getProperty(key);
		}
		if (translates.containsKey("en")) {
			Properties properties = translates.get("en");
			if (properties.containsKey(key)) return properties.getProperty(key);
		}
		for (String name : translates.keySet()) if (!name.equals(language) && !name.equals("en")) {
			Properties properties = translates.get(name);
			if (properties.containsKey(key)) return properties.getProperty(key);
		}
		throw new TranslatableMessageException(key);
	}
	
	/** Represents a translatable message not found. */
	public static class TranslatableMessageException extends IllegalArgumentException {

		private static final long serialVersionUID = -8022664399481137796L;
		
		
		/**
		 * Represents a translatable message not found.
		 * @param key the key of the translatable message in translation files which was not found
		 */
		public TranslatableMessageException(String key) {
			super(key);
		}
		
	}
	
}