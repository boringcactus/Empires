package com.pixelgriffin.empires.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresListenerPlayerGeneral implements Listener {
	
	//when a player logs in
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent _evt) {
		//set their last played time to right now
		Empires.m_playerHandler.setPlayerLastPlayedTime(_evt.getPlayer().getUniqueId(), System.currentTimeMillis());
	}
	
	//when a player moves from one block to another
	@EventHandler
	public void onPlayerBlockChange(PlayerMoveEvent _evt) {
		Location to = _evt.getTo();
		Location from = _evt.getFrom();
		
		//return if we're still in the same block
		if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())
			return;
		
		//gather our TPID
		int tpid = Empires.m_playerHandler.getPlayerTPID(_evt.getPlayer().getUniqueId());
		
		//if our TPID is not null (-1) then stop the teleport
		if(tpid != -1) {
			Bukkit.getScheduler().cancelTask(tpid);//cancel the task in bukkit
			Empires.m_playerHandler.setPlayerTPID(_evt.getPlayer().getUniqueId(), -1);//remove our old TPID
			
			//inform
			_evt.getPlayer().sendMessage(ChatColor.RED + "Teleport cancelled");
		}
	}
	
	//when a player moves from one chunk to another
	@EventHandler
	public void onPlayerChunkChange(PlayerMoveEvent _evt) {
		//do not invoke when we have not moved from this chunk
		if(sameChunk(_evt))
			return;
		
		//gather player
		Player invoker = _evt.getPlayer();
		
		//this is a frequently called method
		//stop it when we don't need it
		//world is in blacklist, no territory can be claimed
		if(EmpiresConfig.m_blacklist.contains(invoker.getWorld().getName()))
			return;
		
		//gather player info
		UUID invokerID = invoker.getUniqueId();
		String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
		
		//host names
		String toHost, fromHost;
		
		{
			//get the TO position
			Location chunkLoc = _evt.getTo();
			
			//gather to host
			toHost = Empires.m_boardHandler.getTerritoryHost(chunkLoc);
			
			//get the FROM position
			chunkLoc = _evt.getFrom();
			
			//gather the last host
			fromHost = Empires.m_boardHandler.getTerritoryHost(chunkLoc);
		}
		
		//handle autoclaiming
		if(Empires.m_playerHandler.getPlayerAutoClaiming(invokerID)) {
			//run the claim command for us
			Bukkit.getServer().dispatchCommand(invoker, "e claim");
		}
		
		//do not display changed chunk information if the territory has not changed
		if(toHost.equals(fromHost))
			return;
		
		//the to location is the default civ
		if(toHost.equals(PlayerHandler.m_defaultCiv)) {
			//tell them it's wilderness
			invoker.sendMessage(ChatColor.DARK_GREEN + "~" + PlayerHandler.m_defaultCiv);
			return;
		}
		
		//since the new host is different and not wilderness
		//gather relationship
		Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
		Joinable host = Empires.m_joinableHandler.getJoinable(toHost);
		//gather relation
		//Relation rel = Empires.m_joinableHandler.getJoinableRelationTo(joinedName, toHost);
		Relation rel = Relation.NEUTRAL;
		if(joined != null)
			rel = joined.getRelation(host);
		
		//gather toHost display name
		//String displayName = Empires.m_joinableHandler.getJoinableDisplayName(toHost);
		if(host != null) {
			String displayName = host.getDisplayName();
			String description = host.getDescription();
			invoker.sendMessage(rel.getColor() + "~" + displayName + " - " + description);
		} else {
			invoker.sendMessage(ChatColor.DARK_GREEN + "~" + PlayerHandler.m_defaultCiv);
		}
		
		//gather toHost description
		//String description = Empires.m_joinableHandler.getJoinableDescription(toHost);
		//String description = host.getDescription();
		
		//print relation
		//invoker.sendMessage(rel.getColor() + "~" + displayName + " - " + description);
	}
	
	//checks if a player has moved from chunk to chunk
	private boolean sameChunk(PlayerMoveEvent evt) {
		Location a = evt.getFrom();
		Location b = evt.getTo();
		
		if(a.getWorld() != b.getWorld())
			return false;
		if(a.getBlockX() >> 4 != b.getBlockX() >> 4)
			return false;
		if(a.getBlockZ() >> 4 != b.getBlockZ() >> 4)
			return false;
		
		return true;
	}
	
	//power loss on death
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		//do we lose power on death?
		if(!EmpiresConfig.m_deathPowerLoss)
			return;
		
		//gather player
		Player invoker = evt.getEntity();
		UUID invokerID = invoker.getUniqueId();
		
		//does the player get to keep their power?
		if(!invoker.hasPermission("Empires.power.keep")) {
			//set
			Empires.m_playerHandler.setPlayerPower(invokerID, 0);
			
			//inform player
			invoker.sendMessage(ChatColor.RED + "Your power has been reduced to 0");
		} else {//yup
			//inform player they kept their power
			invoker.sendMessage(ChatColor.GREEN + "Your power has not been reduced!");
		}
	}
}
