package com.pixelgriffin.empires.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;


public abstract class Joinable {
	protected ConfigurationSection ymlData;//TODO replace with abstracted IO class
	//protected DataAbstractor data;
	
	public Joinable(ConfigurationSection data) {
		ymlData = data;
	}
	
	//abstraction
	public abstract boolean isEmpire();
	
	//interfacing
	public ArrayList<String> getDefaultGlobalFlagsForGroup(TerritoryGroup group) {
		if(!ymlData.contains("flags." + group.toString())) {
			return new ArrayList<String>();
		}
		
		return (ArrayList<String>)ymlData.getList("flags." + group.toString());
	}
	
	public void broadcastMessageToJoined(String msg) {
		ArrayList<UUID> joined = getJoined();
		
		for(UUID id : joined) {
			Player recv = Bukkit.getPlayer(id);
			
			//is player online
			if(recv != null) {
				recv.sendMessage(msg);
			}
		}
	}
	
	public Relation getRelationWish(Joinable other) {
		if(other == null)
			return Relation.NEUTRAL;
		
		ConfigurationSection ourWishes = ymlData.getConfigurationSection("relation-wish");
		String otherName = other.getName();
		
		if(ourWishes.contains(otherName)) {
			try {
				return Relation.valueOf(ourWishes.getString(otherName));
			} catch(Exception e) {
				e.printStackTrace();
				
				return Relation.NEUTRAL;
			}
		} else {
			return Relation.NEUTRAL;
		}
	}
	
	public Set<String> getRelationWishSet() {
		return ymlData.getConfigurationSection("relation-wish").getKeys(false);
	}
	
	public Relation getRelation(Joinable other) {
		if(other == null)
			return Relation.NEUTRAL;
		
		if(other.getName() == this.getName())
			return Relation.US;
		
		//if we are the other's empire
		if(isEmpire()) {
			if(!other.isEmpire()) {
				Kingdom kOther = (Kingdom)other;
				if(kOther.getEmpire().equalsIgnoreCase(getName())) {
					return Relation.E_K;
				}
			}
		} else {//if the other is our empire
			if(other.isEmpire()) {
				Kingdom kUs = (Kingdom)this;
				if(kUs.getEmpire().equalsIgnoreCase(other.getName())) {
					return Relation.E_K;
				}
			}
		}
		
		Relation ourWish = getRelationWish(other);
		Relation theirWish = other.getRelationWish(this);
		
		if(ourWish.getIntValue() > theirWish.getIntValue())
			return ourWish;
		else
			return theirWish;
	}
	
	public boolean toggleDefaultGlobalFlagForGroup(TerritoryGroup group, TerritoryFlag flag) {
		ConfigurationSection flagSect = ymlData.getConfigurationSection("flags");
		ArrayList<String> flags = (ArrayList<String>)flagSect.getList(group.toString());
		
		if(flags.contains(flag.toString())) {
			flags.remove(flag.toString());
			flagSect.set(group.toString(), flags);
			
			Empires.m_boardHandler.updateTerritoryWithFlags(this, group, flags);
			
			return false;
		} else {
			flags.add(flag.toString());
			flagSect.set(group.toString(), flags);
			
			Empires.m_boardHandler.updateTerritoryWithFlags(this, group, flags);
			
			return true;
		}
	}
	
	public boolean togglePermissionForRole(Role role, GroupPermission perm) {
		ConfigurationSection permSect = ymlData.getConfigurationSection("permissions");
		ArrayList<String> flags = (ArrayList<String>)permSect.getList(role.toString());
		
		if(flags.contains(perm.toString())) {
			flags.remove(perm.toString());
			permSect.set(role.toString(), flags);
			
			return false;
		} else {
			flags.add(perm.toString());
			permSect.set(role.toString(), flags);
			
			return true;
		}
	}
	
