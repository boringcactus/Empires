package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandLeader extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invoker.getUniqueId());
			
			if(!invoker.hasPermission("Empires.force.leader")) {
				setError("You don not have permission to force leader!");
				return false;
			}
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot force leader in " + PlayerHandler.m_defaultCiv);
				return false;
			}
			
			try {
				//set the old leader as a member
				Empires.m_playerHandler.setPlayerRole(
						Empires.m_joinableHandler.getJoinable(joinedName).getLeader(), Role.MEMBER);
						//Empires.m_joinableHandler.getJoinableLeader(joinedName), Role.MEMBER);
				
				//set us as the new leader
				Empires.m_playerHandler.setPlayerRole(invoker.getUniqueId(), Role.LEADER);
				
				invoker.sendMessage(ChatColor.YELLOW + "You are now the leader!");
				
				return true;
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
		}
		
		setError("Only players can invoke the 'leader' command");
		return false;
	}

}
