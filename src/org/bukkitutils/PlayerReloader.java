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

/** An optimized player reloading event listener */
public final class PlayerReloader implements Listener {
	
	@FunctionalInterface
	public static interface PlayerReloaderRunnable {
		/**
		 * The code to run when the player is reloaded.
		 * @param player the player to reload
		 * @param type the reloading type
		 */
		void onReload(Player player, Type type);
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
	 * Registers a new player reloader
	 * @param plugin the plugin
	 * @param runable the PlayerReloaderRunnable to run when the player is reloaded is performed
	 * @param ticks the clock delay. Set 0 to disable the clock
	 */
	public static PlayerReloaderRunnable register(Plugin plugin, PlayerReloaderRunnable runnable, long ticks) {
    	new PlayerReloader(plugin, runnable, ticks);
    	return runnable;
    }
	
    
	private final PlayerReloaderRunnable runnable;
    
    private PlayerReloader(Plugin plugin, PlayerReloaderRunnable runnable, long ticks) {
    	this.runnable = runnable;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (ticks > 0L) Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) runnable.onReload(player, Type.TIMER);
            }
        }, 0L, ticks);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private void on(PlayerJoinEvent e) {
    	runnable.onReload(e.getPlayer(), Type.JOIN);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerMoveEvent e) {
    	Location from = e.getFrom();
    	Location to = e.getTo();
    	if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
	        runnable.onReload(e.getPlayer(), Type.MOVE);
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW)
	public void on(PlayerRespawnEvent e) {
    	runnable.onReload(e.getPlayer(), Type.RESPAWN);
	}
    
    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerTeleportEvent e) {
    	runnable.onReload(e.getPlayer(), Type.TELEPORT);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerQuitEvent e) {
    	runnable.onReload(e.getPlayer(), Type.QUIT);
    }
	
}