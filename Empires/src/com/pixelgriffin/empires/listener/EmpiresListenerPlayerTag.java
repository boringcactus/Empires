package com.pixelgriffin.empires.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresListenerPlayerTag implements Listener {
	
	@EventHandler
	public void onPlayerTagReceived(PlayerReceiveNameTagEvent _evt) {
		//gather involved players
		Player player = _evt.getPlayer();
		Player otherPlayer = _evt.getNamedPlayer();
		
		//gather joined names of involved players
		String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(player.getUniqueId());
		String otherJoinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(otherPlayer.getUniqueId());
		
		//the default color is the neutral value
		ChatColor relationColor = Relation.NEUTRAL.getColor();
		
		//if both players are in comparable joinables
		//(not in wilderness)
		if(joinedName != PlayerHandler.m_defaultCiv && otherJoinedName != PlayerHandler.m_defaultCiv) {
			try {
				relationColor = Empires.m_joinableHandler.getJoinableRelationTo(joinedName, otherJoinedName).getColor();
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();//keep for debug purposes
			}
		}
		
		_evt.setTag(relationColor + _evt.getTag());
	}
}