	public ArrayList<UUID> getOfficers(int rank) {
		if(rank > 3 || rank < 1)
			return new ArrayList<UUID>();
		
		Role role;
		switch(rank) {
			case 1:
				role = Role.OFFICER_1;
			break;
			
			case 2:
				role = Role.OFFICER_2;
			break;
			
			case 3:
				role = Role.OFFICER_3;
			break;
			
			default:
				role = Role.OFFICER_3;
			break;
		}
		
		ArrayList<UUID> officers = new ArrayList<UUID>();
		
		for(UUID player : getJoined()) {
			//if(Empires.m_playerHandler.getPlayerRole(player).equals(role))
			if(Empires.m_playerHandler.getPlayer(player).getRole().equals(role))
				officers.add(player);
		}
		
		return officers;
	}
	
	public ArrayList<UUID> getJoined() {
		ArrayList<String> joined = (ArrayList<String>)ymlData.getList("joined-players");
		ArrayList<UUID> ids = new ArrayList<UUID>();
		
		//populate IDs
		if(joined != null) {
			for(String name : joined) {
				ids.add(UUID.fromString(name)); 
			}
		}
		
		return ids;
	}
	
	public void addPlayerPointer(UUID player) {
		/*ArrayList<UUID> players = getJoined();
		
		players.add(player);*/
		ArrayList<String> players = (ArrayList<String>)ymlData.getList("joined-players", new ArrayList<String>());
		players.add(player.toString());
		
		
		ymlData.set("joined-players", players);
	}
	
	public void findNewLeader(boolean announce) {
		UUID heirID = getHeir();
		
		if(heirID != null) {
			EmpiresPlayer heir = Empires.m_playerHandler.getPlayer(heirID);
			
			//if(Empires.m_playerHandler.getPlayerExists(heir)) {
			if(heir != null) {
				//if(Empires.m_playerHandler.getPlayerJoinedCivilization(heir).equals(getName())) {
				if(heir.getJoined().getName().equals(getName())) {
					//Empires.m_playerHandler.setPlayerRole(heir, Role.LEADER);
					heir.setRole(Role.LEADER);
					setHeir(null);
					
					//inform
					if(announce) {
						//OfflinePlayer officer = Bukkit.getPlayer(heir);
						//if(officer == null)
						//	officer = Bukkit.getOfflinePlayer(heir);
						OfflinePlayer officer = heir.getBukkitPlayer();
						if(officer == null)
							officer = Bukkit.getOfflinePlayer(heirID);
						
						broadcastMessageToJoined(ChatColor.YELLOW + officer.getName()  + " has become the new leader of " + getDisplayName() + "!");
					}
					
					return;
				}
			}
		}
		
		{
			ArrayList<UUID> officers;
			for(int i = 3; i >= 1; i--) {
				officers = getOfficers(i);
				if(officers.isEmpty())
					continue;
				
				//Empires.m_playerHandler.setPlayerRole(officers.get(0), Role.LEADER);
				Empires.m_playerHandler.getPlayer(officers.get(0)).setRole(Role.LEADER);
				
				if(announce) {
					OfflinePlayer officer = Bukkit.getPlayer(officers.get(0));
					if(officer == null)
						officer = Bukkit.getOfflinePlayer(officers.get(0));
					
					broadcastMessageToJoined(ChatColor.YELLOW + officer.getName()  + " has become the new leader of " + getDisplayName() + "!");
				}
				
				return;
			}
		}
		
		UUID one = getJoined().get(0);
		//Empires.m_playerHandler.setPlayerRole(one, Role.LEADER);
		Empires.m_playerHandler.getPlayer(one).setRole(Role.LEADER);
		
		if(announce) {
			OfflinePlayer officer = Bukkit.getPlayer(one);
			if(officer == null)
				officer = Bukkit.getOfflinePlayer(one);
			
			broadcastMessageToJoined(ChatColor.YELLOW + officer.getName()  + " has become the new leader of " + getDisplayName() + "!");
		}
	}
	
