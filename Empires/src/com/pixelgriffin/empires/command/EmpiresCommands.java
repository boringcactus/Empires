package com.pixelgriffin.empires.command;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.sub.*;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresCommands implements CommandExecutor {
	
	private HashMap<String, SubCommand> m_commandMap;
	
	public EmpiresCommands() {
		//create the map of sub commands and their ids
		m_commandMap = new HashMap<String, SubCommand>();
		
		m_commandMap.put("create", new SubCommandCreate());
		m_commandMap.put("who", new SubCommandWho());
		m_commandMap.put("title", new SubCommandTitle());
		m_commandMap.put("disband", new SubCommandDisband());
		m_commandMap.put("claim", new SubCommandClaim());
		m_commandMap.put("unclaimall", new SubCommandUnclaimAll());
		m_commandMap.put("ally", new SubCommandAlly());
		m_commandMap.put("enemy", new SubCommandEnemy());
		m_commandMap.put("neutral", new SubCommandNeutral());
		m_commandMap.put("list", new SubCommandList());
		m_commandMap.put("leave", new SubCommandLeave());
		m_commandMap.put("invite", new SubCommandInvite());
		m_commandMap.put("join", new SubCommandJoin());
		m_commandMap.put("secede", new SubCommandSecede());
		m_commandMap.put("power", new SubCommandPower());
		m_commandMap.put("unclaim", new SubCommandUnclaim());
		m_commandMap.put("inherit", new SubCommandInherit());
		m_commandMap.put("kick", new SubCommandKick());
		m_commandMap.put("desc", new SubCommandDesc());
		m_commandMap.put("promote", new SubCommandPromote());
		m_commandMap.put("demote", new SubCommandDemote());
		m_commandMap.put("map", new SubCommandMap());
		m_commandMap.put("autoclaim", new SubCommandAutoClaim());
		m_commandMap.put("name", new SubCommandName());
		m_commandMap.put("sethome", new SubCommandSetHome());
		m_commandMap.put("home", new SubCommandHome());
		m_commandMap.put("leader", new SubCommandLeader());
		m_commandMap.put("access", new SubCommandAccess());//TODO: TEST
		m_commandMap.put("help", new SubCommandHelp());//TODO: TEST
		m_commandMap.put("chunk", new SubCommandChunk());
		m_commandMap.put("flag", new SubCommandFlag());
		m_commandMap.put("perm", new SubCommandPerm());
		m_commandMap.put("remove", new SubCommandRemove());//TODO: TEST
		
		if(Empires.m_vaultActive) {
			//vault specific
			m_commandMap.put("withdraw", new SubCommandWithdraw());
			m_commandMap.put("deposit", new SubCommandDeposit());
		}
		
		m_commandMap.put("blacklist", new SubCommandBlackList());//TODO: TEST
	}
	
	@Override
	public boolean onCommand(CommandSender _sender, Command _cmd, String _label, String[] _args) {
		if(_cmd.getName().equalsIgnoreCase("Empires")) {
			if(_args.length == 0) {
				//just /e
				Bukkit.getServer().dispatchCommand(_sender, "e help 9");
			} else {
				//gather command from map
				SubCommand command = this.m_commandMap.get(_args[0]);
				
				//attempt to run command
				if(command != null) {
					//remove the first argument which will always be the sub command label
					String[] changedArgs = Arrays.copyOfRange(_args, 1, _args.length);
					
					//run the command and send any errors
					if(!command.run(_sender, changedArgs)) {
						if(command.getError() != null)
							_sender.sendMessage(ChatColor.RED + command.getError());
					}
					
					return true;
				}
				
				//no command found, run help
				Bukkit.getServer().dispatchCommand(_sender, "e help");
			}
		}
		
		return true;// couldn't catch a command
	}
}
