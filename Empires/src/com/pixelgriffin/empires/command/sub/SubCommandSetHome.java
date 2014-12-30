package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class SubCommandSetHome extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String invokerName = invoker.getName();
			Location invokerLoc = invoker.getLocation();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot set the home of " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			try {
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
				if(!Empires.m_joinableHandler.joinableHasPermissionForRole(joinedName, invokerRole, GroupPermission.SET_HOME)) {
					setError("You do not have permission to set the home of your civilization!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
			if(!Empires.m_boardHandler.getTerritoryHost(invokerLoc).equalsIgnoreCase(joinedName)) {
				setError("You can only set your home on your land!");
				return false;
			}
			
			try {
				Empires.m_joinableHandler.setJoinableHome(joinedName, invokerLoc);
				
				Empires.m_joinableHandler.broadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " has set a new home for the civilization!");
				
				return true;
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
		}
		
		setError("Only players can invoke the 'sethome' method");
		return false;
	}

}
