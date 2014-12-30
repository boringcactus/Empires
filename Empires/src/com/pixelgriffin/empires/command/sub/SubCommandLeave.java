package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandLeave extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String invokerName = invoker.getName();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You can't leave " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			try {
				//inform the others we left
				Empires.m_joinableHandler.broadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " left the civilization!");
				
				//actually remove us and remove our pointer to the old civ
				Empires.m_playerHandler.removePlayerFromJoinedCivilization(invokerName);
				
				return true;
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				
				return false;
			}
		}
		
		setError("The command 'leave' can only be executed by players");
		return false;
	}

}
