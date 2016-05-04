package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.Empire;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.Kingdom;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandRemove extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String otherName = _args[0].toLowerCase();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				//no default civ actions
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot remove " + PlayerHandler.m_defaultCiv);
					return false;
				}
				
				Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				
					//are we an empire?
					//if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
					if(joined.isEmpire()) {
						Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
						
						//are we the leader?
						//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.REMOVE, invokerRole)) {
						if(joined.getPermissionForRole(invokerRole, GroupPermission.REMOVE)) {
							//do we have that kingdom in our empire?
							Empire eUs = (Empire)joined;
							//if(Empires.m_joinableHandler.getEmpireKingdomList(joinedName).contains(otherName)) {
							if(eUs.getKingdomSet().contains(otherName)) {
								//remove them
								//Empires.m_joinableHandler.invokeKingdomSecedeEmpire(otherName);
								Kingdom other = (Kingdom)Empires.m_joinableHandler.getJoinable(otherName);
								other.leaveEmpire();
								
								//inform everyone
								/*String displayName = Empires.m_joinableHandler.getJoinableDisplayName(otherName);
								Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + displayName + " has been removed from the empire.");
								Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(otherName, ChatColor.YELLOW + "We have seceded from our empire.");*/
								String displayName = other.getDisplayName();
								eUs.broadcastToEmpire(ChatColor.YELLOW + displayName + " has been removed from the empire.");
								other.broadcastMessageToJoined(ChatColor.YELLOW + "We have seceded from our empire.");
								
								return true;
							}
							
							setError("You do not have the kingdom '" + otherName + "' in your Empire");
							return false;
						}
						
						setError("You do not have permission to remove Kingdoms from this Empire");
						return false;
					}
					//end
					
					setError("You do not belong to an empire");
					return false;
			}
			
			setError("Invalid arguments");
			return false;
		}
		
		setError("The command 'remove' can only be executed by a player");
		return false;
	}
}
