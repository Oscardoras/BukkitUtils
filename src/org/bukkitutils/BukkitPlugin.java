package org.bukkitutils;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitutils.command.CommandAPI;

public class BukkitPlugin extends JavaPlugin implements Listener {
	
	private final YamlConfiguration pluginFile;
	private final YamlConfiguration translateFile;
	
	public BukkitPlugin() {
		pluginFile = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("plugin.yml")));
		YamlConfiguration translate;
		try {
			translate = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("translate.yml")));
		} catch (Exception ex) {
			translate = null;
		}
		translateFile = translate;
		CommandAPI.initialize();
	}
	
	public File getJarFile() {
		return getFile();
	}
	
	public YamlConfiguration getPluginConfig() {
		return pluginFile;
	}
	
	public YamlConfiguration getTranslateConfig() {
		return translateFile;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}
	
}