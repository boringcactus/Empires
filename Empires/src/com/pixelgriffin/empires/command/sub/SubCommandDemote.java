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
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.util.IDUtility;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandDemote extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				UUID otherID = IDUtility.getUUIDForPlayer(_args[0]);
				if(otherID == null) {
					setError(ChatColor.RED + "Could not find the player '" + _args[0] + "'");
					return false;
				}
				String invokerName =  invoker.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				//can't work with default civ
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot demote people in the wilderness!");
					return false;
				}
				
				//do not allow them to promote themselves
				if(invokerName.equalsIgnoreCase(_args[0])) {
					setError("You can't demote yourself!");
					return false;
				}
				
				//gather invoker's role for later
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				try {
					//check for the promote permission
					if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.DEMOTE, invokerRole)) {
						//no permission, terminate
						setError("You do not have permission to demote players!");
						return false;
					}
				} catch (EmpiresJoinableDoesNotExistException e) {//joinedName doesn't exist..
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
				
				//does the player exist? (don't want to create a blank player)
				if(Empires.m_playerHandler.getPlayerExists(otherID)) {
					//are they in our joinable?
					if(Empires.m_playerHandler.getPlayerJoinedCivilization(otherID).equalsIgnoreCase(joinedName)) {
						//gather role values
						Role otherRole = Empires.m_playerHandler.getPlayerRole(otherID);
						
						if(otherRole.equals(Role.MEMBER)) {
							setError("You can't demote " + _args[0] + " any further!");
							return false;
						}
						
						int otherRoleValue = otherRole.getIntValue();
						int invokerRoleValue = invokerRole.getIntValue();
						
						//are we ranked high enough to demote them?
						if(otherRoleValue < invokerRoleValue) {//ex: officer_1 (1) < officer_2 (2) OK
							Role role = Role.getRoleFromInt(otherRoleValue - 1);//other role minus one

							//role is possibly null when newRoleValue is incorrect
							if(role != null) {
								try {
									//set the role
									Empires.m_playerHandler.setPlayerRole(otherID, role);
									
									//inform everyone we set the role
									Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " demoted " + _args[0] + " to " + role.toString().toLowerCase().replaceAll("_", " ") + "!");
									
								} catch (EmpiresJoinableDoesNotExistException e) {
									e.printStackTrace();
									
									setError("Something went wrong!");
									return false;
								}
								
								//success!
								return true;
							}
							
							setError("Something went wrong with role assignment!");
							return false;
						}
						
						setError("You cannot demote " + _args[0] + " any further!");
						return false;
					}
					
					setError(_args[0] + " is not part of your civilization!");
					return false;
				}
				
				setError("Could not find the player '" + _args[0] + "'");
				return false;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'demote' command");
		return false;
	}

}
