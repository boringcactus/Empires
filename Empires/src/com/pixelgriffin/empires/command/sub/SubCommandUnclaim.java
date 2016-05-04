package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

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
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandUnclaim extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 0) {
				Player player = (Player)_sender;
				Location loc = player.getLocation();
				UUID invokerID = player.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				//are they part of the default civ?
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					//inform
					setError("You can't unclaim " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				Role playerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				try {
					Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
					//if the player has permission to unclaim
					//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.UNCLAIM, playerRole)) {
					if(joined.getPermissionForRole(playerRole, GroupPermission.UNCLAIM)) {
						
						//is this OUR territory?
						String host = Empires.m_boardHandler.getTerritoryHost(loc);
						if(!host.equalsIgnoreCase(joinedName)) {
							setError("You don't own this territory!");
							return false;
						}
						
						//unclaim
						Empires.m_boardHandler.unclaimTerritoryForJoinable(loc, joined);
						
						
						//let them know what we did!
						//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + player.getDisplayName() + " unclaimed land for you civilization at (...)");
						joined.broadcastMessageToJoined(ChatColor.YELLOW + player.getDisplayName() + " unclaimed land for you civilization at (" + loc.getChunk().getX() + ", " + loc.getChunk().getZ() + ")");
						return true;
					}
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					//joinedName does not exist in YML
					
					setError("Something went wrong...");
					return false;
				} catch (EmpiresEmptyTerritoryException e) {
					//there was no territory here
					//probably shouldn't happen.
					setError("You can't unclaim " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				setError("You do not have permission to unclaim territory!");
				return false;
			}
			
			setError("Too many arguments!");
			return false;
		}
		
		setError("The command 'unclaimall' can only be executed by a player");
		return false;
	}

}
