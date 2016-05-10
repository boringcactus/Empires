package com.pixelgriffin.empires.handler;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresEmptyTerritoryException;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;

/**
 * 
 * @author Nathan
 *
 */
@SuppressWarnings("unchecked")
public class BoardHandler extends DataHandler {
	
	//the board file without plugin data folder directory
	public static final String m_file = "board.dat";
	
	public BoardHandler() {
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
	
	/*
	 * Board specific loading
	 */
	
	/**
	 * Claims territory for _id. Copies flags, data & changes claim size
	 * @param _loc
	 * @param _id
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public void claimTerritoryForJoinable(Location _loc, Joinable owner) throws EmpiresJoinableDoesNotExistException {
		
		
		setTerritoryHostAt(_loc, owner);//copy over flags & host

		//claim size
		//Empires.m_joinableHandler.setJoinableClaimSize(_id, 1, true);
		owner.setClaimSize(1, true);
	}
	
	public void unclaimAllTerritoryForJoinable(Joinable owner) throws EmpiresJoinableDoesNotExistException {
		removeAllTerritoryForHost(owner);
		
		//set claim size to 0
		//Empires.m_joinableHandler.setJoinableClaimSize(_id, 0);
		owner.setClaimSize(0, false);
		
	}
	
	public void unclaimTerritoryForJoinable(Location _loc, Joinable owner) throws EmpiresEmptyTerritoryException, EmpiresJoinableDoesNotExistException {
		removeTerritoryAt(_loc);
		
		//claim size -1
		//Empires.m_joinableHandler.setJoinableClaimSize(_id, -1, true);
		owner.setClaimSize(-1, true);
	}
	
	private void removeTerritoryAt(Location _loc) throws EmpiresEmptyTerritoryException {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		//guess the territory is already gone!
		if(sect == null)
			throw new EmpiresEmptyTerritoryException("Tried to remove empty territory!");
		
		YamlConfiguration conf = getFileConfiguration();
		
		//gather the path
		String path = _loc.getWorld().getName() + "." + _loc.getChunk().getX() + "." + _loc.getChunk().getZ();
		
		//delete
		conf.set(path, null);
	}
	
	/**
	 * Sets the host data for a territory at a certain location.
	 * Sets the territory flags from the new host
	 * @param _loc - location of claim
	 * @throws EmpiresJoinableDoesNotExistException 
	 */
	public void setTerritoryHostAt(Location _loc, Joinable owner) throws EmpiresJoinableDoesNotExistException {
		//proper lookup
		/*_id = _id.toLowerCase();
		
		if(!Empires.m_joinableHandler.getJoinableExists(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to set territory host to a non-existent joinable '" + _id + "'");*/
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = getTerritorySection(_loc);
		
		//gather configuration section
		if(sect == null) {
			sect = conf.createSection(_loc.getWorld().getName() + "." +  _loc.getChunk().getX() + "." + _loc.getChunk().getZ());//create a configuration section if one does not exist!
		}
		
		//set new host name
		sect.set("h", owner.getName());
		
		//create blank access list
		sect.set("a", new ArrayList<String>());
		
		//copy over new host's flags
		//IMPORTANT: relies on JoinableHandler instantiation in Empires
		
		//iterate over groups
		ArrayList<String> flags;//temporary array list of the flags to copy
		for(TerritoryGroup grouping : TerritoryGroup.values()) {
			//gather flags for this group
			//flags = Empires.m_joinableHandler.getJoinableGlobalFlagsForGroup(_id, grouping);
			flags = owner.getDefaultGlobalFlagsForGroup(grouping);
			
			//set flags for this group
			sect.set("f." + grouping.toString(), flags.clone());
		}
		
		//add the single value flags
		sect.set("ignore-relations", owner.getIgnoresRelations());
		sect.set("spawn-mobs", owner.getAllowsMobs());
	}
	
	/**
	 * Removes all territory in every world for the host _id. Does not affect claim size data
	 * @param _id host id
	 * @throws EmpiresJoinableDoesNotExistException 
	 */
	public void removeAllTerritoryForHost(Joinable owner) throws EmpiresJoinableDoesNotExistException {
		//proper lookup, requires lowercase
		//_id = _id.toLowerCase();
		
		//if(!Empires.m_joinableHandler.getJoinableExists(_id))
		//	throw new EmpiresJoinableDoesNotExistException("Tried to remove territory from a non-existent joinable '" + _id + "'");
		
		YamlConfiguration conf = getFileConfiguration();
		
		//iterate through all sections of the board YML
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			//is this a configuration sect?
			if(conf.isConfigurationSection(path)) {
				//set to allocated object
				workingSect = conf.getConfigurationSection(path);
				
				//if the section contiains the "h" value
				//then it is a chunk data section
				if(workingSect.contains("h")) {
					//if the host is _id
					if(workingSect.getString("h").equals(owner.getName())) {
						//remove this section
						conf.set(path, null);
					}
				}
			}
		}
	}
	
