package org.bukkitutils.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.bukkitutils.BukkitPlugin;

/** Manages notifications */
public final class Notification implements Listener {
	
	/**
	 * Registers a plugin
	 * @param plugin the bukkit plugin to registers
	 */
	public static void register(BukkitPlugin plugin) {
		new Notification(plugin);
	}
	
	/**
	 * Sends a notification to a player
	 * @param offlinePlayer the player to send the notification
	 * @param message the translatable message to send to the player
	 * @param args the arguments for the translatable message
	 */
	public static void send(OfflinePlayer offlinePlayer, TranslatableMessage message, String... args) {
		if (offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			player.sendMessage(message.getMessage(player, args));
		} else {
			DataFile file = new DataFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			YamlConfiguration config = file.getYML();
			UUID uuid = UUID.randomUUID();
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".plugin", message.getPlugin().getName());
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".path", message.getPath());
			config.set(offlinePlayer.getUniqueId().toString() + "." + uuid.toString() + ".args", Arrays.asList(args));
			file.save();
		}
	}
	
	/**
	 * Sends a notification to the players of a team
	 * @param team the team containing the players to send the notification
	 * @param message the translatable message to send to the player
	 * @param args the arguments for the translatable message
	 */
	@SuppressWarnings("deprecation")
	public static void send(Team team, TranslatableMessage message, String... args) {
		for (String entry : team.getEntries()) {
			try {
				Entity entity = Bukkit.getEntity(UUID.fromString(entry));
				if (entity != null) {
					entity.sendMessage(message.getMessage(entity, args));
					continue;
				}
			} catch (IllegalArgumentException e) {}
			Notification.send(Bukkit.getOfflinePlayer(entry), message, args);
		}
	}
	

	private final BukkitPlugin plugin;
	
	private Notification(BukkitPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			Player player = e.getPlayer();
			
			DataFile file = new DataFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			YamlConfiguration config = file.getYML();
			if (config.contains(player.getUniqueId().toString())) {
				boolean found = false;
				for (String key : config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false)) {
					if (config.contains(player.getUniqueId().toString() + "." + key + ".path")) {
						if (config.contains(player.getUniqueId().toString() + "." + key + ".plugin")) {
							Plugin pl = Bukkit.getPluginManager().getPlugin(config.getString(player.getUniqueId().toString() + "." + key + ".plugin"));
							if (plugin.equals(pl)) {
								List<String> args;
								if (config.contains(player.getUniqueId().toString() + "." + key + ".args"))
									args = config.getStringList(player.getUniqueId().toString() + "." + key + ".args");
								else args = new ArrayList<String>();
								
								try {
									player.sendMessage(new TranslatableMessage(plugin, config.getString(player.getUniqueId().toString() + "." + key + ".path")).getMessage(player, args.toArray(new String[args.size()])));
								} catch (TranslatableMessageException ex) {
									ex.printStackTrace();
								}
								
								config.set(player.getUniqueId().toString() + "." + key, null);
								if (config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).isEmpty())
									config.set(player.getUniqueId().toString(), null);
								found = true;
							}
						}
					}
				}
				if (found) file.save();
			}
		}, 1L);
	}
	
}