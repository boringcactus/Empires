package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.util.IDUtility;

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
			EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(invoker.getUniqueId());
			//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invoker.getUniqueId());
			Joinable joined = ep.getJoined();
			
			if(!invoker.hasPermission("Empires.force.leader")) {
				setError("You don not have permission to force leader!");
				return false;
			}
			
			//if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
			if(joined == null) {
				setError("You cannot force leader in " + PlayerHandler.m_defaultCiv);
				return false;
			}
			
			//if(Empires.m_playerHandler.getPlayerRole(invoker.getUniqueId()).equals(Role.LEADER) || invoker.hasPermission("Empires.force.leader")) {
			if(ep.getRole().equals(Role.LEADER) || invoker.hasPermission("Empires.force.leader")) {
				//set the old leader as a member
				/*Empires.m_playerHandler.setPlayerRole(
						Empires.m_joinableHandler.getJoinable(joinedName).getLeader(), Role.MEMBER);*/
						//Empires.m_joinableHandler.getJoinableLeader(joinedName), Role.MEMBER);
				//set us as the new leader
				//Empires.m_playerHandler.setPlayerRole(invoker.getUniqueId(), Role.LEADER);
				if(_args.length == 0) {
					Empires.m_playerHandler.getPlayer(joined.getLeader()).setRole(Role.MEMBER);
					ep.setRole(Role.LEADER);
				
					invoker.sendMessage(ChatColor.YELLOW + "You are now the leader!");
				
					return true;
				} else if(_args.length == 1) {
					UUID otherP = IDUtility.getUUIDForPlayer(_args[0]);
					if(otherP != null) {
						EmpiresPlayer otherEP = Empires.m_playerHandler.getPlayer(otherP);
						if(otherEP != null) {
							if(otherEP.getJoined() != null) {
								if(otherEP.getJoined().getName().equalsIgnoreCase(joined.getName())) {
									Empires.m_playerHandler.getPlayer(joined.getLeader()).setRole(Role.MEMBER);
									otherEP.setRole(Role.LEADER);
									
									invoker.sendMessage(ChatColor.YELLOW + _args[0] + " is now the leader!");
									return true;
								}
							}
							
							setError(_args[0] + " is not in your civilization!");
							return false;
						}
					}
					
					setError("Could not find player '" + _args[0] + "'");
					return false;
				}
				
				setError("Invalid arguments!");
				return false;
			} else {
				setError("You are not allowed to choose a new leader!");
				return false;
			}
		}
		
		setError("Only players can invoke the 'leader' command");
		return false;
	}

}
