package com.pixelgriffin.empires.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pixelgriffin.empires.command.SubCommand;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandHelp extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		int pageNum = 0;
		
		if(_args.length == 1) {
			try {
				pageNum = Integer.valueOf(_args[0]) - 1;
			} catch(NumberFormatException e) {
				setError("The page '" + _args[0] + "' is not valid");
				return false;
			}
		}
		
		showHelp(pageNum, _sender);
		
		return true;
	}
	
	private void showHelp (int _pg, CommandSender _cs) {
		_cs.sendMessage(ChatColor.GRAY + "________[" + ChatColor.GOLD + "Empires Help (" + (_pg + 1) + "/10)" + ChatColor.GRAY + "]________");
		
		switch(_pg) {
		case 0:
			_cs.sendMessage(ChatColor.GRAY + "/e access " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Gives player [who] member access to a chunk");
			_cs.sendMessage(ChatColor.GRAY + "/e ally " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Sets your civilization's relation wish with [who] to ally");
			_cs.sendMessage(ChatColor.GRAY + "/e autoclaim - " + ChatColor.LIGHT_PURPLE + "Toggles auto-claiming on/off");
			_cs.sendMessage(ChatColor.GRAY + "/e blacklist - " + ChatColor.LIGHT_PURPLE + "Adds your current world to the Empires blacklist");
			_cs.sendMessage(ChatColor.GRAY + "(blacklisting disables Empires claiming in that world)");
			break;
			
		case 1:
			_cs.sendMessage(ChatColor.GRAY + "/e chunk [flag] [group] - " + ChatColor.LIGHT_PURPLE + "Shows/edits a chunk's flags");
			_cs.sendMessage(ChatColor.GRAY + "(when changing IGNORE_RELATIONS simply use /e chunk IGNORE_RELATIONS)");
			_cs.sendMessage(ChatColor.GRAY + "/e claim - " + ChatColor.LIGHT_PURPLE + "Claims a chunk for your civilization at your location");
			_cs.sendMessage(ChatColor.GRAY + "/e create [name] - " + ChatColor.LIGHT_PURPLE + "Creates a new kingdom with the name [name]");
			_cs.sendMessage(ChatColor.GRAY + "(using just /e create will upgrage a kingdom to an empire");
			break;
			
		case 2:
			_cs.sendMessage(ChatColor.GRAY + "/e demote " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Demotes player [who] by a single ranking");
			_cs.sendMessage(ChatColor.GRAY + "/e deposit " + ChatColor.WHITE + "[money]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Deposits [money] amount of money into the civilization bank");
			_cs.sendMessage(ChatColor.GRAY + "/e desc " + ChatColor.WHITE + "[desc..]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Changes your civilization's description to [desc..]");
			_cs.sendMessage(ChatColor.GRAY + "/e disband - " + ChatColor.LIGHT_PURPLE + "Disbands your civilization");
			_cs.sendMessage(ChatColor.GRAY + "/e enemy " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Sets your civilization's relation wish with [who] to enemy");
			break;
			
		case 3:
			_cs.sendMessage(ChatColor.GRAY + "/e flag [flag] [group] - " + ChatColor.LIGHT_PURPLE + "Shows/edits default flag values for your civilization");
			_cs.sendMessage(ChatColor.GRAY + "(when changed all chunks will update to the new value)");
			_cs.sendMessage(ChatColor.GRAY + "(all flags values will be copied onto each new claim)");
			_cs.sendMessage(ChatColor.GRAY + "/e home [home] - " + ChatColor.LIGHT_PURPLE + "Teleports you to your civilization's home");
			_cs.sendMessage(ChatColor.GRAY + "(teleports you to your empire's home if [home] is your empire)");
			break;
			
		case 4:
			_cs.sendMessage(ChatColor.GRAY + "/e inherit " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Sets the heir of the civilization to [who]");
			_cs.sendMessage(ChatColor.GRAY + "(heirs will take leadership of the civilization if the current leader leaves)");
			_cs.sendMessage(ChatColor.GRAY + "/e invite " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Invites the player [who] to join your civilization");
			_cs.sendMessage(ChatColor.GRAY + "/e join " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "You attempt to join the civilization [who]");
			_cs.sendMessage(ChatColor.GRAY + "(if [who] is an empire and you are a kingdom you will join the empire [who]");
			break;
			
		case 5:
			_cs.sendMessage(ChatColor.GRAY + "/e kick " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Kicks the player [who] from your civilization");
			_cs.sendMessage(ChatColor.GRAY + "/e leader - " + ChatColor.LIGHT_PURPLE + "Forces you into the leader position");
			_cs.sendMessage(ChatColor.GRAY + "/e leave - " + ChatColor.LIGHT_PURPLE + "You leave your current civilization");
			_cs.sendMessage(ChatColor.GRAY + "/e list - " + ChatColor.LIGHT_PURPLE + "Lists the top civilizations on the server");
			_cs.sendMessage(ChatColor.GRAY + "/e map - " + ChatColor.LIGHT_PURPLE + "Displays a map of the surrounding area");
			break;
			
		case 6:
			_cs.sendMessage(ChatColor.GRAY + "/e name " + ChatColor.WHITE + "[name]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Changes the civilization name to [name]");
			_cs.sendMessage(ChatColor.GRAY + "/e neutral " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Sets your civilization's relation wish with [who] to neutral");
			_cs.sendMessage(ChatColor.GRAY + "/e perm [perm] [role] - " + ChatColor.LIGHT_PURPLE + "Shows/edits permission [perm] for role [role]");//FIXME
			_cs.sendMessage(ChatColor.GRAY + "/e power - " + ChatColor.LIGHT_PURPLE + "Displays your current power/max power");
			_cs.sendMessage(ChatColor.GRAY + "/e promote " + ChatColor.WHITE + "[who]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Promotes player [who] by one rank");
			break;
			
		case 7:
			_cs.sendMessage(ChatColor.GRAY + "/e remove" + ChatColor.WHITE + " [kingdom]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Removes a kingdom [kingdom] from an empire");
			_cs.sendMessage(ChatColor.GRAY + "/e secede - " + ChatColor.LIGHT_PURPLE + "Leaves your civilization's current empire");
			_cs.sendMessage(ChatColor.GRAY + "/e sethome - " + ChatColor.LIGHT_PURPLE + "Sets your civilization's home at your location");
			_cs.sendMessage(ChatColor.GRAY + "/e title " + ChatColor.WHITE + "[who] [title]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Sets player [who]'s title to [title]");
			_cs.sendMessage(ChatColor.GRAY + "/e unclaim - " + ChatColor.LIGHT_PURPLE + "Unclaims the chunk you are standing on");
			break;
			
		case 8:
			_cs.sendMessage(ChatColor.GRAY + "/e unclaimall - " + ChatColor.LIGHT_PURPLE + "Unclaims all chunks your civilization owns");
			_cs.sendMessage(ChatColor.GRAY + "/e who [who] - " + ChatColor.LIGHT_PURPLE + "Shows the civilization [who] or your civilization's info");
			_cs.sendMessage(ChatColor.GRAY + "/e withdraw " + ChatColor.WHITE + "[money]" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Withdraws the amount [money] from the civilization bank");
			_cs.sendMessage(ChatColor.GRAY + "/e help [page] - " + ChatColor.LIGHT_PURPLE + "Shows the help pages at page [page] or 9");
			break;
			
		case 9:
			_cs.sendMessage(ChatColor.GRAY + "Written by Pixelgriffin");
			_cs.sendMessage(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "http://pixelgriffin.com/");
			break;
			
		default:
			_cs.sendMessage(ChatColor.GRAY + "There is no information on this page!");
			break;
		}
	}
}
