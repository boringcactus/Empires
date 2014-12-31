package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandInherit extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				String invokerName = invoker.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				String heir = _args[0];
				
				//does the player exist?
				if(!Empires.m_playerHandler.playerExists(heir)) {
					setError("Could not find the player '" + heir +"'");
					return false;
				}
				
				//is the player in our joinable?
				if(!Empires.m_playerHandler.getPlayerJoinedCivilization(heir).equalsIgnoreCase(joinedName)) {
					setError("The player '" + heir + "' is not in your civilization!");
					return false;
				}
				
				//player exists and is in our civilization
				//set the heir
				try {
					Empires.m_joinableHandler.setJoinableHeir(joinedName, heir);
					
					//inform everyone of the new heir
					//if we're an empire inform EVERYONE
					if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
						Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + invokerName + " has declared " + heir + " as the heir of the empire!");
					} else {//not an empire, just inform us
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invokerName + " has declared " + heir + " as the heir of the kingdom!");
					}
				} catch(EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
				
				//worked out okay
				return true;
			}
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'inherit' command");
		return false;
	}

}
