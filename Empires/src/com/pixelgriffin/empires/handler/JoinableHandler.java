package com.pixelgriffin.empires.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.GroupPermission;
import com.pixelgriffin.empires.enums.Relation;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.enums.TerritoryFlag;
import com.pixelgriffin.empires.enums.TerritoryGroup;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresJoinableExistsException;
import com.pixelgriffin.empires.exception.EmpiresJoinableInvalidCharacterException;
import com.pixelgriffin.empires.exception.EmpiresJoinableIsEmpireException;
import com.pixelgriffin.empires.exception.EmpiresJoinableIsNotEmpireException;
import com.pixelgriffin.empires.exception.EmpiresNoFundsException;
import com.pixelgriffin.empires.exception.EmpiresPlayerExistsException;
import com.pixelgriffin.empires.util.IOUtility;

/**
 * <br />
 * <p>
 * Holds all joinable YML interaction related methods.<br />
 * <ul>
 * <li>All actions are prefixed with 'invoke'.</li>
 * <li>All setters are prefixed with 'set'.</li>
 * <li>All getters are prefixed with 'get'.</li>
 * <li>All togglers are prefixed with 'toggle'.</li>
 * <li>All methods relating to a generic joinable contain 'Joinable' after their prefix.</li>
 * <li>All methods relating specifically to empires contain 'Empire' after their prefix.</li>
 * <li>All methods relating specifically to kingdoms contain 'Kingdom' after their prefix.</li>
 * </ul>
 * </p>
 * <br />
 * 
 * @author Nathan
 */
