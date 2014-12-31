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
public class SubCommandTitle extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 2) {
				//limit characters in titles
				if(_args[1].length() > 10) {
					setError("Titles cannot be longer than 10 characters");
					return false;
				}
				
				//check to stop any unwanted special characters
				if(_args[1].contains("&o") || _args[1].contains("&k") || _args[1].contains("&l") || _args[1].contains("&n") || _args[1].contains("&m")) {
					setError("You cannot specialize a title's text!");
					return false;
				}
				
				
				Player invoker = (Player)_sender;
				
				//gather invoker joined name
				String invokerJoinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invoker.getName());
				//gather invoker role
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invoker.getName());
				
				//does the invoker actually have a civilization?
				if(invokerJoinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You belong to the wilderness!");
					return false;
				}
				
				//does the invoker have permission to set titles?
				try {
					if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(invokerJoinedName, GroupPermission.SET_TITLE, invokerRole)) {
						setError("You do not have permission to set titles!");
						return false;
					}
				} catch(EmpiresJoinableDoesNotExistException e) {//player apparently holds a pointer to a non-existent joinable
					setError("You belong to the wilderness!");
					e.printStackTrace();
					return false;
				}
			
				//is the player in our joinable?
				String selectedJoinedName;
				
				//the invoker could have typed gibberish, make sure the player exists so we don't create a new one by mistake
				if(Empires.m_playerHandler.getPlayerExists(_args[0])) {
					selectedJoinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(_args[0]);
				} else {
					setError("Couldn't find the player '" + _args[0] + "'");
					return false;
				}
				
				//make sure they're not in the default civ (wilderness)
				if(selectedJoinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
					setError(_args[0] + " belongs to the wilderness!");
					return false;
				}
				
				//make sure they're in our joinable
				if(!invokerJoinedName.equalsIgnoreCase(selectedJoinedName)) {
					setError(_args[0] + " is not in your civilization!");
					return false;
				}
				
				//since the player has permission & the player whose title will be set is in the right joinable
				//we also know the other player exists
				//we set the title value
				try {
					Empires.m_playerHandler.setPlayerTitle(_args[0], _args[1]);
				} catch (EmpiresJoinableDoesNotExistException e) {
					setError("The player " + _args[0] + " is not in a civilization!");
					return false;
				}
				
				//set the title successfully
				//let everyone know about the new title
				try {
					Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(invokerJoinedName, ChatColor.YELLOW + _args[0] + "'s title has been set to '" + _args[1] + "'");
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
				}
				return true;
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("The command 'title' can only be executed by a player");
		return false;
	}

}
