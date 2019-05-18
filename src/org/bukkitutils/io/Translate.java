package org.bukkitutils.io;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class Translate {
	private Translate() {}
	
	public static String getMessage(String language, ConfigurationSection path) {
		if (language.length() > 2) language = language.toLowerCase().substring(0, 2);
		if (path.contains(language)) return path.getString(language);
		language = "en";
		if (path.contains(language)) return path.getString(language);
		for (String lang : path.getKeys(false)) return path.getString(lang);
		throw new MessageException(path.getCurrentPath());
	}
	
	public static String getPluginMessage(CommandSender sender, Message message, String... args) {
		YamlConfiguration config = message.getPlugin().getTranslateConfig();
		String path = message.getPath();
		if (config != null && config.contains(path)) {
			String msg = getMessage(getLanguage(sender), config.getConfigurationSection(path));
			if (msg != null) for (String arg : args) msg = msg.replaceFirst("%arg%", arg);
			return msg;
		}
		throw new MessageException(path);
	}
	
	public static String getLanguage(CommandSender sender) {
		if (sender instanceof ProxiedCommandSender) sender = ((ProxiedCommandSender) sender).getCaller();
		if (sender instanceof Player) return ((Player) sender).getLocale().split("_")[0];
		else return "en";
	}
	
}