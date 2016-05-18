package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.EmpiresPlayer;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandClaim extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			//gather player information
			Player player = (Player)_sender;
			EmpiresPlayer ep = Empires.m_playerHandler.getPlayer(player.getUniqueId());
			Location loc = player.getLocation();
			UUID playerID = player.getUniqueId();
			//String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(playerID);
			Joinable joined = ep.getJoined();
			
			//no default civ!
			//if(joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
			if(joined == null) {
				setError("You cannot claim for " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			{
				String worldName = loc.getWorld().getName();
				if(EmpiresConfig.m_blacklist.contains(worldName)) {
					setError(worldName + " has disabled claiming!");
					return false;
				}
			}
			
			//gather the current host at this chunk
			String currentHost = Empires.m_boardHandler.getTerritoryHost(loc);
			
			//Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
			
			//is it wilderness?
			if(!currentHost.equals(PlayerHandler.m_defaultCiv)) {
				if(currentHost.equalsIgnoreCase(joined.getName())) {
					setError("You already own this land!");
					return false;
				}
				
				//gather proper display name
				//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
				//String displayName = joined.getDisplayName();
				
				//don't overclaim!
				setError("You cannot claim over " + currentHost + "'s land!");
				return false;
			}
			
			//do we have permission to claim land?
			//Role invokerRole = Empires.m_playerHandler.getPlayerRole(playerID);
			//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.CLAIM, invokerRole)) {
			if(!joined.getPermissionForRole(ep.getRole(), GroupPermission.CLAIM)) {
				setError("You do not have permission to claim land!");
				return false;
			}
			
			//do we have the power to support this land?
			//int claimSize = Empires.m_joinableHandler.getJoinableClaimSize(joinedName);
			int claimSize = joined.getClaimSize();
			//int powerValue = Empires.m_joinableHandler.getJoinablePowerValue(joinedName);
			int powerValue = joined.getPower();
			
			if(claimSize >= powerValue) {
				setError("You do not have the power to support more land!");
				return false;
			}
			
			
			//can we not claim detached land?
			if(!EmpiresConfig.m_detachClaim) {
				
				//is our land not connected?
				if(!Empires.m_boardHandler.isLocationSurrounded(joined, loc)) {
					
					//is interworld claiming allowed?
					if(EmpiresConfig.m_interworldClaim) {
						
						//have we claimed in this land before?
						if(Empires.m_boardHandler.hasJoinableClaimedInWorld(loc.getWorld().getName(), joined)) {
							
							//guess so, better stop this!
							setError("You cannot claim disconnected land!");
							return false;
						}
					} else {//no interworld claiming
						setError("You cannot claim disconnected land!");
						return false;
					}
				}
			}
			
			//fire event
			/*EmpiresPlayerClaimEvent event = new EmpiresPlayerClaimEvent(player, joinedName);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
				return false;//stop executing since something told us not to
			*/
			
			//set this as our claim
			try {
				Empires.m_boardHandler.claimTerritoryForJoinable(loc, joined);
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Could not find a reference for " + joined.getDisplayName() + "!");
				return false;
			}
			
			//successfully claimed
			//let them know
			//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + player.getName() + " claimed land for your civilization!");
			joined.broadcastMessageToJoined(ChatColor.YELLOW + player.getName() + " claimed land for your civilization!");
		
			return true;
		}
		
		setError("The command 'claim' can only be ran by players");
		return false;
	}

}
