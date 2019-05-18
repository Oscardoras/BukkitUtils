package org.bukkitutils.event;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public abstract class PlayerReload implements Listener {
    
	private final Set<Player> moved = new HashSet<Player>();
	private final Plugin plugin;
    
    public PlayerReload(Plugin plugin) {
    	this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) onReload(player, Type.TIMER);
            }
        }, 100l, 100l);
    }
    
    public abstract void onReload(Player player, Type type);
    
    @EventHandler(priority = EventPriority.LOWEST)
    private void on(PlayerJoinEvent e) {
        onReload(e.getPlayer(), Type.JOIN);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerMoveEvent e) {
    	Location from = e.getFrom();
    	Location to = e.getTo();
    	if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
	        Player player = e.getPlayer();
	        if (!moved.contains(player)) {
	        	moved.add(player);
	        	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						moved.remove(player);
					}
	        	}, 5l);
	        	onReload(player, Type.MOVE);
	        }
    	}
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerRespawnEvent e) {
        onReload(e.getPlayer(), Type.RESPAWN);
	}
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerTeleportEvent e) {
        onReload(e.getPlayer(), Type.TELEPORT);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent e) {
        onReload(e.getPlayer(), Type.QUIT);
    }
    
    public enum Type {
        JOIN, MOVE, RESPAWN, TELEPORT, QUIT, TIMER;
    }
	
}