package com.pixelgriffin.empires.handler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.exception.EmpiresJoinableExistsException;
import com.pixelgriffin.empires.exception.EmpiresPlayerExistsException;

/**
 * 
 * @author Nathan
 *
 */
public class PlayerHandler extends DataHandler {
	
	//the player file without plugin data folder directory
	private final String m_file = "players.dat";
	
	//the default player generation values
	public static final String m_defaultCiv = "wilderness";
	public static final String m_defaultTitle = "";
	public static final Role m_defaultRole = Role.MEMBER;
	public static final int m_defaultPower = 0;
	
	public PlayerHandler() {
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
	 * Player specific loading
	 */
	
	/**
	 * "Instantiates" a new player in YML
	 * @param _name
	 */
	private void invokeCreatePlayerSpace(String _name) throws EmpiresPlayerExistsException {
		YamlConfiguration conf = getFileConfiguration();
		
		//a player with this name already exists in the YML, creating new space is unadvisable
		if(conf.isConfigurationSection(_name))
			throw new EmpiresPlayerExistsException("A player with the name " + _name + " found to exist when attemting to create new space");
		
		//create a default player space
		ConfigurationSection sect = conf.createSection(_name.toLowerCase());
		
		sect.set("j", m_defaultCiv);//set civilization name
		sect.set("p", m_defaultPower);//set power value
		sect.set("t", m_defaultTitle);//set title
		sect.set("r", m_defaultRole.toString());//set role
		sect.set("pt", System.currentTimeMillis());
		
		//set autoclaiming
		sect.set("ac", false);
		//set TPID
		sect.set("tp-id", -1);
	}
	
	//methods already create a player if he is found to be non-existent
	//however when you don't want to create a player on accident use this
	public boolean getPlayerExists(String _name) {
		return getFileConfiguration().isConfigurationSection(_name.toLowerCase());
	}
	
	public Role getPlayerRole(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return m_defaultRole;
		} else {
			ConfigurationSection sect = conf.getConfigurationSection(_name);
			
			try {
				return Role.valueOf(sect.getString("r"));
			} catch(Exception e) {//corrupted role value found
				e.printStackTrace();
				return Role.MEMBER;
			}
		}
	}
	
