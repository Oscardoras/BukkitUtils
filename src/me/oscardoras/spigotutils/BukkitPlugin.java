package me.oscardoras.spigotutils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

/** Represents a Bukkit plugin. */
public class BukkitPlugin extends JavaPlugin {
	
	private final Map<String, Properties> translations = new HashMap<String, Properties>();
	private boolean reload = false;
	
	/** Represents a Bukkit plugin. */
	public BukkitPlugin() {
		try {
			JarFile jarFile = new JarFile(getFile());
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("translations"))
					try {
						Properties properties = new Properties();
						properties.load(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(entry.getName()), Charset.forName("UTF-8")));
						String[] files = entry.getName().split("/");
						translations.put(files[files.length - 1].split("\\.")[0], properties);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets translation files for this plugin.
	 * @return a map containing the translation properties files for each language defined
	 */
	public Map<String, Properties> getTranslations() {
		return translations;
	}
	
	@Override
	public FileConfiguration getConfig() {
		if (!reload && this.isEnabled()) {
			JavaPlugin plugin = this;
			Bukkit.getPluginManager().registerEvents(new Listener() {
				
				@EventHandler(priority = EventPriority.HIGHEST)
				public void onReload(ServerCommandEvent e) {
					if (!e.isCancelled()) reload("/"+e.getCommand());
				}
				
				@EventHandler(priority = EventPriority.HIGHEST)
				public void onReload(RemoteServerCommandEvent e) {
					if (!e.isCancelled()) reload("/"+e.getCommand());
				}
				
				@EventHandler(priority = EventPriority.HIGHEST)
				public void onReload(PlayerCommandPreprocessEvent e) {
					if (!e.isCancelled()) reload(e.getMessage());
				}
				
				public void reload(String cmd) {
					if (cmd.startsWith("/reload") || cmd.startsWith("/minecraft:reload")) {
						try {
							plugin.onDisable();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Bukkit.getScheduler().cancelTasks(plugin);
						plugin.reloadConfig();
						try {
							plugin.onEnable();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, this);
			reload = true;
		}
		
		return super.getConfig();
	}
	
}