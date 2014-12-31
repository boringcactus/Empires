package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
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
				String invokerName = invoker.getName();
				String otherName = _args[0].toLowerCase();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				//no default civ actions
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot remove " + PlayerHandler.m_defaultCiv);
					return false;
				}
				
				try {
					//are we an empire?
					if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
						Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
						
						//are we the leader?
						if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, invokerRole, GroupPermission.REMOVE)) {
							//do we have that kingdom in our empire?
							if(Empires.m_joinableHandler.getEmpireKingdomList(joinedName).contains(otherName)) {
								//remove them
								Empires.m_joinableHandler.invokeKingdomSecedeEmpire(otherName);
								
								//inform everyone
								String displayName = Empires.m_joinableHandler.getJoinableDisplayName(otherName);
								Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + displayName + " has been removed from the empire.");
								Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(otherName, ChatColor.YELLOW + "We have seceded from our empire.");
								
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
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong");
					return false;
				}
			}
			
			setError("Invalid arguments");
			return false;
		}
		
		setError("The command 'remove' can only be executed by a player");
		return false;
	}
}
