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
public class SubCommandAutoClaim extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String invokerName = invoker.getName();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot claim for wilderness!");
				return false;
			}
			
			Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
			
			//do we have permission to claim land?
			try {
				if(!Empires.m_joinableHandler.joinableHasPermissionForRole(joinedName, invokerRole, GroupPermission.CLAIM)) {
					setError("You do not have permission to claim land!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
			boolean val = !Empires.m_playerHandler.isPlayerAutoClaiming(invokerName);
			
			Empires.m_playerHandler.setPlayerAutoClaiming(invokerName, val);
			
			invoker.sendMessage(ChatColor.YELLOW + "Auto-claiming: " + val);
			
			return true;
		}
		
		setError("Only players can invoke the 'autoclaim' command");
		return false;
	}

}
