package com.pixelgriffin.empires.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EmpiresPlayerCreateJoinable extends Event implements Cancellable {
	
	private boolean cancelled;
	
	private Player player;
	private String joinableName;
	
	private boolean isEmpire;
	
	public EmpiresPlayerCreateJoinable(Player _player, String _potentialJoinableName, boolean _isEmpire) {
		this.player = _player;
		this.joinableName = _potentialJoinableName;
		this.isEmpire = _isEmpire;
		
		this.cancelled = false;
	}
	
	public boolean getIsCreatingEmpire() {
		return this.isEmpire;
	}
	
	public Player getCreatingPlayer() {
		return this.player;
	}
	
	public String getPotentialJoinableName() {
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
