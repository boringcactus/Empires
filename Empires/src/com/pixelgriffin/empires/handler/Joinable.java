package com.pixelgriffin.empires.handler;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Joinable {
	private ConfigurationSection ymlData;//TODO replace with abstracted IO class
	
	//abstraction
	public abstract boolean isEmpire();
	
	//interfacing
	public void getDefaultGlobalFlagsForGroup() {
	}
	
	public void broadcastMessageToJoined() {
	}
	
	public void getRelationWish() {
	}
	
	public void getRelationWishSet() {
	}
	
	public void getRelation() {
	}
	
	public void toggleDefaultGlobalFlagForGroup() {
	}
	
	public void togglePermissionForGroup() {
	}
	
	public void getOfficers() {
	}
	
	public void getJoined() {
	}
	
	public void getLeader() {
	}
	
	public void getHeir() {
	}
	
	public void getDisplayName() {
	}
	
	public void getDescription() {
	}
	
	public void getHome() {
	}
	
	public void getBankBalance() {
	}
	
	public void getPermissionForGroup() {
	}
	
	public void getPower() {
	}
	
	public void setPower() {
	}
	
	public void getClaimSize() {
	}
	
	public void setClaimSize() {
	}
	
	public void setName() {
	}
	
	public void setDescription() {
	}
	
	public void setHome() {
	}
	
	public void setHeir() {
	}
	
	public void setRelationWish() {
	}
	
	public void disband() {
	}
	
	public void invitePlayer() {
	}
	
	public void uninvitePlayer() {
	}
	
	public void isPlayerInvited() {
	}
	
	public void depositMoney() {
	}
	
	public void withdrawMoney() {
	}
}