	//return true if disbanding
	public boolean removePlayerPointer(EmpiresPlayer player) {
		//ArrayList<UUID> players = getJoined();
		ArrayList<String> players = (ArrayList<String>)ymlData.getList("joined-players");
		if(players.remove(player.getBukkitPlayer().getUniqueId().toString())) {
			ymlData.set("joined-players", players);
			
			if(players.isEmpty()) {
				try {
					disband();
					return true;
				} catch (EmpiresJoinableDoesNotExistException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public UUID getLeader() {
		for(UUID player : getJoined()) {
			//if(Empires.m_playerHandler.getPlayerRole(player).equals(Role.LEADER)) {
			if(Empires.m_playerHandler.getPlayer(player).getRole().equals(Role.LEADER)) {
				return player;
			}
		}
		
		return null;
	}
	
	public UUID getHeir() {
		String heir = ymlData.getString("heir");
		if(heir.isEmpty())//is there a heir set?
			return null;
		
		return UUID.fromString(heir);
	}
	
	public String getDisplayName() {
		return ymlData.getString("d-name");
	}
	
	public String getDescription() {
		return ymlData.getString("desc");
	}
	
	public Location getHome() {
		ConfigurationSection home = ymlData.getConfigurationSection("home");
		
		int x = home.getInt("x");
		int y = home.getInt("y");
		int z = home.getInt("z");
		String w = home.getString("w");
		
		if(w == null)
			return null;
		
		World world = Bukkit.getWorld(w);
		if(world == null) {
			return null;//world does not exist!
		}
		
		return new Location(world, x, y, z);
	}
	
	public double getBankBalance() {
		return ymlData.getDouble("bank");
	}
	
	public boolean getPermissionForRole(Role role, GroupPermission perm) {
		if(role.equals(Role.LEADER))
			return true;
		
		if(!ymlData.isConfigurationSection("permissions")) {
			return false;
		}
		
		if(ymlData.getList(role.toString()) == null) {
			return false;
		}
		
		return ymlData.getConfigurationSection("permissions").getList(role.toString()).contains(perm.toString());
	}
	
	public int getPower() {
		return ymlData.getInt("power");
	}
	
	public void setPower(int val, boolean relative) {
		if(relative)
			ymlData.set("power", ymlData.getInt("power") + val);
		else
			ymlData.set("power", val);
	}
	
	public int getClaimSize() {
		return ymlData.getInt("claims");
	}
	
	public void setClaimSize(int val, boolean relative) {
		if(relative)
			ymlData.set("claims", ymlData.getInt("claims") + val);
		else
			ymlData.set("claims", val);
	}
	
	public boolean setName(String newName) {
		if(newName.contains(":") || newName.contains(".") || newName.contains("-"))
			return false;
		
		String oldName = getName();
		
		int power = ymlData.getInt("power");
		ArrayList<String> joinedPlayers = (ArrayList<String>)ymlData.getList("joined-players");
		ArrayList<String> requestedPlayers = (ArrayList<String>)ymlData.getList("requested-players");
		String desc = getDescription();
		String heir = "";
		if(getHeir() != null)
			heir = getHeir().toString();
		int claims = getClaimSize();
		double bank = getBankBalance();
		String empire = "";
		if(!isEmpire())
			empire = ((Kingdom)this).getEmpire();
		boolean isEmpire = isEmpire();
		ArrayList<String> requestedKingdoms = (ArrayList<String>)ymlData.getList("requested-kingdoms");
		ConfigurationSection home = ymlData.getConfigurationSection("home");
		ConfigurationSection relationWish = ymlData.getConfigurationSection("relation-wish");
		ConfigurationSection permissions = ymlData.getConfigurationSection("permissions");
		ConfigurationSection flags = ymlData.getConfigurationSection("flags");
		boolean ignoreRelations = ymlData.getBoolean("ignore-relations");
		
		ConfigurationSection newData = Empires.m_joinableHandler.createEmptyJoinableData(newName.toLowerCase());
		newData.set("power", power);
		newData.set("joined-players", joinedPlayers.clone());
		newData.set("requested-players", requestedPlayers.clone());
		newData.set("desc", desc);
		newData.set("heir", heir);
		newData.set("claims", claims);
		newData.set("bank", bank);
		newData.set("empire", empire);
		newData.set("d-name", newName);
		newData.set("is-empire", isEmpire());
		newData.set("kingdoms", new ArrayList<String>());
		newData.set("requested-kingdoms", requestedKingdoms.clone());
		newData.set("ignore-relations", ignoreRelations);
		
		ConfigurationSection newHome = newData.createSection("home");
		newHome.set("x", home.getInt("x"));
		newHome.set("y", home.getInt("y"));
		newHome.set("z", home.getInt("z"));
		newHome.set("w", home.getString("w"));
		
		ConfigurationSection newRelations = newData.createSection("relation-wish");
		Map<String, Object> relationMap = relationWish.getValues(true);
		for(String key : relationMap.keySet()) {
			newRelations.set(key, (String)relationMap.get(key));
		}
		
		ConfigurationSection newPermissions = newData.createSection("permissions");
		Map<String, Object> permissionMap = relationWish.getValues(true);
		for(String key : permissionMap.keySet()) {
			newPermissions.set(key, ((ArrayList<String>)permissionMap.get(key)).clone());
		}
		
		ConfigurationSection newFlags = newData.createSection("flags");
		Map<String, Object> flagMap = relationWish.getValues(true);
		for(String key : flagMap.keySet()) {
			newFlags.set(key, ((ArrayList<String>)flagMap.get(key)).clone());
		}
		
		for(UUID player : getJoined()) {
			Empires.m_playerHandler.getPlayer(player).setJoinedPointer(newName);
			/*try {
				Empires.m_playerHandler.overridePlayerJoinedCivilization(player, newName);
			} catch (EmpiresJoinableDoesNotExistException e) {
				e.printStackTrace();
			}*/
		}
		
		//if we are a kingdom in an empire, leave it under our old name
		Empire ourEmpire = null;
		if(!isEmpire() && empire != null && !empire.isEmpty()) {
			Kingdom kUs = (Kingdom)this;
			kUs.leaveEmpire();
			
			ourEmpire = (Empire)Empires.m_joinableHandler.getJoinable(empire);
		}
		
		Empires.m_joinableHandler.changeAllRelationWishNames(oldName, newName);
		Empires.m_boardHandler.renameAllTerritoryForJoinable(this, newName);
		
		Empires.m_joinableHandler.removeJoinable(this);
		this.ymlData = newData;//set all new data to us
		
		//rejoin our empire under our new name & data
		if(!isEmpire() && empire != null && !empire.isEmpty()) {
			Kingdom kUs = (Kingdom)this;
			kUs.setEmpire(ourEmpire);
		} else if(isEmpire()) {//if we are an empire, force all our kingdoms to rejoin us under our new name
			Empire eUs = (Empire)this;
			ArrayList<String> ourKingdoms = eUs.getKingdomSet();
			for(String joinedKingdom : ourKingdoms) {
				Kingdom kingdom = (Kingdom)Empires.m_joinableHandler.getJoinable(joinedKingdom);
				kingdom.setEmpire(eUs);
			}
		}
		
		return true;
	}
	
	public String getName() {
		return ymlData.getName();
	}
	
	public void setDescription(String desc) {
		ymlData.set("desc", desc);
	}
	
	public void setHome(Location loc) {
		ConfigurationSection home = ymlData.getConfigurationSection("home");
		home.set("x", loc.getX());
		home.set("y", loc.getY());
		home.set("z", loc.getZ());
		home.set("w", loc.getWorld().getName());
	}
	
	public void setHeir(UUID newHeir) {
		if(newHeir != null)
			ymlData.set("heir", newHeir.toString());
		else
			ymlData.set("heir", "");
	}
	
	public void setRelationWish(Joinable other, Relation newWish) {
		if(other == null)
			return;
		
		ConfigurationSection wishes = ymlData.getConfigurationSection("relation-wish");
		String otherName = other.getName();
		
		if(newWish.equals(Relation.NEUTRAL)) {
			if(wishes.contains(otherName)) {
				wishes.set(otherName, null);
			}
		} else {
			wishes.set(otherName, newWish.toString());
		}
	}
	
	public void disband() throws EmpiresJoinableDoesNotExistException {
		String displayName = getDisplayName();
		
		//remove player pointers
		{
			CopyOnWriteArrayList<UUID> playerList = new CopyOnWriteArrayList<UUID>(new HashSet<UUID>(getJoined()));
			
			for(UUID player : playerList) {
				//Empires.m_playerHandler.invokeRemovePlayerFromJoinedJoinable(player);
				Empires.m_playerHandler.getPlayer(player).leaveJoined();
			}
		}
		
		//clear relations
		Empires.m_joinableHandler.clearAllRelationWishesTo(this);
		
		//remove empire/kingdom specific info
		{
			if(isEmpire()) {
				Empire eUs = (Empire)this;
				ArrayList<String> joinedKingdoms = eUs.getKingdomSet();
				
				for(String kingdom : joinedKingdoms) {
					Kingdom k = (Kingdom)Empires.m_joinableHandler.getJoinable(kingdom);
					k.setEmpire(null);
				}
			} else {
				Kingdom kUs = (Kingdom)this;
				if(!kUs.getEmpire().isEmpty()) {
					Empire ourEmpire = (Empire)Empires.m_joinableHandler.getJoinable(kUs.getEmpire());
					
					if(ourEmpire != null) {
						ourEmpire.removeKingdom(kUs);
					}
				}
			}
		}
		
		//remove territory
		Empires.m_boardHandler.removeAllTerritoryForHost(this);
		
		//remove us
		Empires.m_joinableHandler.removeJoinable(this);
		
		//exclaim
		String civType = "Kingdom";
		if(isEmpire())
			civType = "Empire";
		
		Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "The " + civType + " '" + displayName + "' disbanded!");
	}
	
	public void invitePlayer(UUID player) {
		ArrayList<String> invited = (ArrayList<String>)ymlData.getList("requested-players");
		
		if(!invited.contains(player.toString())) {
			invited.add(player.toString());
			ymlData.set("requested-players", invited);
		}
	}
	
	public void uninvitePlayer(UUID player) {
		ArrayList<String> invited = (ArrayList<String>)ymlData.getList("requested-players");
		
		if(invited.remove(player.toString()))
			ymlData.set("requested-players", invited);
	}
	
	public boolean isPlayerInvited(UUID player) {
		return ((ArrayList<String>)ymlData.getList("requested-players")).contains(player.toString());
	}
	
	public void depositMoney(String depositer, double val) {
		if(Empires.m_economy.getBalance(depositer) >= val) {
			Empires.m_economy.withdrawPlayer(depositer, val);
			
			ymlData.set("bank", ymlData.getDouble("bank") + val);
		}
	}
	
	public void withdrawMoney(String withdrawer, double val) {
		double bankVal = ymlData.getDouble("bank");
		
		if(bankVal >= val) {
			ymlData.set("bank", bankVal - val);
			Empires.m_economy.depositPlayer(withdrawer, val);
		}
	}
	
	public boolean toggleAllowsMobs() {
		boolean toggleVal = !ymlData.getBoolean("spawn-mobs");
		ymlData.set("spawn-mobs", toggleVal);
		
		Empires.m_boardHandler.updateTerritoryAllowsMobs(this, toggleVal);
		
		return toggleVal;
	}
	
	public boolean getAllowsMobs() {
		return ymlData.getBoolean("spawn-mobs");
	}
	
	public boolean toggleIgnoreRelations() {
		boolean toggleVal = !ymlData.getBoolean("ignore-relations");
		ymlData.set("ignore-relations", toggleVal);
		
		Empires.m_boardHandler.updateTerritoryIgnoresRelations(this, toggleVal);
		
		return toggleVal;
	}
	
	public boolean getIgnoresRelations() {
		return ymlData.getBoolean("ignore-relations");
	}
}
