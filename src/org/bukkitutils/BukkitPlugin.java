package org.bukkitutils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/** Represents a Bukkit plugin */
public class BukkitPlugin extends JavaPlugin implements Listener {
	
	private final Map<String, Properties> translations = new HashMap<String, Properties>();
	
	/** Represents a Bukkit plugin */
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
	 * Gets translation files for this plugin
	 * @return a map containing the translation properties file for each set languages
	 */
	public Map<String, Properties> getTranslations() {
		return translations;
	}
	
}