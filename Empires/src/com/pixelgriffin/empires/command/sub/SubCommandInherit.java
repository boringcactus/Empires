package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.Empire;
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.util.IDUtility;

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
				EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(invoker.getUniqueId());
				UUID invokerID = invoker.getUniqueId();
				//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				Joinable joined = ep.getJoined();
				
				String heir = _args[0];
				UUID heirID = IDUtility.getUUIDForPlayer(_args[0]);
				if(heirID == null) {
					setError(ChatColor.RED + "Could not find the player '" + _args[0] + "'");
					return false;
				}
				
				//does the player exist?
				//if(!Empires.m_playerHandler.getPlayerExists(heirID)) {
				EmpiresPlayer heirEP = Empires.m_playerHandler.getPlayer(heirID);
				if(heirEP == null) {
					setError("Could not find the player '" + heir +"'");
					return false;
				}
				
				//is the player in our joinable?
				//if(!Empires.m_playerHandler.getPlayerJoinedCivilization(heirID).equalsIgnoreCase(joinedName)) {
				if(heirEP.getJoined() != null) {
					if(heirEP.getJoined().getName().equalsIgnoreCase(joined.getName())) {
						setError("The player '" + heir + "' is not in your civilization!");
						return false;
					}
				}
				
				//player exists and is in our civilization
				//set the heir
				//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				//Empires.m_joinableHandler.setJoinableHeir(joinedName, heirID);
				joined.setHeir(heirID);
				
				//inform everyone of the new heir
				//if we're an empire inform EVERYONE
				/*if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
					Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(joinedName, ChatColor.YELLOW + invoker.getName() + " has declared " + heir + " as the heir of the empire!");
				} else {//not an empire, just inform us
					Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getName() + " has declared " + heir + " as the heir of the kingdom!");
				}*/
				if(joined.isEmpire()) {
					((Empire)joined).broadcastToEmpire(ChatColor.YELLOW + invoker.getName() + " has declared " + heir + " as the heir of the empire!");
				} else {
					joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getName() + " has declared " + heir + " as the heir of the kingdom!");
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
