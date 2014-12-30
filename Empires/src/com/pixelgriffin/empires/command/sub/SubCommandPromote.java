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
public class SubCommandPromote extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				String invokerName = invoker.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				//can't work with default civ
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot promote people in the wilderness!");
					return false;
				}
				
				//do not allow them to promote themselves
				if(invokerName.equalsIgnoreCase(_args[0])) {
					setError("You can't promote yourself!");
					return false;
				}
				
				//gather invoker's role for later
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
				
				try {
					//check for the promote permission
					if(!Empires.m_joinableHandler.joinableHasPermissionForRole(joinedName, invokerRole, GroupPermission.PROMOTE)) {
						//no permission, terminate
						setError("You do not have permission to promote players!");
						return false;
					}
				} catch (EmpiresJoinableDoesNotExistException e) {//joinedName doesn't exist..
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
				
				//does the player exist? (don't want to create a blank player)
				if(Empires.m_playerHandler.playerExists(_args[0])) {
					//are they in our joinable?
					if(Empires.m_playerHandler.getPlayerJoinedCivilization(_args[0]).equalsIgnoreCase(joinedName)) {
						//gather role values
						int newRoleValue = Empires.m_playerHandler.getPlayerRole(_args[0]).getIntValue() + 1;
						int invokerRoleValue = invokerRole.getIntValue();
						
						//are we ranked high enough to promote them?
						if(newRoleValue < invokerRoleValue) {
							Role role = Role.getRoleFromInt(newRoleValue);
							//role is possibly null when newRoleValue is incorrect
							if(role != null) {
								try {
									//set the role
									Empires.m_playerHandler.setPlayerRole(_args[0], role);
									
									//inform everyone we set the role
									Empires.m_joinableHandler.broadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " promoted " + _args[0] + " to " + role.toString().toLowerCase().replaceAll("_", " ") + "!");
									
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
						
						setError("You cannot promote " + _args[0] + " any further!");
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
		
		setError("Only players can invoke the 'promote' command");
		return false;
	}

}
