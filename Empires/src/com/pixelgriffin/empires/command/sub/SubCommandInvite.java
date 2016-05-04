package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresJoinableIsEmpireException;
import com.pixelgriffin.empires.handler.Empire;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.Kingdom;
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.util.IDUtility;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandInvite extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				//gather info
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot invite people to " + PlayerHandler.m_defaultCiv);
					return false;
				}
				
					Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
					//do we have permission?
					//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.INVITE, invokerRole)) {
					if(joined.getPermissionForRole(invokerRole, GroupPermission.INVITE)) {
						UUID otherID = IDUtility.getUUIDForPlayer(_args[0]);
						if(otherID == null) {
							setError(ChatColor.RED + "Could not find the player '" + _args[0] + "'");
							return false;
						}
						
						//is the arg0 a player?
						if(Empires.m_playerHandler.getPlayerExists(otherID)) {
							//handle player
							//did we request them?
							//if(Empires.m_joinableHandler.getJoinableRequestedPlayer(joinedName, otherID)) {
							if(joined.isPlayerInvited(otherID)) {
								//remove them from the request list
								//Empires.m_joinableHandler.invokeJoinableRemoveRequestedPlayer(joinedName, otherID);
								joined.uninvitePlayer(otherID);
								
								//gather display name..
								//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
								String displayName = joined.getDisplayName();
								//inform
								Player invited = Bukkit.getPlayer(_args[0]);
								invited.sendMessage(ChatColor.YELLOW + "You are no longer invited to " + displayName);
								
								//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getDisplayName() + " uninvited " + invited.getName() + " to the civilization");
								joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " uninvited " + invited.getName() + " to the civilization");
								return true;
							} else {
								//add them to request list
								//Empires.m_joinableHandler.invokeJoinableRequestPlayer(joinedName, otherID);
								joined.invitePlayer(otherID);
								
								//gather display name..
								//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
								String displayName = joined.getDisplayName();
								//inform
								Player invited = Bukkit.getPlayer(_args[0]);
								invited.sendMessage(ChatColor.YELLOW + "You have been invited to join " + displayName);
								
								//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getDisplayName() + " invited " + invited.getName() + " to the civilization");
								joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " invited " + invited.getName() + " to the civilization");
								return true;
							}
						}
						
						Joinable other = Empires.m_joinableHandler.getJoinable(_args[0]);
						//if(Empires.m_joinableHandler.getJoinableExists(_args[0])) {//we're talking a joinable here
						if(other != null) {
							//handle inviting a joinable
							//are we an empire?
							//if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
							if(joined.isEmpire()) {
								//is the other an empire too?
								if(!other.isEmpire()) {
								//are we a leader?
								if(invokerRole.equals(Role.LEADER)) {
									//invite kingdoms to empire
										//did we request them yet?
										//if(!Empires.m_joinableHandler.getEmpireRequestedKingdom(joinedName, _args[0])) {
										Empire eUs = (Empire)joined;
										Kingdom kOther = (Kingdom)other;
										if(eUs.isKingdomInvited(kOther)) {
											//request
											//Empires.m_joinableHandler.invokeEmpireRequestKingdom(joinedName, _args[0]);
											eUs.inviteKingdom(kOther);
											
											//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(_args[0]);
											String displayName = kOther.getDisplayName();
											
											//inform
											//us
											//Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + displayName + " was invited to join the empire");
											eUs.broadcastToEmpire(ChatColor.YELLOW + displayName + " was invited to join the empire");
											//them
											//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_args[0], ChatColor.YELLOW + displayName + " was invited to join the " + joinedName + " empire");
											kOther.broadcastMessageToJoined(ChatColor.YELLOW + displayName + " was invited to join the " + joinedName + " empire");
											
											return true;
										} else {//guess we did
											//un-request
											//Empires.m_joinableHandler.invokeEmpireRemoveRequestedKingdom(joinedName, _args[0]);
											eUs.uninviteKingdom(kOther);
											
											//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(_args[0]);
											String displayName = kOther.getDisplayName();
											
											//inform
											//us
											//Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + displayName + " is no longer invited to the empire");
											eUs.broadcastToEmpire(ChatColor.YELLOW + displayName + " is no longer invited to the empire");
											//them
											//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_args[0], ChatColor.YELLOW + displayName + " is no longer invited to the " + joinedName + "empire");
											kOther.broadcastMessageToJoined(ChatColor.YELLOW + displayName + " is no longer invited to the " + joinedName + "empire");
											
											return true;
										}
								}
								
								setError("You must be the leader to invite kingdoms to the Empire!");
								return false;
								}
								
								setError("You cannot invite other Empires to join your Empire!");
								return false;
							}
							
							setError("You must be an empire to invite kingdoms!");
							return false;
						}
						//could not find an existing joinable or player
						setError("Could not find a reference for '" + _args[0] + "'");
						return false;
					}
					
					setError("You do not have permission to invite players!");
					return false;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("The command 'invite' can only be executed by players");
		return false;
	}

}
