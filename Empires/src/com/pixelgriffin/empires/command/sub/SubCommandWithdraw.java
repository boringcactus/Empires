package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresNoFundsException;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandWithdraw extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("You cannot withdraw money from " + PlayerHandler.m_defaultCiv);
					return false;
				}
				
				Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				//if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.WITHDRAW, invokerRole)) {
				if(!joined.getPermissionForRole(invokerRole, GroupPermission.WITHDRAW)) {
					setError("You do not have permission to withdraw money!");
					return false;
				}
				
				double amount = 0D;
				
				try {
					amount = Double.valueOf(_args[0]); 
				} catch(NumberFormatException e) {
					setError(_args[0] + " is not a valid amount to withdraw!");
					return false;
				}
				
				//Empires.m_joinableHandler.invokeJoinableWithdrawMoney(joinedName, invoker.getName(), amount);
				joined.withdrawMoney(invoker.getName(), amount);
				
				//inform of a successful deposit!
				//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, ChatColor.YELLOW + invoker.getDisplayName() + " withdrew " + Empires.m_economy.format(amount) + " from the civilization bank!");
				joined.broadcastMessageToJoined(ChatColor.YELLOW + invoker.getDisplayName() + " withdrew " + Empires.m_economy.format(amount) + " from the civilization bank!");
				
				return true;//success
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'deposit' command");
		return false;
	}

}
