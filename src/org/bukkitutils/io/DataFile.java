package org.bukkitutils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.configuration.file.YamlConfiguration;

/** Represents a file containing data */
public class DataFile {
	
	protected final static List<FileCache<YamlConfiguration>> yamlConfigurationFiles = new ArrayList<FileCache<YamlConfiguration>>();
	protected final static List<FileCache<Properties>> propertiesFiles = new ArrayList<FileCache<Properties>>();
	
	static {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
				    synchronized(yamlConfigurationFiles) {
    					Iterator<FileCache<YamlConfiguration>> it = yamlConfigurationFiles.iterator();
    					while (it.hasNext()) {
    						YamlConfiguration config = it.next().get();
    						if (config == null) it.remove();
    					}
				    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 50L, 50L);
		
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					synchronized(propertiesFiles) {
    					Iterator<FileCache<Properties>> it = propertiesFiles.iterator();
    					while (it.hasNext()) {
    						Properties properties = it.next().get();
    						if (properties == null) it.remove();
    					}
				    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 50L, 50L);
	}
	
	
	protected final File file;
	
	/**
	 * Represents a file containing data
	 * @param path the path of the file
	 */
	public DataFile(String path) {
		this.file = new File(path);
	}
	
	/**
	 * Represents a file containing data
	 * @param file the file
	 */
	public DataFile(File file) {
		this.file = file;
	}
	
	/**
	 * Gets the file
	 * @return the file
	 */
	public File getFile() {
		try {
			file.setReadable(true);
			file.setWritable(true);
			if (!file.isFile()) {
				if (file.exists()) file.delete();
				file.mkdirs();
				file.delete();
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the config for this file
	 * @return the YamlConfiguration object
	 */
	@SuppressWarnings("unlikely-arg-type")
	public YamlConfiguration getYML() {
		try {
			YamlConfiguration config = null;
			synchronized(yamlConfigurationFiles) {
    			if (yamlConfigurationFiles.contains(this)) {
    				config = yamlConfigurationFiles.get(yamlConfigurationFiles.indexOf(this)).get();
    			}
    			if (config == null) {
    				config = YamlConfiguration.loadConfiguration(getFile());
    				yamlConfigurationFiles.remove(this);
    				yamlConfigurationFiles.add(new FileCache<YamlConfiguration>(this, config));
    			}
			}
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the properties for this file
	 * @return the Properties object
	 */
	@SuppressWarnings("unlikely-arg-type")
	public Properties getProperties() {
		try {
			Properties properties = null;
			synchronized(propertiesFiles) {
    			if (propertiesFiles.contains(this)) {
    				properties = propertiesFiles.get(propertiesFiles.indexOf(this)).get();
    			}
    			if (properties == null) {
    				properties = new Properties();
    				properties.load(new FileInputStream(getFile()));
    				propertiesFiles.remove(this);
    				propertiesFiles.add(new FileCache<Properties>(this, properties));
    			}
			}
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Saves the file */
	@SuppressWarnings("unlikely-arg-type")
	public void save() {
		try {
		    synchronized(yamlConfigurationFiles) {
    			if (yamlConfigurationFiles.contains(this)) {
    				YamlConfiguration config = yamlConfigurationFiles.get(yamlConfigurationFiles.indexOf(this)).get();
    				if (config != null) config.save(getFile());
    				return;
    			}
		    }
			synchronized(propertiesFiles) {
    			if (propertiesFiles.contains(this)) {
    				Properties properties = propertiesFiles.get(propertiesFiles.indexOf(this)).get();
    				if (properties != null) properties.store(new FileOutputStream(getFile()), "");
    				return;
    			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null) {
			if (object instanceof DataFile)
				try {
					return getFile().getCanonicalPath().equals(((DataFile) object).getFile().getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
					return getFile().getPath().equals(((DataFile) object).getFile().getPath());
				}
			if (object instanceof FileCache<?>) return this.equals(((FileCache<?>) object).dataFile);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		try {
			return getFile().getCanonicalPath().hashCode();
		} catch (IOException e) {
			e.printStackTrace();
			return getFile().getPath().hashCode();
		}
	}
	
}

class FileCache<T> extends SoftReference<T> {
	
	public final DataFile dataFile;

	public FileCache(DataFile dataFile, T data) {
		super(data);
		this.dataFile = dataFile;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null) {
			if (object instanceof FileCache<?>) return dataFile.equals(((FileCache<?>) object).dataFile);
			if (object instanceof DataFile) return dataFile.equals(object);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return dataFile.hashCode();
	}
	
}