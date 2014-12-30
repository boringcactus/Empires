package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandBlackList extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			
			if(!invoker.hasPermission("Empires.blacklist")) {
				setError("You do not have permission to change the blacklist!");
				return false;
			}
			
			String world = invoker.getWorld().getName();
			
			if(!EmpiresConfig.m_blacklist.contains(world)) {
				EmpiresConfig.m_blacklist.add(world);
				invoker.sendMessage(ChatColor.GREEN + "World " + world + " added to the blacklist!");
			} else {
				EmpiresConfig.m_blacklist.remove(world);
				invoker.sendMessage(ChatColor.GREEN + "World " + world + " removed from the blacklist!");
			}
			
			EmpiresConfig.saveBlacklist();
			EmpiresConfig.saveConfig();
			
			return true;
		}
		
		setError("Only players can invoke the 'blacklist' command");
		return false;
	}

}
