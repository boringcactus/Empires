package com.pixelgriffin.empires.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.task.TeleportTask;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandHome extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invoker.getName());
			
			try {
				if(_args.length == 0) {//we are teleporting to our home
					Location homeLoc = Empires.m_joinableHandler.getJoinableHome(joinedName);
					
					if(homeLoc != null) {
						teleport(invoker, homeLoc);
						return true;
					}
					
					setError("You're homeless!");
					return false;
				} else if(_args.length == 1) {//we are attempting to teleport elsewhere
					//does the joinable exist?
					if(Empires.m_joinableHandler.joinableExists(_args[0])) {
						
						if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
							//we are an empire
							setError("You cannot teleport there!");
							return false;
						} else {
							//we are NOT an empire
							//is this joinable our empire?
							if(Empires.m_joinableHandler.getJoinableEmpire(joinedName).equalsIgnoreCase(_args[0])) {
								//allow teleportation
								//gather location
								Location homeLoc = Empires.m_joinableHandler.getJoinableHome(_args[0]);
								
								if(homeLoc != null) {
									teleport(invoker, homeLoc);
									return true;
								}
								
								setError(_args[0] + " does not have a home set!");
								return false;
							}
						}
					}
					
					setError("Could not find a civilization named '" + _args[0] + "'");
					return false;
				}
				
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
		}
		
		setError("Only players can invoke the 'home' command");
		return false;
	}
	
	private void teleport(Player _who, Location _where) {
		_who.sendMessage(ChatColor.GOLD + "Teleport will commence in " + ChatColor.RED + "10 seconds " + ChatColor.GOLD + "don't move!");
		
		Empires.m_playerHandler.setPlayerTPID(_who.getName(), 
				Bukkit.getScheduler().runTaskLater(Empires.m_instance, new TeleportTask(_who, _where), 20 * 10L).getTaskId());
	}
}
