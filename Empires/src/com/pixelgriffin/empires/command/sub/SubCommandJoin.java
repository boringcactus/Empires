package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresJoinableExistsException;
import com.pixelgriffin.empires.exception.EmpiresJoinableIsEmpireException;
import com.pixelgriffin.empires.exception.EmpiresJoinableIsNotEmpireException;
import com.pixelgriffin.empires.handler.Empire;
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.Kingdom;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandJoin extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(invoker.getUniqueId());
				UUID invokerID = invoker.getUniqueId();
				String newJoinedName = "noname";
				
				Player other = Bukkit.getPlayer(_args[0]);
				
				//gather the joinedName
				//the user could be talking about a player OR a joinable
				//this determines what they're refering to
				Joinable tojoin = Empires.m_joinableHandler.getJoinable(_args[0]);
				
				//if(Empires.m_joinableHandler.getJoinableExists(_args[0])) {
				if(tojoin != null) {
					newJoinedName = _args[0];
				} else if(other != null) {
					UUID otherID = other.getUniqueId();
					
					EmpiresPlayer otherEP = Empires.m_playerHandler.getPlayer(otherID);
					//if(Empires.m_playerHandler.getPlayerExists(otherID)) {
					if(otherEP != null) {
						newJoinedName = otherEP.getJoined().getName();//Empires.m_playerHandler.getPlayerJoinedCivilization(otherID);
						
						//if the user belongs to the wilderness we cannot print anything
						if(newJoinedName.equals(PlayerHandler.m_defaultCiv)) {
							setError("You cannot join the wilderness");
							return false;
						}
					}
				} else {
					//if there was no player or joinable, tell them we couldn't find anything
					setError("Could not find a civilization associated with " + _args[0]);
					return false;
				}
				
				tojoin = Empires.m_joinableHandler.getJoinable(newJoinedName);
				if(tojoin == null) {
					setError("Could not find the civilization '" + newJoinedName + "'");
					return false;
				}
				
				/*
				 * Empire jazz
				 */
				//Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				Joinable joined = ep.getJoined();
				
				//we're a leader
				if(ep.getRole().equals(Role.LEADER)) {
					//join empire
					//we are a kingdom
						if(!joined.isEmpire()) {
						//doesn't have an empire
						//if(Empires.m_joinableHandler.getKingdomEmpire(joinedName).equals("")) {
						Kingdom kUs = (Kingdom)joined;
						if(kUs.getEmpire().isEmpty()) {
							//we've been requested
							//if(Empires.m_joinableHandler.getEmpireRequestedKingdom(newJoinedName, joinedName)) {
							if(tojoin.isEmpire()) {
							Empire eOther = (Empire)tojoin;
							if(eOther.isKingdomInvited(kUs)) {
								//set our empire
								//Empires.m_joinableHandler.setKingdomEmpire(joinedName, newJoinedName);
								kUs.setEmpire(eOther);
								
								//inform
								//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
								//Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(newJoinedName, ChatColor.YELLOW + displayName + " has joined the empire!");
								//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + "We have joined the " + displayName + " empire!");
								eOther.broadcastToEmpire(ChatColor.YELLOW + kUs.getDisplayName() + " has joined the empire!");
								kUs.broadcastMessageToJoined(ChatColor.YELLOW + "We have joined the " + eOther.getDisplayName() + " empire!");
								
								return true;
							}
							
							setError("You have not been invited to that empire");
							return false;
						}
						setError("Kingdoms cannot join other Kingdoms!");
						return false;
						}
						
						setError("You already have an empire!");
						return false;
				}
						setError("Empires cannot join other civilizations!");
						return false;
				} else {//not the leader of a kingdom!
					//player is trying to join a kingdom & has override perms
					//if(invoker.hasPermission("Empires.force.join") || Empires.m_joinableHandler.getJoinableRequestedPlayer(newJoinedName, invokerID)) {//force join
					if(invoker.hasPermission("Empires.force.join") || tojoin.isPlayerInvited(invokerID)) {
						//set the civilization
						//Empires.m_playerHandler.setPlayerJoinedCivlization(invokerID, tojoin);
						ep.joinJoinable(tojoin);
						
						//remove from the request list
						//Empires.m_joinableHandler.invokeJoinableRemoveRequestedPlayer(newJoinedName, invokerID);
						tojoin.uninvitePlayer(invokerID);
						
						//inform everyone we joined
						//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(newJoinedName, ChatColor.YELLOW + invoker.getDisplayName() + " has joined the civilization!");
						tojoin.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " has joined the civilization!");
						return true;
					}
					
					setError("You have not been invited to " + newJoinedName + "!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("The command 'join' can only be executed by a player");
		return false;
	}

}
