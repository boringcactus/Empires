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
			UUID invokerID = invoker.getUniqueId();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			
			//no default civ actions
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot have " + PlayerHandler.m_defaultCiv + " secede");
				return false;
			}
			
			try {
				//empire exists
				String empireName = Empires.m_joinableHandler.getKingdomEmpire(joinedName);
				if(!empireName.equals("")) {
					Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
					
					//player has permission
					if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.SECEDE, invokerRole)) {
						//secede
						Empires.m_joinableHandler.invokeKingdomSecedeEmpire(joinedName);
						
						//inform everyone
						String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
						Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(empireName, ChatColor.YELLOW + displayName + " has seceded from the empire.");
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + "We have seceded from our empire.");
						
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
