package com.pixelgriffin.empires.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EmpiresPlayerClaimEvent extends Event implements Cancellable {
	
	private boolean cancelled;
	
	private Player player;
	private String joinableName;
	
	public EmpiresPlayerClaimEvent(Player _player, String _joinableName) {
		this.player = _player;
		this.joinableName = _joinableName;
		
		this.cancelled = false;
	}
	
	public Player getClaimingPlayer() {
		return this.player;
	}
	
	public String getClaimJoinableName() {
		return this.joinableName;
	}
	
	@Override
	public HandlerList getHandlers() {
		return null;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean _cancelled) {
		this.cancelled = _cancelled;
	}
}
