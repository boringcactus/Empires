package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandPower extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			int powerVal = Empires.m_playerHandler.getPlayerPower(invoker.getUniqueId());
			
			//construct the initial string
			String powerStr = ChatColor.GRAY + "[" + ChatColor.GREEN;
			
			//iterate over the power value (add green bars)
			for(int i = 0; i < powerVal; i++) {
				powerStr += "|";
			}
			
			//set new bars to gray
			powerStr += ChatColor.GRAY;
			
			int maxVal = EmpiresConfig.m_powerMax;
			
			//if the player is allowed extra power
			if(invoker.hasPermission("Empires.power.extra"))
				maxVal = EmpiresConfig.m_power2Max;//set it to the other max
			
			//iterate over the remaining power (add bray bars
			for(int i = 0; i < maxVal - powerVal; i++) {
				powerStr += "|";
			}
			
			//end the string
			powerStr += "]\n" + ChatColor.GREEN + powerVal + "/" + maxVal;
			
			//send message
			invoker.sendMessage(ChatColor.GRAY + invoker.getName() + "'s power:");
			invoker.sendMessage(powerStr);
			
			return true;
		}
		
		setError("Only players can invoke the 'power' command");
		return false;
	}

}
