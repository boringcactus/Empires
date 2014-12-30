package com.pixelgriffin.empires.command.sub;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandMap extends SubCommand {

	private final char[] repChars = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	private final int MAP_WIDTH = 39;
	private final int MAP_HEIGHT = 8;
	
	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invoker.getName());
			
			int cX = invoker.getLocation().getChunk().getX();
			int cZ = invoker.getLocation().getChunk().getZ();
			
			int halfW = MAP_WIDTH / 2;
			int halfH = MAP_HEIGHT /2;
			
			int w = MAP_WIDTH + 1;
			int h = MAP_HEIGHT + 1;
			
			int offX = cX - halfW;
			int offZ = cZ - halfH;
			
			String world = invoker.getWorld().getName();
			
			h--;
			
			Map<String, Character> js = new HashMap<String, Character>();
			int index = 0;
			
			String ln;
			String terHost;
			for(int lz = 0; lz < h; lz++) {
				ln = "";
				
				for(int lx = 0; lx < w; lx++) {
					if(lx == halfW && lz == halfH) {
						ln = ln + ChatColor.AQUA + "+";
						continue;
					}
					
					terHost = Empires.m_boardHandler.getTerritoryHost(offX + lx, offZ + lz, world);
					
					if(terHost.equals(PlayerHandler.m_defaultCiv)) {
						ln = ln + ChatColor.GRAY +"-";
					} else {
						if(!js.containsKey(terHost)) {
							js.put(terHost, repChars[index++]);
						}
						
						char c = js.get(terHost);
						
						ChatColor col = ChatColor.GRAY;
						
						if(!joinedName.equals(PlayerHandler.m_defaultCiv)) {
							try {
								col = Empires.m_joinableHandler.getRelationTo(joinedName, terHost).getColor();
							} catch (EmpiresJoinableDoesNotExistException e) {
								e.printStackTrace();
								
								col = ChatColor.YELLOW;
							}
						}
						
						ln = ln + col + "" + c;
					}
				}
				
				invoker.sendMessage(ln);
			}
			
			String jln = "";
			for(String jit : js.keySet()) {
				ChatColor col = ChatColor.GRAY;
				
				if(!joinedName.equals(PlayerHandler.m_defaultCiv)) {
					try {
						col = Empires.m_joinableHandler.getRelationTo(joinedName, jit).getColor();
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						
						col = ChatColor.YELLOW;
					}
				}
				
				jln = jln + col + js.get(jit) + ": " + jit + " ";
			}
			
			jln = jln.trim();
			
			invoker.sendMessage(jln);
			
			return true;
		}
		
		setError("Only players can invoke the 'map' command");
		return false;
	}

}
