package org.bukkitutils.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.bukkitutils.BukkitPlugin;
import org.bukkitutils.io.TranslatableMessage.TranslatableMessageException;

import com.google.gson.JsonParseException;
import com.sun.istack.internal.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Manages notifications. */
public final class Notification implements Listener {
	
	/**
	 * Registers a plugin.
	 * @param plugin the bukkit plugin to register
	 */
	public static void register(@NotNull BukkitPlugin plugin) {
		new Notification(plugin);
	}
	
	/**
	 * Sends a notification to a player.
	 * @param offlinePlayer the player to send the notification
	 * @param message message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
	 */
	public static void send(@NotNull OfflinePlayer offlinePlayer, @NotNull Object... message) {
		if (offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			player.spigot().sendMessage(getBaseComponents(TranslatableMessage.getLanguage(player), message));
		} else {
			ConfigurationFile config = new ConfigurationFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			UUID uuid = UUID.randomUUID();
			ConfigurationSection section = config.createSection(offlinePlayer.getUniqueId().toString() + "." + uuid.toString());
			int i = 0;
			for (Object object : getObjects(message)) {
				if (object instanceof TranslatableMessage) {
					TranslatableMessage msg = (TranslatableMessage) object;
					section.set(i + ".plugin", msg.getPlugin().getName());
					section.set(i + ".key", msg.getKey());
					section.set(i + ".args", Arrays.asList(msg.getArgs()));
				} else section.set(i + ".value", (String) object);
				i++;
			}
			config.save();
		}
	}
	
	/**
	 * Sends a notification to the players of a team.
	 * @param team the team containing the players to send the notification
	 * @param message message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
	 */
	@SuppressWarnings("deprecation")
	public static void send(@NotNull Team team, @NotNull Object... message) {
		for (String entry : team.getEntries()) {
			try {
				Entity entity = Bukkit.getEntity(UUID.fromString(entry));
				if (entity != null) {
					entity.spigot().sendMessage(getBaseComponents(TranslatableMessage.getLanguage(entity), message));
					continue;
				}
			} catch (IllegalArgumentException e) {}
			Notification.send(Bukkit.getOfflinePlayer(entry), message);
		}
	}
	
	private static BaseComponent[] getBaseComponents(String language, Object[] message) {
		List<BaseComponent> components = new ArrayList<BaseComponent>();
		for (Object msg : message) {
			if (msg instanceof Object[]) components.addAll(Arrays.asList(getBaseComponents(language, (Object[]) msg)));
			else {
				BaseComponent component;
				if (msg instanceof BaseComponent) component = (BaseComponent) msg;
				else if (msg instanceof TranslatableMessage) component = new TextComponent(((TranslatableMessage) msg).getMessage(language));
				else component = new TextComponent(msg.toString());
				components.add(component);
			}
		}
		return components.toArray(new BaseComponent[components.size()]);
	}
	
	private static Object[] getObjects(Object[] message) {
		List<Object> objects = new ArrayList<Object>();
		for (Object msg : message) {
			if (msg instanceof Object[]) objects.addAll(Arrays.asList(getObjects((Object[]) msg)));
			else {
				Object object;
				if (msg instanceof BaseComponent) object = ComponentSerializer.toString((BaseComponent) msg);
				else if (msg instanceof TranslatableMessage) object = msg;
				else object = msg.toString();
				objects.add(object);
			}
		}
		return objects.toArray(new Object[objects.size()]);
	}
	

	private final BukkitPlugin plugin;
	
	private Notification(BukkitPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	private void on(PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			Player player = e.getPlayer();
			
			ConfigurationFile config = new ConfigurationFile(Bukkit.getWorlds().get(0).getWorldFolder() + "/data/notifications.yml");
			if (config.contains(player.getUniqueId().toString())) {
				boolean found = false;
				for (String key : config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false)) {
					List<BaseComponent> components = new ArrayList<BaseComponent>();
					
					ConfigurationSection section = config.getConfigurationSection(player.getUniqueId().toString() + "." + key);
					for (String i : section.getKeys(false)) {
						ConfigurationSection c = section.getConfigurationSection(i);
						if (c.contains("value")) {
							String value = c.getString("value");
							try {
								components.addAll(Arrays.asList(ComponentSerializer.parse(value)));
							} catch (JsonParseException e1) {
								components.add(new TextComponent(value));
							}
						} else if (c.contains("path") && c.contains("plugin")) {
							Plugin pl = Bukkit.getPluginManager().getPlugin(c.getString("plugin"));
							if (plugin.equals(pl)) {
								List<String> args;
								if (c.contains("args"))
									args = c.getStringList("args");
								else args = new ArrayList<String>();
								
								try {
									components.add(new TextComponent(
											new TranslatableMessage(plugin, c.getString("path")).getMessage(player, args.toArray(new String[args.size()]))
									));
								} catch (TranslatableMessageException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
					
					player.spigot().sendMessage(components.toArray(new BaseComponent[components.size()]));
					
					config.set(player.getUniqueId().toString() + "." + key, null);
					if (config.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).isEmpty())
						config.set(player.getUniqueId().toString(), null);
					found = true;
				}
				if (found) config.save();
			}
		}, 1L);
	}
	
}