package org.bukkitutils;

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

import com.sun.istack.internal.NotNull;

/** An optimized player reloading event listener. */
public final class PlayerReloader implements Listener {
	
	@FunctionalInterface
	public static interface PlayerReloaderRunnable {
		/**
		 * The code to run when the player is reloaded.
		 * @param player the player to reload
		 * @param location the new player location
		 * @param type the reloading type
		 */
		void onReload(@NotNull Player player, @NotNull Location location, @NotNull Type type);
	}
	
	/** The reloading type */
	public static enum Type {
		/** The player joins the server */
        JOIN,
        
        /** The player moves */
        MOVE,
        
        /** The player respawns */
        RESPAWN,
        
        /** The player is teleported */
        TELEPORT,
        
        /** The player leaves the server */
        QUIT,
        
        /** A clock */
        TIMER,
		
		 /** Manual */
        MANUAL;
    }
	
	/**
	 * Registers a new player reloader.
	 * @param plugin the plugin
	 * @param runable the runnable to run when the player is reloaded
	 * @param ticks the clock delay. Set 0 to disable the clock
	 */
	public static @NotNull PlayerReloaderRunnable register(@NotNull Plugin plugin, @NotNull PlayerReloaderRunnable runnable, long ticks) {
    	new PlayerReloader(plugin, runnable, ticks);
    	return runnable;
    }
	
    
	private final PlayerReloaderRunnable runnable;
    
    private PlayerReloader(Plugin plugin, PlayerReloaderRunnable runnable, long ticks) {
    	this.runnable = runnable;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (ticks > 0L) Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
        	for (Player player : Bukkit.getOnlinePlayers()) runnable.onReload(player, player.getLocation(), Type.TIMER);
        }, 0L, ticks);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void on(PlayerJoinEvent e) {
    	Player player = e.getPlayer();
    	runnable.onReload(player, player.getLocation(), Type.JOIN);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void on(PlayerMoveEvent e) {
    	if (!e.isCancelled()) {
	    	Location from = e.getFrom();
	    	Location to = e.getTo();
	    	if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
		        runnable.onReload(e.getPlayer(), to, Type.MOVE);
	    	}
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void on(PlayerRespawnEvent e) {
    	runnable.onReload(e.getPlayer(), e.getRespawnLocation(), Type.RESPAWN);
	}
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void on(PlayerTeleportEvent e) {
    	if (!e.isCancelled()) {
    		runnable.onReload(e.getPlayer(), e.getTo(), Type.TELEPORT);
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void on(PlayerQuitEvent e) {
    	Player player = e.getPlayer();
    	runnable.onReload(player, player.getLocation(), Type.QUIT);
    }
	
}