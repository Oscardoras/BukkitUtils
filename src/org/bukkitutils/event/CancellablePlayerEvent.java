package org.bukkitutils.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public abstract class CancellablePlayerEvent extends PlayerEvent implements Cancellable {
    
    protected boolean cancelled = false;
    
    public CancellablePlayerEvent(Player who) {
        super(who);
    }
	
	@Override
	public boolean isCancelled() {
	    return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
	    cancelled = cancel;
	}
	
}