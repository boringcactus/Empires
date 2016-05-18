package com.pixelgriffin.empires.handler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;

public class EmpiresPlayer {
	protected ConfigurationSection ymlData;
	protected Player bukkitPlayer;
	
	public EmpiresPlayer(Player bukkit, ConfigurationSection data) {
		ymlData = data;
		bukkitPlayer = bukkit;
	}
	
	public Player getBukkitPlayer() {
		return bukkitPlayer;
	}
	
	public Joinable getJoined() {
		return Empires.m_joinableHandler.getJoinable(ymlData.getString("j"));
	}
	
	/**
	 * DO NOT USE TO JOIN A JOINABLE, USE joinJoinable(...)
	 * 
	 * @param newPointer
	 */
	public void setJoinedPointer(String newPointer) {
		ymlData.set("j", newPointer);
	}
	
	public void joinJoinable(Joinable join) {
		if(join == null)
			return;
		
		if(bukkitPlayer == null)
			return;
		
		if(getJoined() != null)
			return;
		
		setJoinedPointer(join.getName());
		join.addPlayerPointer(bukkitPlayer.getUniqueId());
		join.setPower(getPower(), true);
	}
	
	public Role getRole() {
		try {
			return Role.valueOf(ymlData.getString("r"));
		} catch(Exception e) {//corrupted role value found
			e.printStackTrace();
			return Role.MEMBER;
		}
	}
	
	public void setRole(Role newRole) {
		ymlData.set("r", newRole.toString());
	}
	
	public String getTitle() {
		return ymlData.getString("t");
	}
	
	public void setTitle(String newTitle) {
		ymlData.set("t", newTitle);
	}
	
	public int getPower() {
		return ymlData.getInt("p");
	}
	
	public void setPower(int newPower) {
		int oldPower = getPower();
		ymlData.set("p", newPower);
		
		//update our joined's power if possible
		Joinable joined = getJoined();
		if(joined == null)
			return;
		
		joined.setPower(newPower - oldPower, true);
	}
	
	public void setLastPlayTime(long time) {
		ymlData.set("pt", time);
	}
	
	public long getLastPlayTime() {
		return ymlData.getLong("pt");
	}
	
	public boolean isAutoClaiming() {
		return ymlData.getBoolean("ac");
	}
	
	public void setAutoClaiming(boolean on) {
		ymlData.set("ac", on);
	}
	
	public void setTPID(int id) {
		ymlData.set("tp-id", id);
	}
	
	public int getTPID() {
		return ymlData.getInt("tp-id");
	}
	
	public void leaveJoined() {
		Joinable joined = getJoined();
		if(joined == null)
			return;
		
		//clear our old data
		ymlData.set("j", PlayerHandler.m_defaultCiv);
		Role oldRole = Role.valueOf(ymlData.getString("r").toUpperCase());
		ymlData.set("r", PlayerHandler.m_defaultRole.toString());
		ymlData.set("t", PlayerHandler.m_defaultTitle);
		
		if(joined.removePlayerPointer(this))//if we disbanded
			return;//don't continue
		
		int powerValue = getPower();
		joined.setPower(-powerValue, true);
		
		if(oldRole.equals(Role.LEADER)) {
			joined.findNewLeader(true);
		}
	}
}