	/**
	 * Gather player's joined civilization name
	 * @param _name - the name of the player in question
	 * @return the name of the civilization the player is joined with (m_defaultCiv if none)
	 */
	public String getPlayerJoinedCivilization(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//if this player does not exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return m_defaultCiv;//will have set the civ to this by default creation
		} else {
			//he does exist, return his joinable
			return conf.getConfigurationSection(_name).getString("j");//grab the j (joinable) YML stored string
		}
	}
	
	public String getPlayerTitle(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//if this player does not exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return m_defaultTitle;//return default just set
		} else {
			//he does exist, return his joinable
			return conf.getConfigurationSection(_name).getString("t");//grab the j (joinable) YML stored string
		}
	}
	
	public int getPlayerPower(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		//player doesn't exist??
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return m_defaultPower;//return default just set
		} else {
			//he exists
			return conf.getConfigurationSection(_name).getInt("p");
		}
	}
	
	public boolean getPlayerAutoClaiming(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		//player doesn't exist??
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return false;//return default just set
		} else {
			//he exists
			return conf.getConfigurationSection(_name).getBoolean("ac");
		}
	}
	
	public void setPlayerAutoClaiming(String _name, boolean _val) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);

		//finally set our auto claiming value
		sect.set("ac", _val);
	}
	
	public void setPlayerTPID(String _name, int _val) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);

		//finally set our auto claiming value
		sect.set("tp-id", _val);
	}
	
	public int getPlayerTPID(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		//player doesn't exist??
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return -1;//return default just set
		} else {
			//he exists
			return conf.getConfigurationSection(_name).getInt("tp-id");
		}
	}
	
	/**
	 * Removes a player from a joined civilization. Reset both the player's pointer and the joinable's reference
	 * @param _name
	 * @throws EmpiresJoinableDoesNotExistException
	 */
	@SuppressWarnings("deprecation")
	public void invokeRemovePlayerFromJoinedJoinable(String _name) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
			
			//no reason to do anything in here since he had no joinable set to begin with
			return;
		}
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		String joined = sect.getString("j");
		
		
		//we cannot remove ourselves from the default civ (wilderness)
		if(joined.equals(m_defaultCiv))
			return;
		
		//since we are in a joinable and we exist..
		//we will change our pointer to the default (wilderness)
		sect.set("j", m_defaultCiv);
		
		//and remove ourselves from the previously joined player list
		Empires.m_joinableHandler.invokeJoinableRemovePlayer(joined, _name);
		
		//gather the old role
		Role oldRole = Role.valueOf(sect.getString("r").toUpperCase());
		
		//reset our role as well
		sect.set("r", m_defaultRole.toString());
		//reset our title
		sect.set("t", m_defaultTitle);
		
		//if we haven't disbanded then do some extra stuff
		if(!Empires.m_joinableHandler.getJoinableExists(joined))
			return;
		
		//now that we're out of the previously joined civilization
		//remove our power value
		//gather
		int powerValue = getPlayerPower(_name);
		
		//remove
		Empires.m_joinableHandler.setJoinablePowerValue(joined, -powerValue, true);
		
		//was the removed player the leader?
		if(oldRole.equals(Role.LEADER)) {
			//find a new leader.
			try {
				Empires.m_joinableHandler.invokeJoinableFindNewLeader(joined, true);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();//shouldn't happen.. but you never know!
			}
		}
	}
	
	/**
	 * Adds a player to a civilization.
	 * @param _name Player name
	 * @param _id Joinable id
	 * @throws EmpiresJoinableExistsException the player in question already has a joinable set
	 * @throws EmpiresJoinableDoesNotExistException the joinable in question does not exists
	 */
	public void setPlayerJoinedCivlization(String _name, String _id) throws EmpiresJoinableExistsException, EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		if(!sect.getString("j").equals(m_defaultCiv))
			throw new EmpiresJoinableExistsException("The player '" + _name + "' already has a joinable set. Use removePlayerFromJoinedCivilization to remove him from one");
		
		//since we don't have a joinable and we exist
		//we set our pointer to the joinable in question
		sect.set("j", _id);
		//and add ourselves to the newly joined player list
		Empires.m_joinableHandler.invokeJoinableAddPlayer(_id, _name);
		
		//now that we've added the player, change the power value yo
		//gather power value
		int powerValue = getPlayerPower(_name);
		
		//add the power!
		Empires.m_joinableHandler.setJoinablePowerValue(_id, powerValue, true);
	}
	
	public void setPlayerRole(String _name, Role _role) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//in the case that someone tries to change the name of nobody..?
		if(_name == null)
			return;
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		//make sure we're in a joinable so we can set the role
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		
		if(sect.getString("j").equals(m_defaultCiv))
			throw new EmpiresJoinableDoesNotExistException("Attempted to set a role for " + _name + " who is not involved with a joinable");
		
		//finally set our role
		sect.set("r", _role.toString());
	}
	
	public void setPlayerTitle(String _name, String _title) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		//make sure we're in a joinable so we can set the role
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		if(sect.getString("j").equals(m_defaultCiv))
			throw new EmpiresJoinableDoesNotExistException("Attempted to set a role for " + _name + " who is not involved with a joinable");
		
		//finally set our role
		sect.set("t", _title);
	}
	
	/**
	 * Changes player's power and absolutely affects the player's joinable
	 * @param _name
	 * @param _val
	 */
	public void setPlayerPower(String _name, int _val) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		//gather sect
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		
		//gather old power
		int oldPower = sect.getInt("p");
		
		//set our new power
		sect.set("p", _val);
		
		//update our joined total power
		//gather joined
		String joined = sect.getString("j");
		
		//do not act on wilderness
		if(joined.equals(m_defaultCiv))
			return;
		
		try {
			//change the power relatively based on the old value and the new one
			//for example: we're at 10 we die so we set our power to 0
			//the difference is -10 and therefore 10 power is removed from the joinable
			Empires.m_joinableHandler.setJoinablePowerValue(joined, _val - oldPower, true);//for our joinable
		} catch (EmpiresJoinableDoesNotExistException e) {
			e.printStackTrace();//something went wrong! no joinable was found.. boohoo
		}
	}

	public void overridePlayerJoinedCivilization(String _player, String _name) throws EmpiresJoinableDoesNotExistException {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		_player = _player.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_player)) {
			try {
				invokeCreatePlayerSpace(_player);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		if(!Empires.m_joinableHandler.getJoinableExists(_name))
			throw new EmpiresJoinableDoesNotExistException("Tried to override player civilization to a non-existent joinable '" + _name + "'");
		
		ConfigurationSection sect = conf.getConfigurationSection(_player);
		sect.set("j", _name);
	}
	
	public void setPlayerLastPlayedTime(String _name, long _time) {
		YamlConfiguration conf = getFileConfiguration();
		
		//lower case the name for proper lookup
		_name = _name.toLowerCase();
		
		//player doesn't exist in YML
		if(!conf.isConfigurationSection(_name)) {
			try {
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection sect = conf.getConfigurationSection(_name);
		
		sect.set("pt", _time);
	}
	
	public long getPlayerLastPlayedTime(String _name) {
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		//player doesn't exist??
		if(!conf.isConfigurationSection(_name)) {
			try {
				//attempt to create him
				invokeCreatePlayerSpace(_name);
			} catch (EmpiresPlayerExistsException e) {//unlikely - but that's programming!
				e.printStackTrace();
			}
			
			return System.currentTimeMillis();//return default just set
		} else {
			//he exists
			return conf.getConfigurationSection(_name).getLong("pt");
		}
	}
	
	public void removeDormantPlayer(String _name) throws EmpiresJoinableDoesNotExistException {
		//remove us from any joinable we were in
		invokeRemovePlayerFromJoinedJoinable(_name);
		
		//remove our data
		YamlConfiguration conf = getFileConfiguration();
		
		_name = _name.toLowerCase();
		
		conf.set(_name, null);
	}
	
	/**
	 * Not async safe
	 * @param _currentTime
	 */
	public void purgeDormantPlayers(long _currentTime) {
		YamlConfiguration conf = getFileConfiguration();
		
		for(String player : conf.getKeys(false)) {
			if(conf.isConfigurationSection(player)) {
				long playTime = getPlayerLastPlayedTime(player);
				long timeDiff = (System.currentTimeMillis() - playTime)/(86400000);
				
				if(timeDiff >= 20) {//20 days
					//they're inactive
					try {
						removeDormantPlayer(player);
					} catch (EmpiresJoinableDoesNotExistException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}