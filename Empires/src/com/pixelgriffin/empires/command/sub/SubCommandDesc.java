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
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandDesc extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length > 0) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				//default does not have a desc!
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot change " + PlayerHandler.m_defaultCiv + " description");
					return false;
				}
				
				Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				
				//do we have permission?
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.SET_DESC, invokerRole)) {
				if(joined.getPermissionForRole(invokerRole, GroupPermission.SET_DESC)) {
					//build description
					String desc = "";
					for(int i = 0; i < _args.length; i++) {
						desc = desc + " "+_args[i];
					}
					
					desc = desc.substring(1);
					
					//set description
					//Empires.m_joinableHandler.setJoinableDescription(joinedName, desc);
					joined.setDescription(desc);
					
					//inform everyone
					//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getDisplayName() + " updated the civilization description!");
					joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " updated the civilization description!");
					
					return true;//success!
				}
				
				setError("You don't have permission to change the description!");
				return false;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'desc' command");
		return false;
	}

}
