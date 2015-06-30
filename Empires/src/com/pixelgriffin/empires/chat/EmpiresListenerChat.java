package com.pixelgriffin.empires.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.Relation;
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
		//Bukkit.getPluginManager().registerEvent(ChannelChatEvent.class, this, EventPriority.HIGHEST, new ChatFormatExecutor(), _inst, true);
		Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, EventPriority.LOWEST, new ChatFormatExecutor(), _inst, true);
	}
	
	private class ChatFormatExecutor implements EventExecutor {
		@Override
		public void execute(Listener arg0, Event _evt) throws EventException {
			try {
				if(AsyncPlayerChatEvent.class.isAssignableFrom(_evt.getClass())) {
					AsyncPlayerChatEvent asyncEvt = (AsyncPlayerChatEvent)_evt;
					
					parseVanilla(asyncEvt);
					
					/*System.out.println(asyncEvt.getFormat());
					
						String thisJoined = Empires.m_playerHandler.getPlayerJoinedCivilization(asyncEvt.getPlayer().getName());
						
						Set<Player> recepients = new HashSet<Player>(asyncEvt.getRecipients());
						asyncEvt.getRecipients().clear();
						
						String defaultFormat = asyncEvt.getFormat();
						
						for(Player p : recepients) {
							String otherJoined = Empires.m_playerHandler.getPlayerJoinedCivilization(p.getName());
							Relation rel = Empires.m_joinableHandler.getJoinableRelationTo(otherJoined, thisJoined);
							
							asyncEvt.setFormat(asyncEvt.getFormat().replace("{relation_color}", rel.getColor().toString()));
							String message = String.format(asyncEvt.getFormat(), asyncEvt.getPlayer().getDisplayName(), asyncEvt.getMessage());
							
								p.sendMessage(message);
							
							asyncEvt.setFormat(defaultFormat);
						}
						
					asyncEvt.setCancelled(true);*/
				} else if(ChannelChatEvent.class.isAssignableFrom(_evt.getClass())) {
					ChannelChatEvent chatEvt = (ChannelChatEvent)_evt;
					System.out.println(chatEvt.getFormat());
					parseHerochat(chatEvt);
					System.out.println(chatEvt.getFormat());
				}
			} catch(Throwable t) {
				throw new EventException(t);
			}
		}
		
		private void parseVanilla(AsyncPlayerChatEvent evt) {
			Player p = evt.getPlayer();
			
			String title = Empires.m_playerHandler.getPlayerTitle(p.getUniqueId());
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(p.getUniqueId());
			
			String joinedDisplayName = "";
			try {
				joinedDisplayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
			} catch (EmpiresJoinableDoesNotExistException e) {
			}
			
			Role role = Empires.m_playerHandler.getPlayerRole(p.getUniqueId());
			
			if(!joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
				//{relation_color}
				evt.setFormat("<§l"+role.getPrefix()+"§r"+joinedDisplayName+" "+title+" %1$s§f> %2$s");
			}
		}
		
		private void parseHerochat(ChannelChatEvent evt) {
			Player p = evt.getSender().getPlayer();
			
			String title = Empires.m_playerHandler.getPlayerTitle(p.getUniqueId());
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(p.getUniqueId());
			
			String joinedDisplayName = "";
			try {
				joinedDisplayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
			} catch (EmpiresJoinableDoesNotExistException e) {
			}
			
			Role role = Empires.m_playerHandler.getPlayerRole(p.getUniqueId());
			
			if(!joinedName.equalsIgnoreCase(PlayerHandler.m_defaultCiv)) {
				evt.setFormat("<§l"+role.getPrefix()+"§r"+joinedDisplayName+" "+title+" {relation_color}§f%1$s§f> %2$s");
			}
		}
		
		/*private void parseHerochat(ChannelChatEvent evt) {
			Player p = evt.getSender().getPlayer();
			
			String title = Empires.m_playerHandler.getPlayerTitle(p.getName());
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(p.getName());
			
			String joinedDisplayName = "";
			try {
				joinedDisplayName = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
			} catch (EmpiresJoinableDoesNotExistException e) {
			}
			
			Role role = Empires.m_playerHandler.getPlayerRole(p.getName());
			
			
			String format = evt.getFormat();
			format.replace("{emp_role_prefix}", );
		}*/
	}
}
