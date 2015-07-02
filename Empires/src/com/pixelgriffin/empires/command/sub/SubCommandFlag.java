package com.pixelgriffin.empires.command.sub;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandFlag extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			UUID invokerID = invoker.getUniqueId();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot change chunk flags for " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			try {
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.PERMS, invokerRole)) {
					setError("You do not have permission to edit flags!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
			if(_args.length == 0) {
				invoker.sendMessage(ChatColor.GRAY + "Global Flag Settings:");
				try {
					ArrayList<String> flags;
					String outString = "";
					ChatColor accessCol;
					
					for(TerritoryFlag f : TerritoryFlag.values()) {
						outString = ChatColor.GRAY + f.toString() + ": ";
						
						for(TerritoryGroup g : TerritoryGroup.values()) {
							flags = Empires.m_joinableHandler.getJoinableGlobalFlagsForGroup(joinedName, g);
							accessCol = ChatColor.RED;
							
							if(flags.contains(f.toString())) {
								accessCol = ChatColor.GREEN;
							}
							
							if(g.equals(TerritoryGroup.OFFICER_1)) {
								outString = outString + ChatColor.GRAY + " OFFICER("+ accessCol +"1";
							} else if(g.equals(TerritoryGroup.OFFICER_2)) {
								outString = outString + ChatColor.GRAY + "," + accessCol + "2";
							} else if(g.equals(TerritoryGroup.OFFICER_3)) {
								outString = outString + ChatColor.GRAY + "," + accessCol + "3" + ChatColor.GRAY + ")";
							} else {
								outString = outString + " " + accessCol + g.toString();
							}
						}
						
						invoker.sendMessage(outString);
					}
					
					//single value territory
					//IGNORE_RELATIONS
					outString = ChatColor.GRAY + "IGNORE_RELATIONS: ";
					boolean val = Empires.m_joinableHandler.getJoinableIgnoresRelations(joinedName);
					
					accessCol = ChatColor.RED;
					if(val)
						accessCol = ChatColor.GREEN;
					
					invoker.sendMessage(outString + accessCol + String.valueOf(val).toUpperCase());
					
					//SPAWN_MOBS
					if(EmpiresConfig.m_mobSpawnManaging) {
						outString = ChatColor.GRAY + "SPAWN_MOBS: ";
						val = Empires.m_joinableHandler.getJoinableAllowsMobs(joinedName);
						
						accessCol = ChatColor.RED;
						if(val)
							accessCol = ChatColor.GREEN;
						
						invoker.sendMessage(outString + accessCol + String.valueOf(val).toUpperCase());
					}
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
				
				return true;
				
			} else if(_args.length == 1) {
				if(_args[0].equalsIgnoreCase("IGNORE_RELATIONS")) {
					try {
						boolean val = Empires.m_joinableHandler.toggleJoinableIgnoresRelations(joinedName);
						ChatColor col = ChatColor.RED;
						
						if(val)
							col = ChatColor.GREEN;
						
						invoker.sendMessage(ChatColor.GRAY + "IGNORE_RELATIONS: " + col + String.valueOf(val).toUpperCase());
						
						return true;
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						
						setError("Something went wrong!");
						return false;
					}
				} else if(EmpiresConfig.m_mobSpawnManaging && _args[0].equalsIgnoreCase("SPAWN_MOBS")) {
					try {
						boolean val = Empires.m_joinableHandler.toggleJoinableAllowsMobs(joinedName);
						ChatColor col = ChatColor.RED;
						
						if(val)
							col = ChatColor.GREEN;
						
						invoker.sendMessage(ChatColor.GRAY + "SPAWN_MOBS: " + col + String.valueOf(val).toUpperCase());
						
						return true;
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						
						setError("Something went wrong!");
						return false;
					}
				}
				
				setError("Could not find single-value flag '" + _args[0] + "'");
				return false;
			} else if(_args.length == 2) {
				try {
					TerritoryFlag f = TerritoryFlag.valueOf(_args[0].toUpperCase());
					TerritoryGroup g = TerritoryGroup.valueOf(_args[1].toUpperCase());
					
					boolean val = Empires.m_joinableHandler.toggleJoinableFlag(joinedName, g, f);
					ChatColor accessCol = ChatColor.RED;
					
					if(val)
						accessCol = ChatColor.GREEN;
					
					invoker.sendMessage(ChatColor.GRAY + f.toString().toUpperCase() + ": " + accessCol + g.toString().toUpperCase());
					
					return true;
				} catch(IllegalArgumentException e) {
					setError("Could not find flag value for '" + _args[0].toUpperCase() + "' at '" + _args[1].toUpperCase() +"'");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'flag' command");
		return false;
	}
}
