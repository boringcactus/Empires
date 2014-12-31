package com.pixelgriffin.empires.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandNeutral extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				String invokerName = invoker.getName();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerName);
				
				//is default civ? (wilderness)
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("The wilderness cannot maintain relationships!");
					return false;
				}
				
				String otherJoinable = getReferencedJoinable(_args[0]);
				
				//does the other joinable exist?
				if(otherJoinable.equals(PlayerHandler.m_defaultCiv)) {
					setError("Could not find a reference to '" + _args[0] + "'");
					return false;
				}
				
				//stop us from setting relations to ourselves
				if(otherJoinable.equals(joinedName)) {
					setError("You cannot maintain a relationship with yourself! (that's just sad)");
					return false;
				}
					
				//gather player role
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerName);
				
				try {
					//does the player have permission?
					if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.RELATION, invokerRole)) {
						Empires.m_joinableHandler.setJoinableRelationWish(joinedName, otherJoinable, Relation.NEUTRAL);
						
						//get our new relation
						Relation currentRelation = Empires.m_joinableHandler.getJoinableRelationTo(joinedName, otherJoinable);
						
						//gather display names/messages for printing
						String displayNameA = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
						String displayMessageA = Relation.NEUTRAL.getColor() + displayNameA + " wishes to be neutral";
						String displayNameB = Empires.m_joinableHandler.getJoinableDisplayName(otherJoinable);
						String displayMessageB = Relation.ALLY.getColor() + invokerName + " has asked to cease relations with " + displayNameB;
						
						//if we are now allies
						if(currentRelation.equals(Relation.NEUTRAL)) {
							//change the messages to display such
							displayMessageA = Relation.NEUTRAL.getColor() + displayNameA + " is now neutral";
							displayMessageB = Relation.NEUTRAL.getColor() + displayNameB + " is now neutral";
						}
						
						//inform the other civilization of our intentions
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(otherJoinable, displayMessageA);
						
						//inform us of our actions!
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, displayMessageB);
						
						return true;//yay
					}
					
					setError("You do not have permission to change civilization relations!");
					return false;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
					
					//joinedName does not exist
					//invoker points to non existent joinable
					
					setError("Something went wrong!");
					return false;
				}
			}
			
			setError("Invalid arguments!");
			return false;
		}
		
		setError("The command 'ally' can only be executed by a player");
		return false;
	}

	
	/**
	 * Gets the refered joinable from a string
	 * @param _reference the refernce string
	 * @return will return default civ if it cannot find a reference
	 */
	private String getReferencedJoinable(String _reference) {
		//gather the joinedName
		//the user could be talking about a player OR a joinable
		//this determines what they're refering to
		String joinedName = PlayerHandler.m_defaultCiv;
		
		//does the joinable exist?
		if(Empires.m_joinableHandler.getJoinableExists(_reference)) {
			joinedName = _reference;//then we're talking about _reference
		} else if(Empires.m_playerHandler.getPlayerExists(_reference)) {//does a player with this name exist?
			joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(_reference);//then we were walking about a player's joinable
		}
		
		return joinedName;
	}
}
