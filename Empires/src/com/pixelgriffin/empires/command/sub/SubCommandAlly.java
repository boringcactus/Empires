package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.PlayerHandler;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandAlly extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {
			if(_args.length == 1) {
				Player invoker = (Player)_sender;
				UUID invokerID = invoker.getUniqueId();
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);
				
				//is default civ? (wilderness)
				if(joinedName.equals(PlayerHandler.m_defaultCiv)) {
					setError("The wilderness cannot maintain relationships!");
					return false;
				}
				
				//does the other joinable exist?
				String otherJoinable = getReferencedJoinable(_args[0]);
				
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
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
				
				Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
				
				//does the player have permission?
				//if(Empires.m_joinableHandler.getJoinableHasPermissionForRole(joinedName, GroupPermission.RELATION, invokerRole)) {
				if(joined.getPermissionForRole(invokerRole, GroupPermission.RELATION)) {
					//Empires.m_joinableHandler.setJoinableRelationWish(joinedName, otherJoinable, Relation.ALLY);
					Joinable other = Empires.m_joinableHandler.getJoinable(otherJoinable);
					joined.setRelationWish(other, Relation.ALLY);
					
					//get our new relation
					//Relation currentRelation = Empires.m_joinableHandler.getJoinableRelationTo(joinedName, otherJoinable);
					Relation currentRelation = joined.getRelation(other);
					
					//gather display names/messages for printing
					//String displayNameA = Empires.m_joinableHandler.getJoinableDisplayName(joinedName);
					String displayNameA = joined.getDisplayName();
					String displayMessageA = Relation.ALLY.getColor() + displayNameA + " wishes to be allies";
					//String displayNameB = Empires.m_joinableHandler.getJoinableDisplayName(otherJoinable);
					String displayNameB = other.getDisplayName();
					String displayMessageB = Relation.ALLY.getColor() + invoker.getDisplayName() + " has asked " + displayNameB + " to be allies";
					
					//if we are now allies
					if(currentRelation.equals(Relation.ALLY)) {
						//change the messages to display such
						displayMessageA = Relation.ALLY.getColor() + displayNameA + " is now an ally";
						displayMessageB = Relation.ALLY.getColor() + displayNameB + " is now an ally";
					}
					
					//inform the other civilization of our intentions
					//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(otherJoinable, displayMessageA);
					other.broadcastMessageToJoined(displayMessageA);
					
					//inform us of our actions!
					//Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(joinedName, displayMessageB);
					joined.broadcastMessageToJoined(displayMessageB);
					
					return true;//yay
				}
				
				setError("You do not have permission to change civilization relations!");
				return false;
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
		Joinable referenced = Empires.m_joinableHandler.getJoinable(_reference);
		
		//does the joinable exist?
		//if(Empires.m_joinableHandler.getJoinableExists(_reference)) {
		if(referenced != null) {
			joinedName = _reference;//then we're talking about _reference
		} else {
			Player p = Bukkit.getPlayer(_reference);
			
			if(p != null) {
				UUID id = p.getUniqueId();
				
				if(Empires.m_playerHandler.getPlayerExists(id)) {//does a player with this name exist?
					joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(id);//then we were walking about a player's joinable
				}
			}
		}
		
		return joinedName;
	}

}
