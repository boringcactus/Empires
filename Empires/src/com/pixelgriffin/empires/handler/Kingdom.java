package com.pixelgriffin.empires.handler;

import org.bukkit.configuration.ConfigurationSection;

import com.pixelgriffin.empires.Empires;

public class Kingdom extends Joinable {

	public Kingdom(ConfigurationSection data) {
		super(data);
	}

	public String getEmpire() {
		return ymlData.getString("empire");
	}
	
	public boolean setEmpire(Empire other) {
		if(other == null) {
			ymlData.set("empire", null);
			return true;
		}
	
		ymlData.set("empire", other.getName());
		
		other.addKingdom(this);
		other.uninviteKingdom(this);//uninvite us if we were invited
		
		return true;
	}
	
	public void setAsEmpire() {
		ymlData.set("is-empire", true);
	}
	
	public void leaveEmpire() {
		Empire ourEmpire = (Empire)Empires.m_joinableHandler.getJoinable(getEmpire());
		ourEmpire.removeKingdom(this);
		
		ymlData.set("empire", "");
	}
	
	@Override
	public boolean isEmpire() {
		return false;
	}

}
