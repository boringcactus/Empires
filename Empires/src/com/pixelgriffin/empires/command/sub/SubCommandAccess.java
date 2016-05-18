package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
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
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
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
				EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(invoker.getUniqueId());
				UUID invokerID = invoker.getUniqueId();
				Location invokerLoc = invoker.getLocation();
				//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				Joinable joined = ep.getJoined();
				 
				//if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				if(joined == null) {
					setError("You cannot give access to " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				//if(!Empires.m_playerHandler.getPlayerExists(Bukkit.getPlayer(_args[0]).getUniqueId())) {
				EmpiresPlayer other = Empires.m_playerHandler.getPlayer(Bukkit.getPlayer(_args[0]).getUniqueId());
				if(other == null) {
					setError("Could not find player '" + _args[0] + "'");
					return false;
				}
				
				//Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.ACCESS, invokerRole)) {
				if(!joined.getPermissionForRole(ep.getRole(), GroupPermission.ACCESS)) {
					setError("You do not have permission to give access to chunks!");
					return false;
				}
				
				if(Empires.m_boardHandler.getTerritoryHost(invokerLoc).equalsIgnoreCase(joined.getName())) {
					try {
						//toggle access for chunk
						boolean given = Empires.m_boardHandler.toggleTerritoryAccessFor(invokerLoc, invokerID);
						
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
