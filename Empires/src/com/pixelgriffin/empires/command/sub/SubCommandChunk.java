package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresEmptyTerritoryException;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandChunk extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(invoker.getUniqueId());
			UUID invokerID = invoker.getUniqueId();
			//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			Joinable joined = ep.getJoined();
			Location invokerLoc = invoker.getLocation();
			
			//if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
			if(joined == null) {
				setError("You cannot change chunk flags for " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			//if(!joinedName.equalsIgnoreCase(Empires.m_boardHandler.getTerritoryHost(invokerLoc))) {
			if(!joined.getName().equalsIgnoreCase(Empires.m_boardHandler.getTerritoryHost(invokerLoc))) {
				setError("You cannot change chunk flags for territory you don't own!");
				return false;
			}
			
			//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
			//Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
			//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.PERMS, invokerRole)) {
			if(!joined.getPermissionForRole(ep.getRole(), GroupPermission.PERMS)) {
				setError("You do not have permission to edit chunk flags!");
				return false;
			}
			
			//we are displaying chunk flag values
			if(_args.length == 0) {
				ChatColor accessCol;
				String outString = "";
				
				for(TerritoryFlag f : TerritoryFlag.values()) {
					outString = ChatColor.GRAY + f.toString() + ": ";
					
					for(TerritoryGroup g : TerritoryGroup.values()) {
						accessCol = ChatColor.RED;
						
						if(Empires.m_boardHandler.territoryHasFlag(invokerLoc, g, f)) {
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
				
				//single value territory shit
				//IGNORE_RELATIONS
				outString = ChatColor.GRAY + "IGNORE_RELATIONS: ";
				boolean val = Empires.m_boardHandler.territoryIgnoresRelations(invokerLoc);
				
				accessCol = ChatColor.RED;
				if(val)
					accessCol = ChatColor.GREEN;
				
				invoker.sendMessage(outString + accessCol + String.valueOf(val).toUpperCase());
				
				//SPAWN_MOBS
				if(EmpiresConfig.m_mobSpawnManaging) {
					outString = ChatColor.GRAY + "SPAWN_MOBS: ";
					//val = Empires.m_joinableHandler.getJoinableAllowsMobs(joinedName);
					val = joined.getAllowsMobs();
					
					accessCol = ChatColor.RED;
					if(val)
					accessCol = ChatColor.GREEN;
					
					invoker.sendMessage(outString + accessCol + String.valueOf(val).toUpperCase());
				}
				
				return true;
				
			} else if(_args.length == 1) {//changing single value territory flag
				//single value flag
				if(_args[0].equalsIgnoreCase("IGNORE_RELATIONS")) {
					try {
						if(Empires.m_boardHandler.toggleTerritoryIgnoresRelations(invokerLoc)) {
							invoker.sendMessage(ChatColor.GRAY + "IGNORE_RELATIONS:" + ChatColor.GREEN + " TRUE");
						} else {
							invoker.sendMessage(ChatColor.GRAY + "IGNORE_RELATIONS:" + ChatColor.RED + " FALSE");
						}
						
						return true;
					} catch (EmpiresEmptyTerritoryException e) {
						setError("You cannot toggle IGNORE_RELATIONS on empty territory!");
						return false;
					}
				} else if(EmpiresConfig.m_mobSpawnManaging && _args[0].equalsIgnoreCase("SPAWN_MOBS")) {
					try {
						if(Empires.m_boardHandler.toggleTerritoryAllowsMobs(invokerLoc)) {
							invoker.sendMessage(ChatColor.GRAY + "SPAWN_MOBS:" + ChatColor.GREEN + " TRUE");
						} else {
							invoker.sendMessage(ChatColor.GRAY + "SPAWN_MOBS:" + ChatColor.RED + " FALSE");
						}
						
						return true;
					} catch(EmpiresEmptyTerritoryException e) {
						setError("You cannot toggle SPAWN_MOBS on empty territory!");
						return false;
					}
				}
				
				setError("Could not find single-value flag '" + _args[0] +"'");
				return false;
			} else if(_args.length == 2) {//changing regular territory flag
				try {
					System.out.println("Chunkin'");
					TerritoryFlag f = TerritoryFlag.valueOf(_args[0].toUpperCase());
					TerritoryGroup g = TerritoryGroup.valueOf(_args[1].toUpperCase());
					
					ChatColor accessCol = ChatColor.RED;
					
					if(Empires.m_boardHandler.toggleTerritoryFlag(invokerLoc, g, f))
						accessCol = ChatColor.GREEN;
					
					invoker.sendMessage(ChatColor.GRAY + f.toString().toUpperCase() + ": " + accessCol + g.toString().toUpperCase());
					
					return true;
				} catch(IllegalArgumentException e) {
					setError("Could not find flag value for '" + _args[0].toUpperCase() + "' at '" + _args[1].toUpperCase() +"'");
					return false;
				} catch (EmpiresEmptyTerritoryException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'chunk' command");
		return false;
	}

}
