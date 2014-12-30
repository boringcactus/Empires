package com.pixelgriffin.empires.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresListenerChat implements Listener {
	
	public EmpiresListenerChat(Empires _inst) {
		Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, EventPriority.HIGHEST, new ChatFormatExecutor(), _inst, true);
	}
	
	private class ChatFormatExecutor implements EventExecutor {
		@Override
		public void execute(Listener arg0, Event arg1) throws EventException {
			try {
				if(!AsyncPlayerChatEvent.class.isAssignableFrom(arg1.getClass()))
					return;
				
				parse((AsyncPlayerChatEvent)arg1);
			} catch(Throwable t) {
				throw new EventException(t);
			}
		}
		
		private void parse(AsyncPlayerChatEvent evt) {
			Player p = evt.getPlayer();
			
			ChatColor relCol = ChatColor.WHITE;
			
			String title = Empires.m_playerHandler.getPlayerTitle(p.getName());
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(p.getName());
			
			String joinedDisplayName = "";
			try {
				joinedDisplayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
			} catch (EmpiresJoinableDoesNotExistException e) {
			}
			
			Role role = Empires.m_playerHandler.getPlayerRole(p.getName());
			
			/*if(newEP.getTitle() == null) {
				title = "";
			} else if(newEP.getTitle().equals("")) {
				title = "";
			} else {
				title = " "+newEP.getTitle();
			}*/
			
			if(!joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
				evt.setFormat("<"+relCol+"§l"+role.getPrefix()+"§r"+relCol+joinedDisplayName+" "+title+" §f%1$s> %2$s");
			}
		}
	}
}
