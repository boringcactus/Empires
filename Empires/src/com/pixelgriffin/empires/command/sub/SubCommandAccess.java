package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresEmptyTerritoryException;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandAccess extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				String invokerName = invoker.getName();
				Location invokerLoc = invoker.getLocation();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot give access to " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				if(!Empires.m_playerHandler.playerExists(_args[0])) {
					setError("Could not find player '" + _args[0] + "'");
					return false;
				}
				
				try {
					Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
					if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.ACCESS, invokerRole)) {
						setError("You do not have permission to give access to chunks!");
						return false;
					}
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
				
				if(Empires.m_boardHandler.getTerritoryHost(invokerLoc).equalsIgnoreCase(joinedName)) {
					try {
						//toggle access for chunk
						boolean given = Empires.m_boardHandler.toggleTerritoryAccessFor(invokerLoc, _args[0]);
						
						//inform
						invoker.sendMessage(ChatColor.YELLOW + _args[0] + " access set to: " + given);
						
						return true;
					} catch (EmpiresEmptyTerritoryException e) {
						e.printStackTrace();
						
						setError("Something went wrong!");
						return false;
					}
				}
				
				setError("You cannot give access to a chunk you don't own!");
				return false;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'access' command");
		return false;
	}
}
