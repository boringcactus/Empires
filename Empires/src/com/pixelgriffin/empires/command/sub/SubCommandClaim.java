package com.pixelgriffin.empires.command.sub;

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
			Location loc = player.getLocation();
			String playerName = player.getName();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(playerName);
			
			//no default civ!
			if(joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
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
			
			//is it wilderness?
			if(!currentHost.equals(PlayerHandler.m_defaultCiv)) {
				if(currentHost.equals(joinedName)) {
					setError("You already own this land!");
					return false;
				}
				
				try {
					//gather proper display name
					String displayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
					
					//don't overclaim!
					setError("You cannot claim over " + displayName + "'s land!");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					//TODO: fail gracefully
					
					setError("The civilization '" + currentHost + "' was found not to exist!");
					return false;
				}
			}
			
			//do we have permission to claim land?
			try {
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(playerName);
				if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.CLAIM, invokerRole)) {
					setError("You do not have permission to claim land!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
			//do we have the power to support this land?
			try {
				int claimSize = Empires.m_joinableHandler.getJoinableClaimSize(joinedName);
				int powerValue = Empires.m_joinableHandler.getJoinablePowerValue(joinedName);
				
				if(claimSize >= powerValue) {
					setError("You do not have the power to support more land!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
			}
			
			
			//can we not claim detached land?
			if(!EmpiresConfig.m_detachClaim) {
				
				//is our land not connected?
				if(!Empires.m_boardHandler.isLocationSurrounded(joinedName, loc)) {
					
					//is interworld claiming allowed?
					if(EmpiresConfig.m_interworldClaim) {
						
						//have we claimed in this land before?
						if(Empires.m_boardHandler.hasJoinableClaimedInWorld(loc.getWorld().getName(), joinedName)) {
							
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
				Empires.m_boardHandler.claimTerritoryForJoinable(loc, joinedName);
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Could not find a reference for " + joinedName + "!");
				return false;
			}
			
			//successfully claimed
			//let them know
			try {
				Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + player.getName() + " claimed land for your civilization!");
			} catch (EmpiresJoinableDoesNotExistException e) {//shouldn't happen.. but hey
				//joined name doesn't exist?
				e.printStackTrace();
			}
			
			return true;
		}
		
		setError("The command 'claim' can only be ran by players");
		return false;
	}

}
