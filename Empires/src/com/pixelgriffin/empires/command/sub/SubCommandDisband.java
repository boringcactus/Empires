package com.pixelgriffin.empires.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandDisband extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		//we are trying to disband ourself
		if(_args.length == 0) {
			if(_sender instanceof Player) {
				//gather player information
				Player player = (Player)_sender;
				String playerName = player.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(playerName);
				
				//m_joinableHandler methods don't allow default to be targeted
				//but we should let the player know
				if(joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
					setError(PlayerHandler.m_defaultCiv + " cannot be disbanded!");
					return false;
				}
				
				//does the joinable exist?
				if(Empires.m_joinableHandler.getJoinableExists(joinedName)) {
					try {
						//player has permission to disband
						if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, Empires.m_playerHandler.getPlayerRole(playerName), GroupPermission.DISBAND)) {
							Empires.m_joinableHandler.invokeJoinableDisband(joinedName);//run disband
							
							//we disbanded successfully
							return true;
						}
						
						setError("You do not have permission to disband this civilization!");
						return false;
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
					}
				}
				
				setError("Could not find the civilization '" + joinedName +"'");
				return false;
			}
			
			setError("The command 'disband' with no arguments can only be ran by a player");
			return false;
		} else if(_args.length == 1) {//trying to disband someone else
			//check bukkit permission to disband someone else
			if(_sender.hasPermission("Empires.disband.other")) {
				
				//m_joinableHandler methods don't allow default to be targeted
				//but we should let the player know
				if(_args[0].equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
					setError(PlayerHandler.m_defaultCiv + " cannot be disbanded!");
					return false;
				}
				
				if(Empires.m_joinableHandler.getJoinableExists(_args[0])) {
					try {
						Empires.m_joinableHandler.invokeJoinableDisband(_args[0]);
						//successfully disbanded
						return true;
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						setError("The civilization '" + _args[0] + "' was found to not exist!");
						return false;
					}
				}
				
				setError("Could not find the civilization '" + _args[0] + "'");
				return false;
			}
			
			setError("You do not have permission to disband other civilizations!");
			return false;
		}
		return false;
	}

}
