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
public class SubCommandSecede extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String invokerName = invoker.getName();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
			
			//no default civ actions
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot have " + PlayerHandler.m_defaultCiv + " secede");
				return false;
			}
			
			try {
				//empire exists
				String empireName = Empires.m_joinableHandler.getJoinableEmpire(joinedName);
				if(!empireName.equals("")) {
					Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
					
					//player has permission
					if(Empires.m_joinableHandler.joinableHasPermissionForRole(joinedName, invokerRole, GroupPermission.SECEDE)) {
						//secede
						Empires.m_joinableHandler.joinableSecedeEmpire(joinedName);
						
						//inform everyone
						String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
						Empires.m_joinableHandler.broadcastToEmpireNetwork(empireName, ChatColor.YELLOW + displayName + " has seceded from the empire.");
						Empires.m_joinableHandler.broadcastToJoined(joinedName, ChatColor.YELLOW + "We have seceded from our empire.");
						
						return true;
					}
					
					setError("You do not have permission to declare secession!");
					return false;
				}
				
				setError("You do not belong to an empire");
				return false;
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong");
				return false;
			}
		}
		
		setError("The command 'secede' can only be executed by a player");
		return false;
	}

}
