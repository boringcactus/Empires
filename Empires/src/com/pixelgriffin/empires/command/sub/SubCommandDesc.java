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
public class SubCommandDesc extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length > 0) {
				Player invoker = (Player)_sender;
				String invokerName = invoker.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				//default does not have a desc!
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot change " + PlayerHandler.m_defaultCiv + " description");
					return false;
				}
				
				try {
					//do we have permission?
					Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
					if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.SET_DESC, invokerRole)) {
						//build description
						String desc = "";
						for(int i = 0; i < _args.length; i++) {
							desc = desc + " "+_args[i];
						}
						
						desc = desc.substring(1);
						
						//set description
						Empires.m_joinableHandler.setJoinableDescription(joinedName, desc);
						
						//inform everyone
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " updated the civilization description!");
						
						return true;//success!
					}
					
					setError("You don't have permission to change the description!");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'desc' command");
		return false;
	}

}
