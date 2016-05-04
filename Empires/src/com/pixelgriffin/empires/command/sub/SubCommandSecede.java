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
import com.pixelgriffin.empires.handler.Empire;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.Kingdom;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandSecede extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			UUID invokerID = invoker.getUniqueId();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			
			//no default civ actions
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot have " + PlayerHandler.m_defaultCiv + " secede");
				return false;
			}
			
			Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
			//empire exists
			//String empireName = Empires.m_joinableHandler.getKingdomEmpire(joinedName);
			if(!joined.isEmpire()) {
				Kingdom kUs = (Kingdom)joined;
				if(!kUs.getEmpire().isEmpty()) {
					Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
					
					//player has permission
					//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.SECEDE, invokerRole)) {
					if(joined.getPermissionForRole(invokerRole, GroupPermission.SECEDE)) {
						//secede
						//Empires.m_joinableHandler.invokeKingdomSecedeEmpire(joinedName);
						Empire other = (Empire)Empires.m_joinableHandler.getJoinable(kUs.getEmpire());
						kUs.leaveEmpire();
						
						//inform everyone
						/*String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
						Empires.m_joinableHandler.invokeEmpireBroadcastToNetwork(empireName, ChatColor.YELLOW + displayName + " has seceded from the empire.");
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + "We have seceded from our empire.");*/
						String displayName = joined.getDisplayName();
						other.broadcastToEmpire(ChatColor.YELLOW + displayName + " has seceded from the empire.");
						joined.broadcastMessageToJoined(ChatColor.YELLOW + "We have seceded from our empire.");
						
						return true;
					}
					
					setError("You do not have permission to declare secession!");
					return false;
				}
				
				setError("You do not belong to an empire");
				return false;
			}
			
			setError("Empires cannot secede from anything!");
			return false;
		}
		
		setError("The command 'secede' can only be executed by a player");
		return false;
	}

}
