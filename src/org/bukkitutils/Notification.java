package org.bukkitutils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.bukkitutils.io.DataFile;
import org.bukkitutils.io.Message;
import org.bukkitutils.io.Translate;

public final class Notification {
	private Notification() {}
	
	
	private static class EventListener implements Listener {
		
		private BukkitPlugin plugin;
		
		public EventListener(BukkitPlugin plugin) {
			this.plugin = plugin;
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
		
		@EventHandler
		public void on(PlayerJoinEvent e) {
			Player player = e.getPlayer();
			
			DataFile file = new DataFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			YamlConfiguration config = file.getYML();
			if (config.contains(player.getUniqueId().toString())) {
				boolean found = false;
				for (String key : config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false)) {
					if (config.contains(player.getUniqueId().toString() + "." + key + ".plugin")) {
						Plugin pl = Bukkit.getPluginManager().getPlugin(config.getString(player.getUniqueId().toString() + "." + key + ".plugin"));
						if (plugin.equals(pl)) {
							if (config.contains(player.getUniqueId().toString() + "." + key + ".path")) {
								Message message = new Message() {
									public BukkitPlugin getPlugin() {
										return plugin;
									}
									public String getPath() {
										return config.getString(player.getUniqueId().toString() + "." + key + ".path");
									}
								};
								
								String[] args;
								if (config.contains(player.getUniqueId().toString() + "." + key + ".args")) {
									args = config.getStringList(player.getUniqueId().toString() + "." + key + ".args").toArray(new String[0]);
								} else args = new String[0];
								
								player.sendMessage(Translate.getPluginMessage(player, message, args));
								
								config.set(player.getUniqueId().toString() + "." + key, null);
								if (config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).isEmpty()) {
									config.set(player.getUniqueId().toString(), null);
								}
								found = true;
							}
						}
					}
				}
				if (found) file.save();
			}
		}
	};
	
	public static void registerPlugin(BukkitPlugin plugin) {
		new EventListener(plugin);
	}
	
	public static void send(OfflinePlayer offlinePlayer, Message message, String... args) {
		if (offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			player.sendMessage(Translate.getPluginMessage(player, message, args));
		} else {
			DataFile file = new DataFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			YamlConfiguration config = file.getYML();
			UUID uuid = UUID.randomUUID();
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".plugin", message.getPlugin().getName());
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".path", message.getPath());
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".args", args);
			file.save();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void send(Team team, Message message, String... args) {
		for (String entry : team.getEntries()) {
			try {
				Entity entity = Bukkit.getEntity(UUID.fromString(entry));
				if (entity != null) {
					entity.sendMessage(Translate.getPluginMessage(entity, message, args));
					continue;
				}
			} catch (IllegalArgumentException ex) {}
			Notification.send(Bukkit.getOfflinePlayer(entry), message, args);
		}
	}
	
}