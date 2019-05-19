package org.bukkitutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitutils.command.CommandAPI;

public class BukkitPlugin extends JavaPlugin implements Listener {
	
	private final YamlConfiguration pluginConfig;
	private final Map<String, Properties> translates = new HashMap<String, Properties>();
	
	public BukkitPlugin() {
		pluginConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("plugin.yml"), Charset.forName("UTF-8")));
		
		try {
			JarFile jarFile = new JarFile(getFile());
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("translate"))
					try {
						Properties properties = new Properties();
						properties.load(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(entry.getName()), Charset.forName("UTF-8")));
						String[] files = entry.getName().split("/");
						translates.put(files[files.length - 1].split("\\.")[0], properties);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CommandAPI.initialize();
	}
	
	public File getFile() {
		return super.getFile();
	}
	
	public YamlConfiguration getPluginConfig() {
		return pluginConfig;
	}
	
	public Map<String, Properties> getTranslates() {
		return translates;
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