@SuppressWarnings("unchecked")
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
	
	/*
	 * Joinable specific loading
	 */
	
	/**
	 * <br />
	 * <p>
	 * Checks if a joinable exists by the name of <b>_name</b>
	 * </p>
	 * <br />
	 * 
	 * @param _name - The {@link String} of the name of the joinable
	 * @return Returns true if the joinable exists
	 */
	public boolean getJoinableExists(String _name) {
		return getFileConfiguration().isConfigurationSection(_name.toLowerCase());
	}
	
	/**
	 * <br />
	 * <p>
	 * Creates a blank default joinable with the name <b>_name</b>
	 * </p>
	 * <br />
	 * 
	 * @param _name - The {@link String} of the joinable name
	 * @throws EmpiresJoinableExistsException <p>Thrown when a joinable by the name of <b>_name</b> already exists</p>
	 * @throws EmpiresJoinableInvalidCharacterException <p>Thrown when the name <b>_name</b> contains a ':', '.', or '&' character</p>
	 */
	public void invokeCreateBlankJoinable(String _name) throws EmpiresJoinableExistsException, EmpiresJoinableInvalidCharacterException {
		//we cannot create a blank joinable with the same name as the default
		if(_name.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			throw new EmpiresJoinableExistsException("Attempted to create blank joinable with default name '" + _name + "'");
		
		if(_name.contains(":") || _name.contains(".") || _name.contains("&"))
			throw new EmpiresJoinableInvalidCharacterException("Attempted to create joinable with the name '" + _name + "'");
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the id for case insensitive lookup
		String lookupID = _name.toLowerCase();
		
		//we cannot overwrite an existing joinable
		if(conf.isConfigurationSection(lookupID))
			throw new EmpiresJoinableExistsException("Found existing joinable when attempting to create blank joinable '" + _name + "'");
		
		//create default joinable
		ConfigurationSection sect = conf.createSection(lookupID);
		sect.set("power", m_defaultPower);//power
		sect.set("joined-players", new ArrayList<String>());//joined players
		sect.set("requested-players", new ArrayList<String>());//requested players
		sect.set("desc", m_defaultDescription);//description
		sect.set("heir", "");//heir of the joinable
		sect.set("claims", 0);//claim size
		sect.set("bank", 0.0);//bank value
		sect.set("empire", "");//empire pointer
		
		//new values
		sect.set("d-name", _name);//display name
		sect.set("is-empire", false);//is empire?
		sect.set("kingdoms", new ArrayList<String>());//kingdom list for empires
		sect.set("requested-kingdoms", new ArrayList<String>());
		
		//hashmaps & vector3
		ConfigurationSection homeSect = sect.createSection("home");//e home
		homeSect.set("x", 0);
		homeSect.set("y", 0);
		homeSect.set("z", 0);
		homeSect.set("w", "");
		
		sect.createSection("relation-wish");//relation wish set
		
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
	}
	
	/**
	 * <br />
	 * <p>
	 * Sends a message <b>_msg</b> to all online players in the joinable <b>_name</b>
	 * </p>
	 * <br />
	 * 
	 * @param _name - The {@link String} of the joinable name
	 * @param _msg - The {@link String} of the message sent
	 * @throws EmpiresJoinableDoesNotExistException <p>Thrown when the joinable <b>_name</b> does not exist</p>
	 */
	public void invokeJoinableBroadcastToJoined(String _name, String _msg) throws EmpiresJoinableDoesNotExistException {
		//cannot broadcast to wilderness
		if(_name.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup (not case sensitive)
		_name = _name.toLowerCase();
		
		//make sure the joinable exists
		if(!conf.isConfigurationSection(_name))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch a non-existent joinable '" + _name + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		
		//gather players
		ArrayList<String> joinedPlayers = (ArrayList<String>)sect.getList("joined-players");
		
		//iterate through players
		Player player;
		for(String playerName : joinedPlayers) {
			player = Bukkit.getPlayer(playerName);//gather player
			
			//is the player online?
			if(player != null) {
				player.sendMessage(_msg);//send the message
			}
		}
	}
	
	/**
	 * <br />
	 * <p>
	 * Sends a message <b>_msg</b> to all online players in the empire '<b>_name</b>', including involved kingdoms
	 * </p>
	 * <br />
	 * 
	 * @param _name - The {@link String} of the empire name
	 * @param _msg - The {@link String} of the message to send
	 * @throws EmpiresJoinableDoesNotExistException <p>Thrown when the empire is found to not exist. This is not thrown when the joinable is not an empire.</p>
	 */
	public void invokeEmpireBroadcastToNetwork(String _name, String _msg) throws EmpiresJoinableDoesNotExistException {
		//nope
		if(_name.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_name = _name.toLowerCase();
		
		//make sure the joinable exists
		if(!conf.isConfigurationSection(_name))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch a non-existent joinable '" + _name + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		
		//iterate through joined kingdoms
		ArrayList<String> players;
		Player sendTo;
		for(String kingdom : (ArrayList<String>)sect.getList("kingdoms")) {
			players = getJoinableJoinedPlayers(kingdom);
			
			for(String player : players) {
				sendTo = Bukkit.getPlayer(player);
				
				if(sendTo != null) {
					sendTo.sendMessage(_msg);
				}
			}
		}
		
		//send to ourselves
		invokeJoinableBroadcastToJoined(_name, _msg);
	}
	
	/**
	 * <br />
	 * <p>
	 * Returns the relation <i>wish</i> of a joinable <b>_a</b> towards the joinable <b>_b</b>. <br/> Will not return the {@link Relation} E_K or US
	 * </p>
	 * <br />
	 * 
	 * @param _a - The {@link String} name of a joinable who holds the relationship
	 * @param _b - The {@link String} name of a joinable that <b>_a</b> holds a relationship towards
	 * @return The {@link Relation} relationship status
	 * @throws EmpiresJoinableDoesNotExistException <p>Thrown when the joinables <b>_a</b> or <b>_b</b> does not exist</p>
	 */
	public Relation getJoinableRelationWish(String _a, String _b) throws EmpiresJoinableDoesNotExistException {
		//real civilizations cannot maintain diplomacy with the default civ (wilderness)
		if(_a == PlayerHandler.m_defaultCiv || _b == PlayerHandler.m_defaultCiv)
			return Relation.NEUTRAL;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lowercase for proper lookup
		_a = _a.toLowerCase();
		_b = _b.toLowerCase();
		
		//verify that these joinables exist
		if(!conf.isConfigurationSection(_a))
			throw new EmpiresJoinableDoesNotExistException("Tried to gather relation information from non-existent A joinable '" + _a + "'");
		
		if(!conf.isConfigurationSection(_b))
			throw new EmpiresJoinableDoesNotExistException("Tried to gather relation information from non-existent B joinable '" + _b + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_a);
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
	
	/**
	 * <br />
	 * <p>
	 * Returns the <i>actual</i> current {@link Relation} relationship held between the joinables <b>_a</b> and <b>_b</b>.
	 * <p>
	 * <br />
	 * 
	 * @param _a - The {@link String} name of the first joinable
	 * @param _b - The {@link String} name of the second joinable
	 * @return Returns the {@link Relation} currently held between the joinables <b>_a</b> and <b>_b</b>
	 * @throws EmpiresJoinableDoesNotExistException <p>Thrown when either joinable <b>_a</b> or <b>_b</b> does not exist</p>
	 */
	public Relation getJoinableRelationTo(String _a, String _b) throws EmpiresJoinableDoesNotExistException {
		//real civilizations cannot maintain diplomacy with the default civ (wilderness)
		if(_a.equalsIgnoreCase(PlayerHandler.m_defaultCiv) || _b.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return Relation.NEUTRAL;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lowercase for proper lookup
		_a = _a.toLowerCase();
		_b = _b.toLowerCase();
		
		//verify that these joinables exist
		if(!conf.isConfigurationSection(_a))
			throw new EmpiresJoinableDoesNotExistException("Tried to gather relation information from non-existent A joinable '" + _a + "'");
		
		if(!conf.isConfigurationSection(_b))
			throw new EmpiresJoinableDoesNotExistException("Tried to gather relation information from non-existent B joinable '" + _b + "'");
		
		//if someone is comparing two same-strings
		//we are us!
		if(_a.equals(_b))
			return Relation.US;
		
		//gather sections
		ConfigurationSection sectA = conf.getConfigurationSection(_a);
		ConfigurationSection sectB = conf.getConfigurationSection(_b);
		
		//gather data for later usage
		boolean empireA = sectA.getBoolean("is-empire");
		boolean empireB = sectB.getBoolean("is-empire");
		
		//check for empire relationship
		//a is an empire
		if(empireA) {
			//b is NOT an empire
			if(!empireB) {
				//then an empire relationship is possible
				//b points to A's name
				if(sectB.getString("empire").equals(_a)) {
					return Relation.E_K;
				}
			}
		}
		
		//b is an empire
		if(empireB) {
			if(!empireA) {
				if(sectA.getString("empire").equals(_b)) {
					return Relation.E_K;
				}
			}
		}
		
		//gather relation wishes
		Relation wishA = getJoinableRelationWish(_a, _b);
		Relation wishB = getJoinableRelationWish(_b, _a);
		
		//if one wish carries more weight than the other return the more important wish
		//if they are the same it doesn't matter which is returned so just return B
		if(wishA.getIntValue() > wishB.getIntValue()) {
			return wishA;
		} else {
			return wishB;
		}
	}
	
	/**
	 * <br/>
	 * <p>
	 * Returns a {@link GroupPermission} {@link String} {@link ArrayList} of all enabled global flags for the joinable <b>_joinableName</b>.<br />Does not return the actual {@link GroupPermission}, only a {@link String} version of it.
	 * </p>
	 * <br/>
	 * 
	 * @param _joinableName - The {@link String} name of the joinable to gather flags from
	 * @param _group - The {@link TerritoryGroup} to gather the flags for
	 * @return Returns a {@link GroupPermission} {@link String} {@link ArrayList}
	 * @throws EmpiresJoinableDoesNotExistException <p>Thrown when the joinable <b>_joinableName</b> does not exist</p>
	 */
	public ArrayList<String> getJoinableGlobalFlagsForGroup(String _joinableName, TerritoryGroup _group) throws EmpiresJoinableDoesNotExistException {
		//if the group we're fetching is the default (wilderness) for whatever reason
		if(_joinableName.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return new ArrayList<String>();//we return an empty list
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lowercase for proper lookup
		_joinableName = _joinableName.toLowerCase();
		
		//if the civilization does not exist we cannot continue
		if(!conf.isConfigurationSection(_joinableName))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch flags from a non-existent joinable '" + _joinableName + "'");
		
		//gather joinable
		ConfigurationSection sect = conf.getConfigurationSection(_joinableName);
		
		//return a new array list if no flags exist for this group
		if(!sect.contains("flags." + _group.toString()))
			return new ArrayList<String>();
		
		return (ArrayList<String>)sect.getList("flags." + _group.toString());
	}
	
	/**
	 * Updates all territory when the flag is toggled
	 * @param _id
	 * @param _g
	 * @param _f
	 * @return
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public boolean toggleJoinableFlag(String _id, TerritoryGroup _g, TerritoryFlag _f) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		ConfigurationSection flagSect = sect.getConfigurationSection("flags");
		
		ArrayList<String> flags = (ArrayList<String>)flagSect.getList(_g.toString());
		
		if(flags.contains(_f.toString())) {
			flags.remove(_f.toString());
			
			flagSect.set(_g.toString(), flags);
			
			//update territory
			Empires.m_boardHandler.updateTerritoryWithFlags(_id, _g, flags);
			
			return false;
		} else {
			flags.add(_f.toString());
			
			flagSect.set(_g.toString(), flags);
			
			//update territory
			Empires.m_boardHandler.updateTerritoryWithFlags(_id, _g, flags);
			
			return true;
		}
	}
	
	public boolean toggleJoinablePermission(String _id, GroupPermission _p, Role _r) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		ConfigurationSection permSect = sect.getConfigurationSection("permissions");
		
		ArrayList<String> flags = (ArrayList<String>)permSect.getList(_r.toString());
		
		if(flags.contains(_p.toString())) {
			flags.remove(_p.toString());
			
			permSect.set(_r.toString(), flags);
			return false;
		} else {
			flags.add(_p.toString());
			
			permSect.set(_r.toString(), flags);
			return true;
		}
	}
	
	/**
	 * Updates all territory when the flag is toggled
	 */
	public boolean toggleJoinableIgnoresRelations(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		boolean toggleVal = !sect.getBoolean("ignore-relations");
		sect.set("ignore-relations", toggleVal);
		
		//update territory
		Empires.m_boardHandler.updateTerritoryIgnoresRelations(_id, toggleVal);
		
		return toggleVal;
	}
	
	/**
	 * Updates all territory when the flag is toggled
	 */
	public boolean toggleJoinableAllowsMobs(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		boolean toggleVal = !sect.getBoolean("spawn-mobs");
		sect.set("spawn-mobs", toggleVal);
		
		//update territory
		Empires.m_boardHandler.updateTerritoryAllowsMobs(_id, toggleVal);
		
		return toggleVal;
	}
	
	/**
	 * Removes a player from a civilization joined-player list. Does not change player's "j" pointer data
	 * @param _id - civ id
	 * @param _playerName - player name
	 * @throws EmpiresJoinableDoesNotExistException _id does not exist in YML
	 */
	public void invokeJoinableRemovePlayer(String _id, String _playerName) throws EmpiresJoinableDoesNotExistException {
		//if the id we're looking up happens to be the default civilization (wilderness)
		//then we can't remove a player
		if(_id.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lowercase for proper lookup
		_id = _id.toLowerCase();
		
		//tried to fetch a non-existent j
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch a non-existent joinable '" + _id + "'");
		
		//gather the current joined players list
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		ArrayList<String> joinedPlayers = (ArrayList<String>)sect.getList("joined-players");
		
		//if the player is inside the joined player list remove him and set the new list
		if(joinedPlayers.contains(_playerName)) {
			joinedPlayers.remove(_playerName);//remove
			
			IOUtility.log("removed " + _playerName);
			
			//disband if this player was the last in the civilization
			//cannot have empty civilizations
			if(joinedPlayers.isEmpty()) {
				IOUtility.log("invoking disband");
				invokeJoinableDisband(_id);
				return;
			}
			
			sect.set("joined-players", joinedPlayers);//set
		}
	}
	
	/**
	 * Finds a new leader. Careful to remove the old leader first or exceptions will be thrown
	 * @param _id
	 * @throws EmpiresJoinableDoesNotExistException 
	 * @throws EmpiresPlayerExistsException 
	 */
	public void invokeJoinableFindNewLeader(String _id, boolean _announce) throws EmpiresJoinableDoesNotExistException, EmpiresPlayerExistsException {
		if(getCheck(_id))
			return;
		
		_id = _id.toLowerCase();
		
		if(getJoinableLeader(_id) != null)
			throw new EmpiresPlayerExistsException("A leader has already been appointed for '" + _id + "'");
		
		String heir = getJoinableHeir(_id);
		
		//if there an heir
		if(!heir.equals("")) {
			//does the player exist?
			if(Empires.m_playerHandler.playerExists(heir)) {
				if(Empires.m_playerHandler.getPlayerJoinedCivilization(heir).equals(_id)) {
					Empires.m_playerHandler.setPlayerRole(heir, Role.LEADER);
					
					//remove the old heir value
					Empires.m_joinableHandler.clearJoinableHeir(_id);
					
					//inform
					if(_announce) {
						Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_id, ChatColor.YELLOW +  heir + " has become the new leader of " + _id + "!");
					}
					
					//do not continue searching since we found an heir
					return;
				}
			}
		}
		
		//we must pick from the existing players
		//starting from the highest rank down
		{
			ArrayList<String> officers;
			
			//iterate through the officers from level 3 -> 1
			for(int i =3; i >= 1; i--) {
				officers = getJoinableOfficers(_id, i);
				
				//are there officers?
				if(officers.isEmpty())
					continue;//keep checking
				
				//set the player
				Empires.m_playerHandler.setPlayerRole(officers.get(0), Role.LEADER);
				
				//inform
				if(_announce) {
					Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_id, ChatColor.YELLOW +  officers.get(0) + " has become the new leader of " + _id + "!");
				}
				
				//FIX without returning
				//it will iterate continuously and add more than one leader?
				//if we don't ever return (no officers)
				//we will continue to add the first member.
				
				//do not search any more since we have an officer to promote
				return;
			}
			
			//we didn't find an officer..
			//gather the player list and find a new leader
			//gather the "one" to set
			String one = getJoinableJoinedPlayers(_id).get(0);
			//set as leader
			Empires.m_playerHandler.setPlayerRole(one, Role.LEADER);
			
			//inform
			if(_announce) {
				Empires.m_joinableHandler.invokeJoinableBroadcastToJoined(_id, ChatColor.YELLOW + one + " has become the new leader of " + _id + "!");
			}
		}
	}
	
	public ArrayList<String> getJoinableOfficers(String _id, int _rank) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return new ArrayList<String>();
		
		//rank restriction
		if(_rank > 3 || _rank < 1) {
			return null;
		}
		
		//gather what role we're talking about
		Role role;
		
		switch(_rank) {
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
			//somehow the check failed, just return the highest role
			role = Role.OFFICER_3;
			//better inform someone the check failed
			System.out.println("ERROR: getJoinableOfficers check FAILED");
			break;
		}
		
		ArrayList<String> officers = new ArrayList<String>();
		for(String player : getJoinableJoinedPlayers(_id)) {
			//is this player part of the officer group?
			if(Empires.m_playerHandler.getPlayerRole(player).equals(role)) {
				//add them to the list
				officers.add(player);
			}
		}
		
		return officers;
	}
	
	/**
	 * 
	 * @param _id
	 * @return returns null if no leader is found
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public String getJoinableLeader(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return null;
		
		for(String player : getJoinableJoinedPlayers(_id)) {
			if(Empires.m_playerHandler.getPlayerRole(player).equals(Role.LEADER)) {
				return player;
			}
		}
		
		return null;
	}
	
	public void invokeJoinableAddPlayer(String _id, String _name) throws EmpiresJoinableDoesNotExistException {
		//if the id we're looking up happens to be the default civilization (wilderness)
		//then we can't add a player
		if(_id.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lowercase for proper lookup
		_id = _id.toLowerCase();
		
		//tried to fetch a non-existent j
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch a non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		ArrayList<String> joinedPlayers = (ArrayList<String>)sect.getList("joined-players");
		
		if(!joinedPlayers.contains(_name)) {
			joinedPlayers.add(_name);
			sect.set("joined-players", joinedPlayers);
		}
	}
	
	/**
	 * Basic check to see if _id is valid
	 * is _id the default civilization?
	 * does _id exist?
	 * 
	 * @param _id - joinable in question
	 * @return returns true if the _id is the default civilization
	 * @throws EmpiresJoinableDoesNotExistException - joinable doesn't exist
	 */
	private boolean getCheck(String _id) throws EmpiresJoinableDoesNotExistException {
		//default name is just the default name
		if(_id.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return true;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup
		_id = _id.toLowerCase();
		
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch a non-existent joinable '" + _id + "'");
		
		return false;
	}
	
	public String getJoinableDisplayName(String _id) throws EmpiresJoinableDoesNotExistException {
		//default display name is just the default id
		if(getCheck(_id))
			return PlayerHandler.m_defaultCiv;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getString("d-name");
	}
	
	public String getJoinableDescription(String _id) throws EmpiresJoinableDoesNotExistException {
		//return the default id if we are searching for default information
		if(getCheck(_id))
			return PlayerHandler.m_defaultCiv;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getString("desc");
	}
	
	public void setJoinableName(String _id, String _name) throws EmpiresJoinableDoesNotExistException, EmpiresJoinableInvalidCharacterException {
		//do not allow wilderness shenanigans
		if(getCheck(_id))
			return;
		if(_name.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		if(_name.contains(":") || _name.contains(".") || _name.contains("&"))
			throw new EmpiresJoinableInvalidCharacterException("Attempted to create joinable with the name '" + _name + "'");
		
		
		//lower case for proper lookup
		_id = _id.toLowerCase();
		//_name lower case is done later so it can be saved as the display name
		
		//gather old section's data
		//There's probably a better way to do this but I couldn't find it
		//if the future programmer is reading this maybe spend a few hours
		//trying to find a way to just rename the configuration section?
		YamlConfiguration conf = getFileConfiguration();
		ConfigurationSection oldSect = conf.getConfigurationSection(_id);
		int power = oldSect.getInt("power");
		ArrayList<String> joinedPlayers = (ArrayList<String>) oldSect.getList("joined-players");
		ArrayList<String> requestedPlayers = (ArrayList<String>) oldSect.getList("requested-players");
		String desc = oldSect.getString("desc");
		String heir = oldSect.getString("heir");
		int claims = oldSect.getInt("claims");
		double bank = oldSect.getDouble("bank");
		String empire = oldSect.getString("empire");
		boolean isEmpire = oldSect.getBoolean("is-empire");
		ArrayList<String> requestedKingdoms = (ArrayList<String>) oldSect.getList("requested-kingdoms");
		ConfigurationSection home = oldSect.getConfigurationSection("home");
		ConfigurationSection relationWish = oldSect.getConfigurationSection("relation-wish");
		ConfigurationSection permissions = oldSect.getConfigurationSection("permissions");
		ConfigurationSection flags = oldSect.getConfigurationSection("flags");
		boolean ignoreRelations = oldSect.getBoolean("ignore-relations");
		
		//create a new section with _name and set the values from the last section
		ConfigurationSection newSect = conf.createSection(_name.toLowerCase());
		newSect.set("power", power);
		newSect.set("joined-players", joinedPlayers.clone());
		newSect.set("requested-players", requestedPlayers.clone());
		newSect.set("desc", desc);
		newSect.set("heir", heir);
		newSect.set("claims", claims);
		newSect.set("bank", bank);
		newSect.set("empire", empire);
		newSect.set("d-name", _name);//set to our new name
		newSect.set("is-empire", isEmpire);
		newSect.set("kingdoms", new ArrayList<String>());
		newSect.set("requested-kingdoms", requestedKingdoms.clone());
		
		ConfigurationSection homeSect = newSect.createSection("home");
		homeSect.set("x", home.get("x"));
		homeSect.set("y", home.get("y"));
		homeSect.set("z", home.get("z"));
		homeSect.set("w", home.getString("w"));
		
		//iterate through the sections that contain data.
		//must create a new instance of each value or we'll get issues serializing
		
		ConfigurationSection relSect = newSect.createSection("relation-wish");
		
		Map<String, Object> relMap = relationWish.getValues(true);
		for(String key : relMap.keySet()) {
			relSect.set(key, Relation.valueOf((String)relMap.get(key)).toString());
		}
		
		ConfigurationSection permSect = newSect.createSection("permissions");
		
		Map<String, Object> permMap = permissions.getValues(true);
		for(String key : permMap.keySet()) {
			permSect.set(key, ((ArrayList<String>)permMap.get(key)).clone());
		}
		
		ConfigurationSection flagSect = newSect.createSection("flags");
		
		Map<String, Object> flagMap = flags.getValues(true);
		for(String key : flagMap.keySet()) {
			flagSect.set(key, ((ArrayList<String>)flagMap.get(key)).clone());
		}
		
		//just a regular boolean. For boolean value territory flags
		newSect.set("ignore-relations", ignoreRelations);
		
		//now set _name to lower case for proper lookup
		_name = _name.toLowerCase();
		
		//change player pointers
		for(String player : joinedPlayers) {
			//overrides 'j' value to _name
			Empires.m_playerHandler.overridePlayerJoinedCivilization(player, _name);
		}
		
		//change empire (if any) name pointer
		if(!isEmpire && empire != null && !empire.isEmpty()) {
			//since we are not an empire we need to change our empire's list name
			
			//leave the empire with the old name
			Empires.m_joinableHandler.invokeKingdomSecedeEmpire(_id);
			
			//join it again under a different name
			try {
				Empires.m_joinableHandler.setJoinableEmpire(_name, empire);
			} catch (EmpiresJoinableIsNotEmpireException e) {
				e.printStackTrace();
			}
			
		} else if(isEmpire) {
			//since we are an empire we need to have our kingdoms update their names
			//and add our kingdoms from the old id to our new id
			//best way to do this in this framework is to just set their empire
			//to the new section
			
			//gather kingdoms from the old ID, current id has no kingdoms in it yet
			ArrayList<String> empireKingdoms = Empires.m_joinableHandler.getEmpireKingdomList(_id);
			try {
				for(String kingdom : empireKingdoms) {
					Empires.m_joinableHandler.setJoinableEmpire(kingdom, _name);
				}
			} catch(Exception e) {
					e.printStackTrace();//TODO send a message (could not set empire)
			}
		}
		
		//update other joinable's relationship names towards us
		Relation value;
		for(String other : conf.getKeys(false)) {
			//do not act on us or the old us
			if(other.equals(_id))
				continue;
			if(other.equals(_name))
				continue;
			
			//we have a relation set
			value = getJoinableRelationWish(other, _id);
			
			if(!value.equals(Relation.NEUTRAL)) {
				//set our new relation to us
				setJoinableRelationWish(other, _name, value);
				//remove the relation to our old self
				setJoinableRelationWish(other, _id, Relation.NEUTRAL);
			}
		}
		
		//update old territory to our new name
		Empires.m_boardHandler.renameAllTerritoryForJoinable(_id, _name);
		
		//remove old section
		conf.set(_id.toLowerCase(), null);
		
		//done
	}
	
	public void setJoinableDescription(String _id, String _desc) throws EmpiresJoinableDoesNotExistException {
		//can't work with the defuault civ
		if(getCheck(_id))
			return;
		
		//gather section
		YamlConfiguration conf = getFileConfiguration();
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		//set value
		sect.set("desc", _desc);
	}
	
	public void setJoinableHome(String _id, Location _loc) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		//gather section
		YamlConfiguration conf = getFileConfiguration();
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		ConfigurationSection homeSect = sect.getConfigurationSection("home");
		
		homeSect.set("x", _loc.getX());
		homeSect.set("y", _loc.getY());
		homeSect.set("z", _loc.getZ());
		homeSect.set("w", _loc.getWorld().getName());
	}
	
	public Location getJoinableHome(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return null;
		
		//gather section
		YamlConfiguration conf = getFileConfiguration();
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		ConfigurationSection homeSect = sect.getConfigurationSection("home");
		
		int x = homeSect.getInt("x");
		int y = homeSect.getInt("y");
		int z = homeSect.getInt("z");
		String w = homeSect.getString("w");
		
		//no REAL home set yet
		if(w == null)
			return null;
		
		//it's possible that the world has been deleted or disabled
		World world = Bukkit.getWorld(w);
		//so check to make sure the world exists
		if(world == null)
			return null;//if not return a null location
		
		return new Location(world, x, y, z);
	}
	
	public ArrayList<String> getJoinableJoinedPlayers(String _id) throws EmpiresJoinableDoesNotExistException {
		//return an empty list if we are searching for default information
		if(getCheck(_id))
			return new ArrayList<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return (ArrayList<String>)sect.getList("joined-players");
	}
	
	public boolean getJoinableEmpireStatus(String _id) throws EmpiresJoinableDoesNotExistException {
		//return false if we are searching for default information
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getBoolean("is-empire");
	}
	
	public double getJoinableBankBalance(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return 0.0;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getDouble("bank");
	}
	
	public String getKingdomJoinedEmpire(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return "";
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getString("empire");
	}
	
	public boolean getJoinableHasPermissionForRole(String _id, Role _role, GroupPermission _perm) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return false;
		
		//leaders have permission to do anything
		if(_role.equals(Role.LEADER))
			return true;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		ConfigurationSection permSect = sect.getConfigurationSection("permissions");
		
		return permSect.getList(_role.toString()).contains(_perm.toString());
	}
	
	public Set<String> getJoinableRelationNameSet(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return new HashSet<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		ConfigurationSection wishSect = sect.getConfigurationSection("relation-wish");
		
		return wishSect.getKeys(false);
	}
	
	public int getJoinableClaimSize(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return 0;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getInt("claims");
	}
	
	/**
	 * 
	 * @param _id
	 * @param _val
	 * @param _relative when true _val will be added to the current claim value
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public void setJoinableClaimSize(String _id, int _val, boolean _relative) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		//we are setting the value relative to the current value
		if(_relative)
			sect.set("claims", sect.getInt("claims") + _val);
		else//we are not setting the value relative to the current value, override old value
			sect.set("claims", _val);
	}
	
	/**
	 * Sets absolute claim size to _val
	 * @param _id
	 * @param _val
	 * @throws EmpiresJoinableDoesNotExistException 
	 */
	public void setJoinableClaimSize(String _id, int _val) throws EmpiresJoinableDoesNotExistException {
		setJoinableClaimSize(_id, _val, false);
	}
	
	public int getJoinablePowerValue(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return 0;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getInt("power");
	}
	
	public void setJoinablePowerValue(String _id, int _val, boolean _relative) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		if(_relative)//set the power relative to the old value
			sect.set("power", sect.getInt("power") + _val);
		else//set the power to absolute value _val
			sect.set("power", _val);
	}
	
	/**
	 * Sets power absolutely
	 * @param _id
	 * @param _val
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public void setJoinablePowerValue(String _id, int _val) throws EmpiresJoinableDoesNotExistException {
		setJoinablePowerValue(_id, _val, false);
	}
	
	/**
	 * 
	 * @param _id
	 * @return Returns empty string if no heir
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	public String getJoinableHeir(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return "";
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getString("heir");
	}
	
	public void setJoinableHeir(String _id, String _heir) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		//save as lower case just inCASE hah
		sect.set("heir", _heir.toLowerCase());
	}
	
	public void clearJoinableHeir(String _id) throws EmpiresJoinableDoesNotExistException {
		setJoinableHeir(_id, "");
	}
	
	public ArrayList<String> getEmpireKingdomList(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return new ArrayList<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return (ArrayList<String>) sect.getList("kingdoms");
	}
	
	public void setJoinableAsEmpire(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		sect.set("is-empire", true);
	}
	
	public void setJoinableRelationWish(String _id, String _other, Relation _wish) throws EmpiresJoinableDoesNotExistException {
		//cannot set relations to non-existent or default civ
		if(getCheck(_id))
			return;
		if(getCheck(_other))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//grab section
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		ConfigurationSection wishSect = sect.getConfigurationSection("relation-wish");
		
		//proper lookup
		_other = _other.toLowerCase();
		
		//set section value
		//since comparing relations will return neutral if no wish is given
		if(_wish.equals(Relation.NEUTRAL)) {
			if(wishSect.contains(_other)) {//if we have a wish towards them
				wishSect.set(_other, null);//we remove it
			}
		} else {
			//if it's not neutral we need to keep track of the wish
			wishSect.set(_other, _wish.toString());
		}
	}
	
	public void setJoinableEmpire(String _id, String _empire) throws EmpiresJoinableDoesNotExistException, EmpiresJoinableIsNotEmpireException {
		//cannot set relations to non-existent or default civ
		if(getCheck(_id))
			return;
		if(getCheck(_empire))
			return;
		
		if(!getJoinableEmpireStatus(_empire))
			throw new EmpiresJoinableIsNotEmpireException(_empire + " is not an empire");
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		sect.set("empire", _empire.toLowerCase());
		
		//set empire pointer to us as well
		sect = conf.getConfigurationSection(_empire.toLowerCase());
		{
			ArrayList<String> kingdoms = (ArrayList<String>)sect.getList("kingdoms");
			kingdoms.add(_id);
			sect.set("kingdoms", kingdoms);
		}
		
		//remove from requested
		{
			ArrayList<String> requested = (ArrayList<String>)sect.getList("requested-kingdoms");
			requested.remove(_id);
			sect.set("requested-kingdoms", requested);
		}
	}
	
	public void invokeKingdomSecedeEmpire(String _id) throws EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		_id = _id.toLowerCase();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		//set our pointer to nothing
		String empire = sect.getString("empire");
		sect.set("empire", "");
		
		//remove empire pointer to us as well
		sect = conf.getConfigurationSection(empire);
		{
			ArrayList<String> kingdoms = (ArrayList<String>)sect.getList("kingdoms");
			kingdoms.remove(_id);
			sect.set("kingdoms", kingdoms);
		}
	}
	
	public String getKingdomEmpire(String _id) throws EmpiresJoinableDoesNotExistException {
		//cannot set relations to non-existent or default civ
		if(getCheck(_id))
			return "";
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getString("empire");
	}
	
	public void invokeJoinableDisband(String _id) throws EmpiresJoinableDoesNotExistException {
		//cannot disband the default civ (wilderness)
		if(_id.equalsIgnoreCase(PlayerHandler.m_defaultCiv))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		
		//proper lookup
		_id = _id.toLowerCase();
		
		//doesn't exist?
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to disband non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		//gather empire status
		boolean isEmpire = sect.getBoolean("is-empire");
		
		//gather display name
		String displayName = sect.getString("d-name");
		
		//remove player pointers
		{
			//gather list of joined players
			CopyOnWriteArrayList<String> joinedPlayerList = new CopyOnWriteArrayList<String>(new HashSet<String>((List<String>)sect.getList("joined-players")));
			
			//for every player we remove their pointer and our reference
			for(String joinedPlayer : joinedPlayerList) {
				Empires.m_playerHandler.removePlayerFromJoinedCivilization(joinedPlayer);
			}
		}
		
		//remove relations from others
		{
			//iterate through all existing joinables
			Set<String> idSet = conf.getKeys(false);
			ConfigurationSection otherSect;
			ConfigurationSection relationSect;
			for(String otherId : idSet) {
				//gather section
				otherSect = conf.getConfigurationSection(otherId);
				relationSect = otherSect.getConfigurationSection("relation-wish");
				
				//if we exist in the section (there was a relation wish)
				if(relationSect.contains(_id)) {
					//remove
					relationSect.set(_id, null);
				}
			}
		}
		
		//remove empire information
		{
			//if we're an empire
			if(isEmpire) {
				//gather joined kingdoms
				ArrayList<String> joinedKingdomList = (ArrayList<String>) sect.getList("kingdoms");
				
				//iterate through the list of joined kingdoms
				ConfigurationSection otherSect;
				for(String joinedKingdom : joinedKingdomList) {
					if(!conf.isConfigurationSection(joinedKingdom))
						throw new EmpiresJoinableDoesNotExistException("Tried to remove empire pointer from the non-existent joinable '" + joinedKingdom + "'");
						
					//gather section
					otherSect = conf.getConfigurationSection(joinedKingdom);
					//remove their pointers to us
					otherSect.set("empire", "");//set to blank string. null would remove the section
				}
			} else {
				//we're a kingdom
				String empireName = sect.getString("empire");
				//check to make sure the empire exists in YML
				if(!conf.isConfigurationSection(empireName))
					throw new EmpiresJoinableDoesNotExistException("Tried to remove a reference from a non-existent empire '" + empireName +"' from '" + _id + "'");
				
				//gather section
				ConfigurationSection empireSect = conf.getConfigurationSection(empireName);
				
				//we HAVE an empire ! ""
				if(!empireName.equals("")) {
					//we remove the empire's reference to us
					//gather list
					ArrayList<String> joinedKingdomsList = (ArrayList<String>) empireSect.getList("kingdoms");
					
					//remove us
					joinedKingdomsList.remove(_id);
					
					//reset the list
					empireSect.set("kingdoms", joinedKingdomsList);
				}
			}
		}
		
		//remove our territory
		Empires.m_boardHandler.removeAllTerritoryForHost(_id);
		
		//remove us
		conf.set(_id, null);
		
		String civType = "kingdom";
		if(isEmpire) {
			civType = "empire";
		}
		Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "The " + civType + " '" + displayName + "' disbanded!");
	}

	public ArrayList<String> getJoinableList() throws EmpiresJoinableDoesNotExistException {
		ArrayList<String> nameList = new ArrayList<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		//iterate through ids
		for(String id : conf.getKeys(false)) {
			nameList.add(getJoinableDisplayName(id));
		}
		
		return nameList;
	}
	
	public void invokeJoinableRequestPlayer(String _id, String _name) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_id = _id.toLowerCase();
		
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		//add name
		ArrayList<String> requestList = (ArrayList<String>) sect.getList("requested-players");
		requestList.add(_name.toLowerCase());
		
		//save
		sect.set("requested-players", requestList);
	}
	
	public void invokeJoinableRemoveRequestedPlayer(String _id, String _name) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_id = _id.toLowerCase();
		
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		//add name
		ArrayList<String> requestList = (ArrayList<String>) sect.getList("requested-players");
		requestList.remove(_name.toLowerCase());
		
		//save
		sect.set("requested-players", requestList);
	}
	
	public boolean getJoinableRequestedPlayer(String _id, String _name) throws EmpiresJoinableDoesNotExistException {
		ArrayList<String> requestList = new ArrayList<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_id = _id.toLowerCase();
		
		//doesn't exist?
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		requestList = (ArrayList<String>) sect.getList("requested-players");
		
		return requestList.contains(_name.toLowerCase());
	}
	
	public boolean getJoinableIgnoresRelations(String _id) throws EmpiresJoinableDoesNotExistException {
		//return false if we are searching for default information
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getBoolean("ignore-relations");
	}
	
	public boolean getJoinableAllowsMobs(String _id) throws EmpiresJoinableDoesNotExistException {
		//return false if we are searching for default information
		if(getCheck(_id))
			return false;
		
		YamlConfiguration conf = getFileConfiguration();
		
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		return sect.getBoolean("spawn-mobs");
	}
	
	public boolean getEmpireRequestedKingdom(String _id, String _kingdom) throws EmpiresJoinableDoesNotExistException, EmpiresJoinableIsEmpireException {
		ArrayList<String> requestList = new ArrayList<String>();
		
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_kingdom = _kingdom.toLowerCase();
		_id = _id.toLowerCase();
		
		//kingdom exists?
		if(!getJoinableExists(_kingdom))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent kingdom '" + _kingdom + "'");
		
		//kingdom is kingdom?
		if(getJoinableEmpireStatus(_kingdom))
			throw new EmpiresJoinableIsEmpireException("Tried to invite empire " + _kingdom + " to empire");
		
		//doesn't exist?
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		requestList = (ArrayList<String>) sect.getList("requested-kingdoms");
		
		return requestList.contains(_kingdom);
	}
	
	public void invokeEmpireRequestKingdom(String _id, String _kingdom) throws EmpiresJoinableDoesNotExistException, EmpiresJoinableIsEmpireException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_kingdom = _kingdom.toLowerCase();
		_id = _id.toLowerCase();
		
		//kingdom exists?
		if(!getJoinableExists(_kingdom))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent kingdom '" + _kingdom + "'");
		
		//kingdom is kingdom?
		if(getJoinableEmpireStatus(_kingdom))
			throw new EmpiresJoinableIsEmpireException("Tried to invite empire " + _kingdom + " to empire");
		
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		//add name
		ArrayList<String> requestList = (ArrayList<String>) sect.getList("requested-kingdoms");
		requestList.add(_kingdom);
		
		//save
		sect.set("requested-kingdoms", requestList);
	}
	
	public void invokeEmpireRemoveRequestedKingdom(String _id, String _kingdom) throws EmpiresJoinableDoesNotExistException, EmpiresJoinableIsEmpireException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lookup
		_id = _id.toLowerCase();
		_kingdom = _kingdom.toLowerCase();
		
		//kingdom exists?
		if(!getJoinableExists(_kingdom))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent kingdom '" + _kingdom + "'");
		
		//kingdom is kingdom?
		if(getJoinableEmpireStatus(_kingdom))
			throw new EmpiresJoinableIsEmpireException("Tried to invite empire " + _kingdom + " to empire");
		
		if(!conf.isConfigurationSection(_id))
			throw new EmpiresJoinableDoesNotExistException("Tried to fetch non-existent joinable '" + _id + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_id);
		
		//add name
		ArrayList<String> requestList = (ArrayList<String>) sect.getList("requested-kingdoms");
		requestList.remove(_kingdom);
		
		//save
		sect.set("requested-kingdoms", requestList);
	}
	
	/**
	 * Deposits money in a relative fashion
	 * @param _id
	 * @param _invokerName
	 * @param _val
	 * @throws EmpiresNoFundsException _invokerName does not have enough funds
	 * @throws EmpiresJoinableDoesNotExistException _id does not exist
	 */
	public void invokeJoinableDepositMoney(String _id, String _invokerName, double _val) throws EmpiresNoFundsException, EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		if(Empires.m_economy.getBalance(_invokerName) >= _val) {
			Empires.m_economy.withdrawPlayer(_invokerName, _val);
			
			YamlConfiguration conf = getFileConfiguration();
			ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
			
			sect.set("bank", sect.getDouble("bank") + _val);
			
			return;
		}
		
		throw new EmpiresNoFundsException(_invokerName + " does not have the funds required to deposit " + _val);
	}
	
	public void invokeJoinableWithdrawMoney(String _id, String _invokerName, double _val) throws EmpiresNoFundsException, EmpiresJoinableDoesNotExistException {
		if(getCheck(_id))
			return;
		
		YamlConfiguration conf = getFileConfiguration();
		ConfigurationSection sect = conf.getConfigurationSection(_id.toLowerCase());
		
		double bankVal = sect.getDouble("bank");
		
		if(bankVal >= _val) {
			sect.set("bank", bankVal - _val);
			Empires.m_economy.depositPlayer(_invokerName, _val);
			
			return;
		}
		
		throw new EmpiresNoFundsException(_id + " does not have the funds required to deposit " + _val + " to " + _invokerName);
	}
}