package com.pixelgriffin.empires.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.util.IDUtility;
import com.pixelgriffin.empires.util.IOUtility;

public class JoinableHandler extends DataHandler {
	
	//the civ file without plugin data folder directory
	private final String m_file = "joinable.dat";
	
	//TODO: add other defaults as final static fields
	public static final String m_defaultDescription = "Default description";
	public static final int m_defaultPower = 0;
	
	
	public JoinableHandler() {
		super();
	}
	
	/*
	 * Basic load/save
	 */
	
	public void loadFile(JavaPlugin _inst) {
		createDirectory();//attempt to create a non existent directory
		loadConfigSafe(m_file, _inst);//load the file
	}
	
	public void saveFile() {
		saveConfigSafe(m_file);
	}
	
	public void updateToUUIDs() {
		YamlConfiguration conf = getFileConfiguration();
		
		HashSet<String> joinables = (HashSet<String>)conf.getKeys(false);
		
		for(String joinable : joinables) {
			if(joinable.equals("data-version"))
				continue;
			
			ConfigurationSection sect = conf.getConfigurationSection(joinable);
			
			/*
			 * joined-players data conversion
			 */
			{
				ArrayList<UUID> ids = new ArrayList<UUID>();
				ArrayList<String> serialized = new ArrayList<String>();
				
				//get names
				ArrayList<String> requested = (ArrayList<String>) sect.getList("joined-players");
				
				//convert names to UUIDs
				for(String name : requested) {
					UUID id = IDUtility.getUUIDForPlayer(name);
					
					if(id != null)
						ids.add(id);
					else
						IOUtility.log("CRITICAL ERROR WHEN CONVERTING joinable.dat: null UUID for '" + name + "'");
				}
				
				//convert UUIDs to strings
				for(UUID id : ids) {
					serialized.add(id.toString());
				}
				
				//save UUID data
				sect.set("joined-players", serialized);
			}
			
			/*
			 * requested-players data conversion
			 */
			{
				ArrayList<UUID> ids = new ArrayList<UUID>();
				ArrayList<String> serialized = new ArrayList<String>();
				
				//get names
				ArrayList<String> requested = (ArrayList<String>) sect.getList("requested-players");
				
				//convert names to UUIDs
				for(String name : requested) {
					UUID id = IDUtility.getUUIDForPlayer(name);
					
					if(id != null)
						ids.add(id);
					else
						IOUtility.log("CRITICAL ERROR WHEN CONVERTING joinable.dat: null UUID for '" + name + "'");
				}
				
				//convert UUIDs to strings
				for(UUID id : ids) {
					serialized.add(id.toString());
				}
				
				//save UUID data
				sect.set("requested-players", serialized);
			}
			
			/*
			 * heir data conversion
			 */
			{
				String name = sect.getString("heir");
				if(name.equals(""))
					continue;
				
				UUID id = IDUtility.getUUIDForPlayer(name);
				sect.set("heir", id.toString());
			}
		}
	}
	
	/*
	 * Joinable specific construction
	 */
	public Joinable getJoinable(String name) {
		if(name.equals(PlayerHandler.m_defaultCiv))
			return null;
		
		ConfigurationSection data = getFileConfiguration().getConfigurationSection(name.toLowerCase());
		if(data != null) {
			if(data.getBoolean("is-empire")) {
				return new Empire(data);
			} else {
				return new Kingdom(data);
			}
		}
		
		return null;
	}
	
	public ArrayList<Joinable> getAllJoinables() {
		ArrayList<Joinable> all = new ArrayList<Joinable>();
		for(String name : getFileConfiguration().getKeys(false)) {
			if(name.equals("data-version"))
				continue;
			
			all.add(getJoinable(name));
		}
		
		return all;
	}
	
