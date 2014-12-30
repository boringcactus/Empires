package com.pixelgriffin.empires.command.sub;

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
				String invokerName = invoker.getName();
				
				String newJoinedName;
				
				//gather the joinedName
				//the user could be talking about a player OR a joinable
				//this determines what they're refering to
				if(Empires.m_joinableHandler.joinableExists(_args[0])) {
					newJoinedName = _args[0];
				} else if(Empires.m_playerHandler.playerExists(_args[0])) {
					newJoinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(_args[0]);
					
					//if the user belongs to the wilderness we cannot print anything
					if(newJoinedName.equals(PlayerHandler.m_defaultCiv)) {
						setError("You cannot join the wilderness");
						return false;
					}
				} else {
					//if there was no player or joinable, tell them we couldn't find anything
					setError("Could not find a civilization associated with " + _args[0]);
					return false;
				}
				
				/*
				 * Empire jazz
				 */
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				//we're a leader
				if(invokerRole.equals(Role.LEADER)) {
					//join empire
					try {
						//we are a kingdom
						try {
							//doesn't have an empire
							if(Empires.m_joinableHandler.getJoinableEmpire(joinedName).equals("")) {
								//we've been requested
								if(Empires.m_joinableHandler.getEmpireRequestedKingdom(newJoinedName, joinedName)) {
									//set our empire
									try {
										Empires.m_joinableHandler.setJoinableEmpire(joinedName, newJoinedName);
										
										return true;
									} catch (EmpiresJoinableIsNotEmpireException e) {
										e.printStackTrace();
										
										setError("You tried to join a kingdom as an empire..");
										return false;
									}
								}
								
								setError("You have not been invited to that empire");
								return false;
							}
							
							setError("You already have an empire!");
							return false;
						} catch (EmpiresJoinableIsEmpireException e) {
							setError("You are already part of an empire");
							return false;
						}
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						
						setError("Something went wrong!");
						return false;
					}
					
				} else {//not the leader of a kingdom!
					//player is trying to join a kingdom & has override perms
					try {
						if(invoker.hasPermission("Empires.force.join") || Empires.m_joinableHandler.getJoinableRequestedPlayer(newJoinedName, invokerName)) {//force join
							try {
								//set the civilization
								Empires.m_playerHandler.setPlayerJoinedCivlization(invokerName, newJoinedName);
								
								//remove from the request list
								Empires.m_joinableHandler.joinableRemoveRequestedPlayer(newJoinedName, invokerName);
							} catch (EmpiresJoinableExistsException e) {
								setError("You must leave your current civilization first!");
								return false;
							} catch (EmpiresJoinableDoesNotExistException e) {
								setError("Could not find the civilization '" + newJoinedName + "'");
								return false;
							}
							
							//inform everyone we joined
							try {
								Empires.m_joinableHandler.broadcastToJoined(newJoinedName, ChatColor.YELLOW + invokerName + " has joined the civilization!");
							} catch (EmpiresJoinableDoesNotExistException e) {
								e.printStackTrace();
								
								setError("Something went wrong!");
								return false;
							}
							return true;
						}
						
						setError("You have not been invited to " + newJoinedName + "!");
						return false;
					} catch (EmpiresJoinableDoesNotExistException e) {
						setError("Could not find the civilization '" + newJoinedName +"'");
						return false;
					}
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("The command 'join' can only be executed by a player");
		return false;
	}

}
