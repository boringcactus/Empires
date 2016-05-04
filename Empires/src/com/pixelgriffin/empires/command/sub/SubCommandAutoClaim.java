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
public class SubCommandAutoClaim extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			UUID invokerID = invoker.getUniqueId();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot claim for wilderness!");
				return false;
			}
			
			Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
			
			//do we have permission to claim land?
			Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
			//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.CLAIM, invokerRole)) {
			if(!joined.getPermissionForRole(invokerRole, GroupPermission.CLAIM)) {
				setError("You do not have permission to claim land!");
				return false;
			}
			
			boolean val = !Empires.m_playerHandler.getPlayerAutoClaiming(invokerID);
			
			Empires.m_playerHandler.setPlayerAutoClaiming(invokerID, val);
			
			invoker.sendMessage(ChatColor.YELLOW + "Auto-claiming: " + val);
			
			return true;
		}
		
		setError("Only players can invoke the 'autoclaim' command");
		return false;
	}

}