	public Kingdom createNewKingdom(String name) {
		if(name.contains(":") || name.contains(".") || name.contains("-"))
			return null;
		
		String lookup = name.toLowerCase();
		
		if(getFileConfiguration().isConfigurationSection(lookup))
			return null;
		
		ConfigurationSection sect = getFileConfiguration().createSection(lookup);
		sect.set("power", m_defaultPower);
		sect.set("joinedPlayers", new ArrayList<String>());
		sect.set("requested-players", new ArrayList<String>());
		sect.set("desc", m_defaultDescription);
		sect.set("heir", "");
		sect.set("claims", 0);
		sect.set("bank", 0.0);
		sect.set("empire", "");
		
		sect.set("d-name", name);
		sect.set("is-empire", false);
		sect.set("kingdoms", new ArrayList<String>());
		sect.set("requested-kingdoms", new ArrayList<String>());
		
		ConfigurationSection home = sect.createSection("home");
		home.set("x", 0);
		home.set("y", 0);
		home.set("z", 0);
		home.set("w", "");
		
		sect.createSection("relation-wish");
		
		//permissions
		ConfigurationSection permSect = sect.createSection("permissions");//permission set
		
		//set default permissions
		//gather list of defaults
		{
			ArrayList<String> memberPerms = new ArrayList<String>();
			ArrayList<String> officerPerms = new ArrayList<String>();
			for(GroupPermission perm : GroupPermission.values()) {
				if(perm.getMemberDefault()) {
					memberPerms.add(perm.toString());
				}
				if(perm.getOfficerDefault()) {
					officerPerms.add(perm.toString());
				}
			}
			
			permSect.set(Role.MEMBER.toString(), memberPerms);
			permSect.set(Role.OFFICER_1.toString(), officerPerms.clone());
			permSect.set(Role.OFFICER_2.toString(), officerPerms.clone());
			permSect.set(Role.OFFICER_3.toString(), officerPerms.clone());
		}
		
		//set default flags
		ConfigurationSection flagSect = sect.createSection("flags");
		ArrayList<String> flagList = new ArrayList<String>();
		
		for(TerritoryGroup group : TerritoryGroup.values()) {
			for(TerritoryFlag flag : TerritoryFlag.values()) {
				if(group.getId() >= flag.getBaseGroup().getId()) {
					flagList.add(flag.toString());
				}
			}
			
			flagSect.set(group.toString(), flagList);
			flagList = new ArrayList<String>();
		}
		
		//set single value flags
		sect.set("ignore-relations", false);
		sect.set("spawn-mobs", false);
		
		return new Kingdom(sect);
	}
	
	public void removeJoinable(Joinable remove) {
		getFileConfiguration().set(remove.getName(), null);
	}
	
	public ConfigurationSection createEmptyJoinableData(String name) {
		if(!getFileConfiguration().isConfigurationSection(name)) {
			return getFileConfiguration().createSection(name);
		}
		
		return null;
	}
	
	public void changeAllRelationWishNames(String oldName, String newName) {
		for(String joinable : getFileConfiguration().getKeys(false)) {
			if(joinable.equalsIgnoreCase(oldName) || joinable.equalsIgnoreCase(newName) || joinable.equals("data-version"))
				continue;
			
			Relation rel = getRelationByName(joinable, oldName);
			
			if(!rel.equals(Relation.NEUTRAL)) {
				setRelationByName(joinable, newName, rel);
				setRelationByName(joinable, oldName, Relation.NEUTRAL);
			}
		}
	}
	
	public void clearAllRelationWishesTo(Joinable clear) {
		for(String joinable : getFileConfiguration().getKeys(false)) {
			if(joinable.equalsIgnoreCase(clear.getName()) || joinable.equals("data-version"))
				continue;
			
			Joinable j = getJoinable(joinable);
			j.setRelationWish(clear, Relation.NEUTRAL);
		}
	}
	
	public Relation getRelationByName(String _a, String _b) {
		YamlConfiguration conf = getFileConfiguration();
		
		_a = _a.toLowerCase();
		_b = _b.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_a);
		if(sect == null)
			return null;
		
		ConfigurationSection wishes = sect.getConfigurationSection("relation-wish");
		
		if(wishes.contains(_b)) {
			try {
				//attempt to return the relation wish for _b
				return Relation.valueOf(wishes.getString(_b));
			} catch(Exception e) {//off chance that the relation is messed up
				e.printStackTrace();//print the error
				return Relation.NEUTRAL;//return neutral
			}
		} else {
			//we have no wishes towards them
			return Relation.NEUTRAL;
		}
	}
	
	private void setRelationByName(String _joinableName, String _otherName, Relation _wish) {
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_joinableName.toLowerCase());
		if(sect == null)
			return;
		
		ConfigurationSection wishSect = sect.getConfigurationSection("relation-wish");
		
		//proper lookup
		_otherName = _otherName.toLowerCase();
		
		//set section value
		//since comparing relations will return neutral if no wish is given
		if(_wish.equals(Relation.NEUTRAL)) {
			if(wishSect.contains(_otherName)) {//if we have a wish towards them
				wishSect.set(_otherName, null);//we remove it
			}
		} else {
			//if it's not neutral we need to keep track of the wish
			wishSect.set(_otherName, _wish.toString());
		}
	}
}
