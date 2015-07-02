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
public class SubCommandKick extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				String other = _args[0];
				UUID otherID = IDUtility.getUUIDForPlayer(other);
				if(otherID == null) {
					setError(ChatColor.RED + "Could not find the player '" + other + "'");
					return false;
				}
				
				//can't invoke on the default civ!
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You can't kick people from " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				//not in the same joinable?
				if(!Empires.m_playerHandler.getPlayerJoinedCivilization(otherID).equalsIgnoreCase(joinedName)) {
					setError(other + " is not in your civilization!");
					return false;
				}
				
				//gather role
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				try {
					//do they have permission to kick?
					if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.KICK, invokerRole)) {
						//are they ranked higher than the one they're trying to kick?
						Role otherRole = Empires.m_playerHandler.getPlayerRole(otherID);
						
						//yes
						if(otherRole.getIntValue() < invokerRole.getIntValue()) {
							//inform everyone
							Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getDisplayName() + " kicked " + other + " from the civilization!");
							
							//remove them
							Empires.m_playerHandler.invokeRemovePlayerFromJoinedJoinable(otherID);
							
							return true;//success
						}
						
						setError("You cannot kick someone with the same or higher rank as yours!");
						return false;
					}
					
					setError("You do not have permission to kick people!");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'kick' command");
		return false;
	}

}
