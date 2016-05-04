package com.pixelgriffin.empires.handler;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;

public class Empire extends Joinable {
	
	public Empire(ConfigurationSection data) {
		super(data);
	}

	public void broadcastToEmpire(String msg) {
		ArrayList<UUID> recv;
		Player sendTo;
		for(String kingdom : getKingdomSet()) {
			recv = Empires.m_joinableHandler.getJoinable(kingdom).getJoined();
			
			for(UUID id : recv) {
				sendTo = Bukkit.getPlayer(id);
				if(sendTo != null) {
					sendTo.sendMessage(msg);
				}
			}
		}
	}
	
	public void addKingdom(Kingdom kingdom) {
		ArrayList<String> joinedKingdoms = getKingdomSet();
		
		joinedKingdoms.add(kingdom.getName());
		
		ymlData.set("kingdoms", joinedKingdoms);
	}
	
	public void removeKingdom(Kingdom kingdom) {
		ArrayList<String> joinedKingdoms = getKingdomSet();
		
		joinedKingdoms.remove(kingdom.getName());
		
		ymlData.set("kingdoms", joinedKingdoms);
	}
	
	public ArrayList<String> getKingdomSet() {
		return (ArrayList<String>)ymlData.getList("kingdoms");
	}
	
	public boolean isKingdomInvited(Kingdom kingdom) {
		return getKingdomSet().contains(kingdom.getName());
	}
	
	public void inviteKingdom(Kingdom kingdom) {
		ArrayList<String> requested = (ArrayList<String>)ymlData.getList("requested-kingdoms");
		
		requested.add(kingdom.getName());
		
		ymlData.set("requested-kingdoms", requested);
	}
	
	public void uninviteKingdom(Kingdom kingdom) {
		ArrayList<String> requested = (ArrayList<String>)ymlData.getList("requested-kingdoms");
		
		requested.remove(kingdom.getName());
		
		ymlData.set("requested-kingdoms", requested);
	}
	
	//abstration implementation
	@Override
	public boolean isEmpire() {
		return true;
	}
}