	public String getTerritoryHost(Location _loc) {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		//System.out.println("ter host");
		
		//if the path exists
		//meaning somewhere along the lines that shit was set
		if(sect != null) {
			//return "h" host value
			return sect.getString("h");
		}
		
		//otherwise return empty
		return PlayerHandler.m_defaultCiv;
	}
	
	public String getTerritoryHost(int _cx, int _cz, String _w) {
		ConfigurationSection sect;
		String path;
		
		path = _w + "." + _cx + "." + _cz;
		
		YamlConfiguration conf = getFileConfiguration();
		
		if(!conf.isConfigurationSection(path))
			return PlayerHandler.m_defaultCiv;
		
		sect = getFileConfiguration().getConfigurationSection(path);
		
		return sect.getString("h");
	}
	
	public boolean toggleTerritoryAccessFor(Location _loc, UUID _id) throws EmpiresEmptyTerritoryException {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		//if the path doesn't exist
		if(sect == null)
			throw new EmpiresEmptyTerritoryException("Tried to change access for player " + _id.toString() + " at a null territory");//we can't set access
		
		ArrayList<String> accessList = (ArrayList<String>)sect.getList("a");
		
		String idString = _id.toString();
		
		if(accessList.contains(idString)) {
			accessList.remove(idString);
			
			sect.set("a", accessList);
			
			return false;
		} else {
			accessList.add(idString);
			
			sect.set("a", accessList);
			
			return true;
		}
	}
	
	public boolean territoryHasAccessFor(Location _loc, UUID _id) {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		if(sect == null)
			return false;
		
		ArrayList<String> accessList = (ArrayList<String>)sect.getList("a");
		
		String idString = _id.toString();
		
		return accessList.contains(idString);
	}
	
