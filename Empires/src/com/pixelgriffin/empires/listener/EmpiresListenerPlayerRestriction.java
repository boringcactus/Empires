package com.pixelgriffin.empires.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.EmpiresConfig;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.util.IOUtility;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresListenerPlayerRestriction implements Listener {
	/*
	 * Build limitations
	 */
	@EventHandler
	//when a player builds
	public void onPlayerBuild(BlockPlaceEvent _evt) {
		if(_evt.isCancelled())
			return;
		
		Location loc = _evt.getBlock().getLocation();
		String host = Empires.m_boardHandler.getTerritoryHost(loc);
		
		//do not block building in default civ
		if(host.equals(PlayerHandler.m_defaultCiv))
			return;
		
		if(_evt.getPlayer() == null) {
			IOUtility.log("Detected null player during BlockPlaceEvent.. cancelling", ChatColor.RED);
			return;
		}
		
		//if we can't build here
		//check for territory flags
		TerritoryGroup tg = getInvokerGroup(_evt.getPlayer().getName(), host, loc);
		if(!Empires.m_boardHandler.territoryHasFlag(loc, tg, TerritoryFlag.ALLOW_BUILD)) {
			//cancel building
			_evt.setCancelled(true);
			//inform
			sendError(_evt.getPlayer(), "You are not allowed to build here");
		}
	}
	
	@EventHandler
	//when a player tries to place a liquid somewhere
	public void onPlayerPlaceLiquid(PlayerBucketEmptyEvent _evt) {
		Location placeLoc = _evt.getBlockClicked().getLocation();
		Player invoker = _evt.getPlayer();
		String host = Empires.m_boardHandler.getTerritoryHost(placeLoc);
		
		if(host.equals(PlayerHandler.m_defaultCiv))
			return;
		
		if(_evt.getPlayer() == null) {
			IOUtility.log("Detected null player during PlayerBucketEmptyEvent.. cancelling", ChatColor.RED);
			return;
		}
		
		//if we can't build here
		//check for territory flags
		TerritoryGroup tg = getInvokerGroup(invoker.getName(), host, placeLoc);
		if(!Empires.m_boardHandler.territoryHasFlag(placeLoc, tg, TerritoryFlag.ALLOW_BUILD)) {
			//cancel building
			_evt.setCancelled(true);
			//inform
			sendError(_evt.getPlayer(), "You are not allowed to build here");
		}
	}
	
	@EventHandler
	//when a player breaks a block
	public void onPlayerBreak(BlockBreakEvent _evt) {
		Location loc = _evt.getBlock().getLocation();
		String host = Empires.m_boardHandler.getTerritoryHost(loc);
		
		//do not block building in default civ
		if(host.equals(PlayerHandler.m_defaultCiv))
			return;
		
		//if we can't build here
		TerritoryGroup tg = getInvokerGroup(_evt.getPlayer().getName(), host, loc);
		if(!Empires.m_boardHandler.territoryHasFlag(loc, tg, TerritoryFlag.ALLOW_BUILD)) {
			_evt.setCancelled(true);
			sendError(_evt.getPlayer(), "You are not allowed to build here");
		}
	}
	
	/*
	 * Ender pearl limit
	 */
	
	@EventHandler
	//when a player teleports because of an ender pearl
	public void onPearlLands(PlayerTeleportEvent _evt) {
		//only check for ender pearls
		if(!_evt.getCause().equals(TeleportCause.ENDER_PEARL))
			return;//not an ender pearl
		
		String hostFrom, hostTo;
		hostFrom = Empires.m_boardHandler.getTerritoryHost(_evt.getFrom());
		hostTo = Empires.m_boardHandler.getTerritoryHost(_evt.getTo());
		
		TerritoryGroup invokerGroup;
		
		//if the territory we're throwing FROM is not default
		if(!hostFrom.equals(PlayerHandler.m_defaultCiv)) {
			//gather our group in regards to the FROM host
			invokerGroup = getInvokerGroup(_evt.getPlayer().getName(), hostFrom, _evt.getFrom());
			
			//if the territory doesn't allow pearls
			if(!Empires.m_boardHandler.territoryHasFlag(_evt.getFrom(), invokerGroup, TerritoryFlag.ALLOW_PEARLS)) {
				//cancel teleport
				_evt.setCancelled(true);
				//inform
				sendError(_evt.getPlayer(), hostFrom + " does not allow pearl use");
				//do not continue checking if we should cancel
				return;
			}
		}
		
		if(!hostTo.equals(PlayerHandler.m_defaultCiv)) {
			//gather invoker group in regards to the TO host
			invokerGroup = getInvokerGroup(_evt.getPlayer().getName(), hostTo, _evt.getTo());
			
			//if the territory doesn't allow pearls
			if(!Empires.m_boardHandler.territoryHasFlag(_evt.getTo(), invokerGroup, TerritoryFlag.ALLOW_PEARLS)) {
				//cancel teleport
				_evt.setCancelled(true);
				//inform
				sendError(_evt.getPlayer(), hostTo + " does not allow pearl use");
				//do not continue checking if we should cancel
				return;
			}
		}
	}
	
	/*
	 * Tripwire limit
	 */
	@EventHandler
	public void onPlayerTripwire(PlayerInteractEvent _evt) {
		if(_evt.getAction().equals(Action.PHYSICAL)) {
			if(_evt.getClickedBlock().getType().equals(Material.TRIPWIRE)) {
				Player invoker = _evt.getPlayer();
				Location invokerLoc = invoker.getLocation();
				String host = Empires.m_boardHandler.getTerritoryHost(invokerLoc);
				
				if(!host.equals(PlayerHandler.m_defaultCiv)) {
					TerritoryGroup invokerGroup = getInvokerGroup(invoker.getName(), host, invokerLoc);
					
					if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_TRIPWIRE)) {
						sendError(invoker, host + " does not allow you to use tripwires!");
						_evt.setCancelled(true);
					}
				}
			}
		}
	}
	
	/*
	 * Plate, button, lever limitation
	 */
	@EventHandler
	public void onPlayerTripPlateButton(PlayerInteractEvent _evt) {
		if(_evt.getAction().equals(Action.PHYSICAL) || _evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = _evt.getClickedBlock();
			Player invoker = _evt.getPlayer();
			Location invokerLoc = invoker.getLocation();
			
			String host = Empires.m_boardHandler.getTerritoryHost(invokerLoc);
			
			if(!host.equals(PlayerHandler.m_defaultCiv)) {
				TerritoryGroup invokerGroup = getInvokerGroup(invoker.getName(), host, invokerLoc);
				
				//handle buckets of lava/water
				if(_evt.getItem() != null) {
					Material itemType = _evt.getItem().getType();
					if(itemType.equals(Material.LAVA_BUCKET) || itemType.equals(Material.WATER_BUCKET)) {
						if(Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_BUILD)) {
							sendError(invoker, host + " does not allow you to build here!");
							_evt.setCancelled(true);
							return;//do not continue from here
						}
					}
				}
				
				//we have a block
				if(b != null) {
					//gather material
					Material blockType = b.getType();
					
					if(blockType.equals(Material.WOOD_PLATE)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_WOODPLT)) {
							sendError(invoker, host + " does not allow you to use wood plates!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.STONE_PLATE)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_STONEPLT)) {
							sendError(invoker, host + " does not allow you to use stone plates!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.WOOD_BUTTON)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_WOODBTN)) {
							sendError(invoker, host + " does not allow you to use wood buttons!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.STONE_BUTTON)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_STONEBTN)) {
							sendError(invoker, host + " does not allow you to use stone buttons!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.LEVER)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_LEVER)) {
							sendError(invoker, host + " does not allow you to use levers!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.WOODEN_DOOR) || blockType.equals(Material.IRON_DOOR)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_DOOR)) {//error occurring? causing NPE later on
							sendError(invoker, host + " does not allow you to use doors!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.FENCE_GATE)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_FENCEGATE)) {
							sendError(invoker, host + " does not allow you to use fence gates!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.DISPENSER) || blockType.equals(Material.DROPPER)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_DISPENSER)) {
							sendError(invoker, host + " does not allow you to use dispensers!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.NOTE_BLOCK)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_NOTEBLOCK)) {
							sendError(invoker, host + "does not allow you to use note blocks!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.JUKEBOX)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_JUKEBOX)) {
							sendError(invoker, host + " does not allow you to use juke boxes!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.ANVIL)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_ANVIL)) {
							sendError(invoker, host + " does not allow you to use anvils");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.ENCHANTMENT_TABLE)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_ENCHANT)) {
							sendError(invoker, host + " does not allow you to use enchantment tables!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.CHEST)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_CHEST)) {
							sendError(invoker, host + " does not allow you to use chests!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.WORKBENCH)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_CRAFTING)) {
							sendError(invoker, host + " does not allow you to use workbenches!");
							_evt.setCancelled(true);
						}
					} else if(blockType.equals(Material.BREWING_STAND)) {
						if(!Empires.m_boardHandler.territoryHasFlag(invokerLoc, invokerGroup, TerritoryFlag.ALLOW_BREWSTAND)) {
							sendError(invoker, host + " does not allow you to use brewing stands!");
							_evt.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	/*
	 * Ally damage limit
	 */
	@EventHandler
	//when a player is damaged by an ally of some sort
	public void onDamageAlly(EntityDamageByEntityEvent _evt) {
		//make sure both the recipient and invoker of the
		//damage are players or shot by a player in the case of the arrow
		if(!(_evt.getEntity() instanceof Player))
			return;
		if(!((_evt.getDamager() instanceof Player) || (_evt.getDamager() instanceof Arrow)))
			return;
		
		Player attacker;
		Player defender;
		
		//arrow fix, do not harm allies by our arrows!
		//was the damager an arrow?
		if(_evt.getDamager() instanceof Arrow) {
			//gather arrow
			Arrow a = (Arrow)_evt.getDamager();
			//was the shooter a player?
			if(a.getShooter() instanceof Player) {
				//set the attacker
				attacker = (Player)a.getShooter();
			} else {
				//arrow was fired by a mob, no need
				//to do any other work
				return;
			}
		} else {//the damager must have been a player
			attacker = (Player)_evt.getDamager();
		}
		
		//gather defender
		defender = (Player)_evt.getEntity();
		
		//gather defender location
		Location defLoc = defender.getLocation();
		
		//gather host from defenders location
		/*String host = Empires.m_boardHandler.getTerritoryHost(defLoc);
		
		//no need to do work on the default civ
		if(host.equals(PlayerHandler.m_defaultCiv))
			return;*/
		
		//does this territory ignore relations?
		if(!Empires.m_boardHandler.territoryIgnoresRelations(defLoc)) {
			//if not
			try {
				//gather relationship information
				String defHost, atkHost;
				defHost = Empires.m_playerHandler.getPlayerJoinedCivilization(defender.getName());
				atkHost = Empires.m_playerHandler.getPlayerJoinedCivilization(attacker.getName());
				
				Relation rel = Empires.m_joinableHandler.getJoinableRelationTo(defHost, atkHost);
				
				//message string
				String message = "";
				
				//test relation
				if(rel.equals(Relation.ALLY)) {
					message = Relation.ALLY.getColor() + "You can't hurt allies!";
				} else if(rel.equals(Relation.US)) {
					message = Relation.US.getColor() + "You can't hurt your fellow members!";
				} else if(rel.equals(Relation.E_K)) {
					message = Relation.E_K + "You can't hurt members of your empire!";
				} else {
					//we must be neutral or enemies
					//do not send a message and do not cancel damage
					return;
				}
				
				//cancel damage
				_evt.setCancelled(true);
				
				//inform attacker
				attacker.sendMessage(message);
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
				
				sendError(defender, "Something went wrong!");
				sendError(attacker, "Something went wrong!");
			}
		}
	}
	
	/*
	 * Damage reduction on territory
	 */
	@EventHandler
	public void onDamagedOnTerritory(EntityDamageByEntityEvent _evt) {
		//are we allowed to reduce damage in the config?
		if(!EmpiresConfig.m_damageReduc)
			return;//nope
		
		if(_evt.getEntity() instanceof Player) {
			Player damaged = (Player)_evt.getEntity();
			
			String host = Empires.m_boardHandler.getTerritoryHost(damaged.getLocation());
			
			if(!host.equals(PlayerHandler.m_defaultCiv)) {
				String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(damaged.getName());
				
				//this is our territory
				if(joinedName.equalsIgnoreCase(host)) {
					damaged.sendMessage(ChatColor.GREEN + "30% less damage taken on your land!");
					_evt.setDamage(_evt.getDamage() * 0.7D);
				} else {
					try {
						if(Empires.m_joinableHandler.getJoinableEmpireStatus(joinedName)) {
							//we are an empire
							//check if host is in our empire
							if(Empires.m_joinableHandler.getEmpireKingdomList(joinedName).contains(host.toLowerCase())) {
								damaged.sendMessage(ChatColor.GOLD + "30% less damage taken on empire land!");
								_evt.setDamage(_evt.getDamage() * 0.7D);
							}
						} else {
							//we are kingdom
							//gather empire name
							String empireName = Empires.m_joinableHandler.getKingdomEmpire(joinedName);
							
							//no empire, no need to check
							if(empireName.equals(""))
								return;
							
							if(empireName.equalsIgnoreCase(host)) {
								//the host is our empire
								damaged.sendMessage(ChatColor.GOLD + "30% less damage taken on empire land!");
								_evt.setDamage(_evt.getDamage() * 0.7D);
							} else {
								//is the host part of our empire?
								if(Empires.m_joinableHandler.getEmpireKingdomList(empireName).contains(host.toLowerCase())) {
									damaged.sendMessage(ChatColor.GOLD + "30% less damage taken on empire land!");
									_evt.setDamage(_evt.getDamage() * 0.7D);
								}
							}
						}
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
						
						sendError(damaged, "Something went wrong!");
						
						return;
					}
				}
			}
		}
	}
	
	/*
	 * Stop mob spawns
	 */
	@EventHandler
	public void onMobSpawnsInTerritory(CreatureSpawnEvent _evt) {
		if(EmpiresConfig.m_mobSpawnManaging) {
			if(_evt.getEntity() instanceof Monster) {
				if(Empires.m_boardHandler.getTerritoryHost(_evt.getLocation()) != PlayerHandler.m_defaultCiv) {
					if(!Empires.m_boardHandler.territoryAllowsMobs(_evt.getLocation())) {
						_evt.setCancelled(true);
					}
				}
			}
		}
	}
	
	/**
	 * Convenience method
	 * @param _p
	 * @param _msg
	 */
	private void sendError(Player _p, String _msg) {
		_p.sendMessage(ChatColor.RED + _msg);
	}
	
	/**
	 * Gathers the invoker's territory group
	 * @param _name
	 * @param _host
	 * @return
	 */
	private TerritoryGroup getInvokerGroup(String _name, String _host, Location _loc) {
		//if the default civ then return a neutral value
		if(_host.equals(PlayerHandler.m_defaultCiv))
			return TerritoryGroup.NEUTRAL;
			
		//gather player info
		String joinedName = Empires.m_playerHandler.getPlayerJoinedCivilization(_name);
		TerritoryGroup group = TerritoryGroup.NEUTRAL;
		
		//if someone has access to this chunk they are treated as a member
		if(Empires.m_boardHandler.territoryHasAccessFor(_loc, _name)) {
			return TerritoryGroup.MEMBER;
		}
		
		Relation rel = null;
		try {
			rel = Empires.m_joinableHandler.getJoinableRelationTo(joinedName, _host);
		} catch (EmpiresJoinableDoesNotExistException e) {
			e.printStackTrace();
		}
		
		//we have a relation to the host
		if(rel != null) {
			if(rel.equals(Relation.ALLY) || rel.equals(Relation.E_K)) {
				group = TerritoryGroup.ALLY;
			} else if(rel.equals(Relation.ENEMY)) {
				group = TerritoryGroup.ENEMY;
			} else if(rel.equals(Relation.NEUTRAL)) {
				group = TerritoryGroup.NEUTRAL;
			} else if(rel.equals(Relation.US)) {
				Role invokerRole = Empires.m_playerHandler.getPlayerRole(_name);
				group = TerritoryGroup.fromRole(invokerRole);
			}
		} else {
			return TerritoryGroup.NEUTRAL;
		}
		
		return group;
	}
}
