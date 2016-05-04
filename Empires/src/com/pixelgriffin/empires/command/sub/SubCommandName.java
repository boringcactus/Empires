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
import com.pixelgriffin.empires.exception.EmpiresJoinableInvalidCharacterException;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandName extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot rename " + PlayerHandler.m_defaultCiv + "!");
					return false;
				}
				
				Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.RENAME, invokerRole)) {
				if(!joined.getPermissionForRole(invokerRole, GroupPermission.RENAME)) {
					setError("You do not have permission to rename your civilization!");
					return false;
				}
				
				Joinable other = Empires.m_joinableHandler.getJoinable(_args[0]);
				//if(Empires.m_joinableHandler.getJoinableExists(_args[0])) {
				if(other != null) {
					setError("A civilization with the name '" + _args[0] + "' already exists!");
					return false;
				}
				
				//check for colors and permission
				if(!invoker.hasPermission("Empires.colors.joinable")) {
					if(_args[0].contains("&")) {
						setError("You do not have permission to put colors in civilization names!");
						return false;
					}
				}
				
				//update name
				//Empires.m_joinableHandler.setJoinableName(joinedName, _args[0]);
				joined.setName(_args[0]);
				
				//inform
				//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_args[0], ChatColor.YELLOW + invoker.getDisplayName() + " renamed the civilization to '" + _args[0] + "!'");
				joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " renamed the civilization to '" + _args[0] + "!'");
				
				return true;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'name' command");
		return false;
	}

}
