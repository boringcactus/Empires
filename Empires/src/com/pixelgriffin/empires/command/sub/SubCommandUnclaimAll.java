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
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandUnclaimAll extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 0) {
				Player player = (Player)_sender;
				EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(player.getUniqueId());
				UUID invokerID = player.getUniqueId();
				//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				Joinable joined = ep.getJoined();
				
				//are they part of the default civ?
				//if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				if(joined == null) {
					//inform
					setError("You can't unclaim " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				//Role playerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				try {
					//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
					//if the player has permission to unclaim
					//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.UNCLAIM, playerRole)) {
					if(joined.getPermissionForRole(ep.getRole(), GroupPermission.UNCLAIM)) {
						
						//remove all territory
						Empires.m_boardHandler.unclaimAllTerritoryForJoinable(joined);
						
						//let them know what we did!
						//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + player.getDisplayName() + " unclaimed all land for you civilization!");
						joined.broadcastMessageToJoined(ChatColor.YELLOW + player.getDisplayName() + " unclaimed all land for you civilization!");
						return true;
					}
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					//joinedName does not exist in YML
					
					setError("Something went wrong...");
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
