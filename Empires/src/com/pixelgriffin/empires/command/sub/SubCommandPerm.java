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
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandPerm extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			Player invoker = (Player)_sender;
			UUID invokerID = invoker.getUniqueId();
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
			
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
				setError("You cannot edit permissions for " + PlayerHandler.m_defaultCiv + "!");
				return false;
			}
			
			try {
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				if(!Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.PERMS, invokerRole)) {
					setError("You do not have permission to edit permissions!");
					return false;
				}
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				setError("Something went wrong!");
				return false;
			}
			
			if(_args.length == 0) {
				try {
					String outString = "";
					for(GroupPermission p : GroupPermission.values()) {
						outString = ChatColor.GRAY + p.toString() + ": ";
						
						for(Role r : Role.values()) {
							if(r.equals(Role.LEADER))
								continue;
							
							ChatColor accessCol = ChatColor.RED;
							
							if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, p, r))
								accessCol = ChatColor.GREEN;
							
							if(r.equals(Role.OFFICER_1)) {
								outString = outString + ChatColor.GRAY + " OFFICER("+ accessCol +"1";
							} else if(r.equals(Role.OFFICER_2)) {
								outString = outString + ChatColor.GRAY + "," + accessCol + "2";
							} else if(r.equals(Role.OFFICER_3)) {
								outString = outString + ChatColor.GRAY + "," + accessCol + "3" + ChatColor.GRAY + ")";
							} else {
								outString = outString + " " + accessCol + r.toString();
							}
						}
						
						invoker.sendMessage(outString);
					}
					
					return true;
				} catch(EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
			} else if(_args.length == 2) {
				try {
					GroupPermission p = GroupPermission.valueOf(_args[0].toUpperCase());
					Role r = Role.valueOf(_args[1].toUpperCase());
					
					if(r.equals(Role.LEADER)) {
						setError("Leaders have all permissions!");
						return false;
					}
					
					boolean val = Empires.m_joinableHandler.toggleJoinablePermission(joinedName, p, r);
					ChatColor col = ChatColor.RED;
					
					if(val)
						col = ChatColor.GREEN;
					
					invoker.sendMessage(ChatColor.GRAY + p.toString() + ": " + col + r.toString());
					
					return true;
				} catch(IllegalArgumentException e) {
					setError("Could not find permission '" + _args[0] + "' for '" + _args[1] + "'");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					setError("Something went wrong!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("Only players can invoke the 'perm' command");
		return false;
	}

}