	public void renameAllTerritoryForJoinable(Joinable owner, String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			if(conf.isConfigurationSection(path)) {
				workingSect = conf.getConfigurationSection(path);
				
				if(workingSect.contains("h")) {
					if(workingSect.getString("h").equals(owner.getName())) {
						workingSect.set("h", _name);
					}
				}
			}
		}
	}
	
	public boolean hasJoinableClaimedInWorld(String _w, Joinable owner) {
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			//could hold territory
			if(conf.isConfigurationSection(path)) {
				//set to allocated object
				workingSect = conf.getConfigurationSection(path);
				
				//is it territory?
				if(workingSect.contains("h")) {
					//is the territory _id's?
					if(workingSect.getString("h").equals(owner.getName()))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isLocationSurrounded(Joinable owner, Location _loc) {
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup
		ConfigurationSection workingSect;//working section
		//Location workingLoc;
		String path;
		String world = _loc.getWorld().getName();
		int lx = _loc.getChunk().getX();
		int ly = _loc.getChunk().getZ();
		
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				// 0 x 0
				// x 0 x
				// 0 x 0
				
				//if one of the coords is not 0
				//does not count center or corners
				if((x == 0 && y != 0) || (x != 0 && y == 0)) {
					//do not use getTerritorySection()
					//must increase chunkX by x and chunkY by y
					path = world + "." + (lx + x) + "." + (ly + y);

					//test path
					if(conf.isConfigurationSection(path)) {
						workingSect = conf.getConfigurationSection(path);
						
						if(workingSect.contains("h")) {
							//found us around here
							if(workingSect.getString("h").equals(owner.getName()))
								return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/*
	 * FIXME
	 * This method could potentially be much faster by storing flags NOT as lists
	 * but as sections, then checking if something exists is as simple as
	 * sect.contains(_flag.toString());
	 * 
	 * or even just getting tempList once
	 */
	public boolean territoryHasFlag(Location _loc, TerritoryGroup _group, TerritoryFlag _flag) {
		ConfigurationSection sect;
		
		sect = getTerritorySection(_loc);
		
		if(sect != null) {
			if(!sect.isConfigurationSection("f"))
				return false;
			
			ArrayList<String> tempList = (ArrayList<String>)sect.getConfigurationSection("f").getList(_group.toString());//NPEing
			if(tempList == null)
				return false;
			
			return tempList.contains(_flag.toString());
		}
		
		return false;
	}
	
	public boolean toggleTerritoryFlag(Location _loc, TerritoryGroup _group, TerritoryFlag _flag) throws EmpiresEmptyTerritoryException {
		ConfigurationSection sect;
		sect = getTerritorySection(_loc);
		
		System.out.println("flag toggle ter");
		
		if(sect == null)
			throw new EmpiresEmptyTerritoryException("Tried to toggle " + _flag.toString() + " for " + _group.toString() +" for empty territory");
		
		ArrayList<String> flags = (ArrayList<String>)sect.getConfigurationSection("f").getList(_group.toString());
		
		boolean ret;
		
		if(flags.contains(_flag.toString())) {
			flags.remove(_flag.toString());
			ret = false;
		} else {
			flags.add(_flag.toString());
			ret = true;
		}
		
		//update the flags
		sect.getConfigurationSection("f").set(_group.toString(), flags.clone());
		
		return ret;
	}
	
	public boolean territoryIgnoresRelations(Location _loc) {
		ConfigurationSection sect;
		
		sect = getTerritorySection(_loc);
		
		//if the territory exists
		if(sect != null)
			return sect.getBoolean("ignore-relations");//return the ignore-relations value
		else
			return false;//otherwise we will not ignore relations
	}
	
	public boolean territoryAllowsMobs(Location _loc) {
		ConfigurationSection sect;
		
		sect = getTerritorySection(_loc);
		
		//if the territory exists
		if(sect != null)
			return sect.getBoolean("spawn-mobs");//return the ignore-relations value
		else
			return false;//otherwise we will not ignore relations
	}
	
	public boolean toggleTerritoryIgnoresRelations(Location _loc) throws EmpiresEmptyTerritoryException {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		if(sect == null)
			throw new EmpiresEmptyTerritoryException("Tried to toggle IGNORE_RELATIONS for empty territory");
		
		//toggle
		boolean toggleVal = !sect.getBoolean("ignore-relations");
		sect.set("ignore-relations", toggleVal);
		
		return toggleVal;
	}
	
	public boolean toggleTerritoryAllowsMobs(Location _loc) throws EmpiresEmptyTerritoryException {
		ConfigurationSection sect = getTerritorySection(_loc);
		
		if(sect == null)
			throw new EmpiresEmptyTerritoryException("Tried to toggle SPAWN_MOBS for empty territory");
		
		//toggle
		boolean toggleVal = !sect.getBoolean("spawn-mobs");
		sect.set("spawn-mobs", toggleVal);
		
		return toggleVal;
	}
	
	public void updateTerritoryIgnoresRelations(Joinable owner, boolean _val) {
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			//could hold territory
			if(conf.isConfigurationSection(path)) {
				//set to allocated object
				workingSect = conf.getConfigurationSection(path);
				
				//is it territory?
				if(workingSect.contains("h")) {
					//is the territory _id's?
					if(workingSect.getString("h").equals(owner.getName())) {
						workingSect.set("ignore-relations", _val);
					}
				}
			}
		}
	}
	
	public void updateTerritoryAllowsMobs(Joinable owner, boolean _val) {
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			//could hold territory
			if(conf.isConfigurationSection(path)) {
				//set to allocated object
				workingSect = conf.getConfigurationSection(path);
				
				//is it territory?
				if(workingSect.contains("h")) {
					//is the territory _id's?
					if(workingSect.getString("h").equals(owner.getName())) {
						workingSect.set("spawn-mobs", _val);
					}
				}
			}
		}
	}
	
	public void updateTerritoryWithFlags(Joinable owner, TerritoryGroup _g, ArrayList<String> _flags) {
		YamlConfiguration conf = getFileConfiguration();
		
		System.out.println("update");
		
		ConfigurationSection workingSect;
		for(String path : conf.getKeys(true)) {
			if(path.equals("data-version"))
				continue;
			
			//could hold territory
			if(conf.isConfigurationSection(path)) {
				//set to allocated object
				workingSect = conf.getConfigurationSection(path);
				
				//is it territory?
				if(workingSect.contains("h")) {
					//is the territory _id's?
					if(workingSect.getString("h").equals(owner.getName())) {
						workingSect.getConfigurationSection("f").set(_g.toString(), _flags.clone());
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param _loc location of the territory
	 * @return returns the configuration section or null if the section does not exist
	 */
	private ConfigurationSection getTerritorySection(Location _loc) {
		ConfigurationSection sect;
		String path;
		
		path = _loc.getWorld().getName() + "." + _loc.getChunk().getX() + "." + _loc.getChunk().getZ();
		
		if(getFileConfiguration().isConfigurationSection(path)) {
			sect = getFileConfiguration().getConfigurationSection(path);
		} else {
			sect = null;
		}
		
		return sect;
	}
}