package org.bukkitutils.io;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sun.istack.internal.NotNull;

/** Represents a configuration file. */
public final class ConfigurationFile extends YamlConfiguration {
	
	protected final File file;
	
	/**
	 * Represents a configuration file.
	 * @param file the file
	 */
	public ConfigurationFile(@NotNull File file) {
		this.file = file;
		try {
			file.setReadable(true);
			file.setWritable(true);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Represents a configuration file.
	 * @param path the file path
	 */
	public ConfigurationFile(@NotNull String path) {
		this(new File(path));
	}
	
	/** Saves the configuration file. */
	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof YamlConfiguration)
			return super.equals((YamlConfiguration) object) && object instanceof ConfigurationFile ? file.equals(((ConfigurationFile) object).file) : true;
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 17 + super.hashCode();
		hash *= 4 + file.hashCode();
		return hash;
	}
	
}