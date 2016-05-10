package com.pixelgriffin.empires.command.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.enums.Role;
//import com.pixelgriffin.empires.event.EmpiresPlayerCreateJoinable;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresJoinableExistsException;
import com.pixelgriffin.empires.exception.EmpiresJoinableInvalidCharacterException;
import com.pixelgriffin.empires.handler.Joinable;
import com.pixelgriffin.empires.handler.Kingdom;
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.util.IOUtility;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandCreate extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		if(_sender instanceof Player) {//only a player could possibly create a civilization
			Player player = (Player)_sender;//gather player
			UUID invokerID = player.getUniqueId();
			
			String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(invokerID);//gather joined
			
			//if the player is NOT in a real civilization (is in wilderness) then he is creating a kingdom
			if(joinedName.equals(PlayerHandler.m_defaultCiv)) {//KINGDOM
				if(_args.length == 1)  {
					if(EmpiresConfig.m_kingdomCreation) {
						
						//money check
						if(Empires.m_vaultActive) {
							if(Empires.m_economy.getBalance(player.getName()) < EmpiresConfig.m_kingdomCost) {
								setError("Kingdoms cost " + Empires.m_economy.format(EmpiresConfig.m_kingdomCost) + " to create!");
								return false;
							}
						}
						
						if(!player.hasPermission("Empires.colors.joinable")) {
							if(_args[0].contains("&")) {
								setError("You do not have permission to put colors in civilization names!");
								return false;
							}
						}
						
						//fire event
						//EmpiresPlayerCreateJoinable event = new EmpiresPlayerCreateJoinable(player, _args[0], false);
						//Bukkit.getPluginManager().callEvent(event);
						//if(event.isCancelled())//stop creating a kingdom since someone told us to stop
						//	return false;
						
						//create kingdom
						Joinable newJoinable = Empires.m_joinableHandler.createNewKingdom(_args[0]);
						if(newJoinable == null) {
							setError("Make sure your name is not taken and does not contain ':', '.' or '-'");
							return false;
						}
						
						//once the joinable was successfully created
						try {
							//attempt to set the player to this new civilization
							Empires.m_playerHandler.invokeRemovePlayerFromJoinedJoinable(invokerID);//shouldn't happen but as a precaution remove them from any pre-existing joinable
							Empires.m_playerHandler.setPlayerJoinedCivlization(invokerID, newJoinable);
							Empires.m_playerHandler.setPlayerRole(invokerID, Role.LEADER);
							
							player.sendMessage(ChatColor.GRAY + "Welcome to leadership! Helpful tips:");
							player.sendMessage(ChatColor.GRAY + "/e perm to view permission settings");
							player.sendMessage(ChatColor.GRAY + "/e flag to view flag settings");
							
						} catch (EmpiresJoinableExistsException e) {//shouldn't happen, but that's coding!
							e.printStackTrace();//print the error
							
							//fail gracefully - delete created joinable, reset role?
							
							setError("Something went wrong!");
							return false;
						} catch (EmpiresJoinableDoesNotExistException e) {//shouldn't happen, but that's coding!
							e.printStackTrace();//print the error
							
							//fail gracefully - delete created joinable, reset role
							
							setError("Something went wrong!");
							return false;
						}
						
						//remove money from balance
						if(Empires.m_vaultActive)
							Empires.m_economy.withdrawPlayer(player.getName(), EmpiresConfig.m_kingdomCost);
						
						//created successfully
						//inform the server of the new kingdom
						Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + _args[0] + " has risen by the hands of " + player.getName());
						
						return true;
					}
					
					
					setError("Kingdom creation has been disabled!");
					return false;
				}
				
				setError("Invalid arguments!");
				return false;
			} else {//EMPIRE
				if(_args.length == 0) {
					if(EmpiresConfig.m_empireCreation) {
						//gather invoker role
						Role invokerRole = Empires.m_playerHandler.getPlayerRole(invokerID);
						IOUtility.log(invokerRole.toString());
						
						//invoker must be a leader
						if(invokerRole.equals(Role.LEADER)) {
							
							//money check
							if(Empires.m_vaultActive) {
								if(Empires.m_economy.getBalance(player.getName()) < EmpiresConfig.m_empireCost) {
									setError("Empires cost " + Empires.m_economy.format(EmpiresConfig.m_empireCost) + " to create!");
									return false;
								}
							}
							
							//fire event
							//EmpiresPlayerCreateJoinable event = new EmpiresPlayerCreateJoinable(player, joinedName, true);
							//Bukkit.getPluginManager().callEvent(event);
							//if(event.isCancelled())//stop creating an empire since someone told us to stop
								//return false;
							
							//empire creation
							//if we're not an empire already
							Joinable joined = Empires.m_joinableHandler.getJoinable(joinedName);
							
							//if(!Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
							if(!joined.isEmpire()) {
								//Empires.m_joinableHandler.setKingdomAsEmpire(joinedName);
								((Kingdom)joined).setAsEmpire();
								
								//remove money from balance
								if(Empires.m_vaultActive)
									Empires.m_economy.withdrawPlayer(player.getName(), EmpiresConfig.m_empireCost);
								
								//inform the server of your success!
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + joinedName + " has become an empire!");
								return true;
							}
							
							setError("You already own an Empire, you have no greater aspirations!");
							return false;
						}
						
						setError("You must be a leader to create an Empire!");
						return false;
					}
					
					
					setError("Empire creation has been disabled");
					return false;
				}
				
				setError("Invalid arguments!");
				return false;
			}
		}
		
		setError("The command 'create' can only be run by players");
		return false;
	}

}
