package org.bukkitutils.io;

import org.bukkitutils.BukkitPlugin;

public class Message {
	
	protected BukkitPlugin plugin;
	protected String path;
	
	public Message(BukkitPlugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
	}
	
	public BukkitPlugin getPlugin() {
		return plugin;
	}
	
	public String getPath() {
		return path;
	}
	
}