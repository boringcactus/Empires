package com.pixelgriffin.empires.handler;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.enums.Role;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.util.IDUtility;
import com.pixelgriffin.empires.util.IOUtility;

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
	
	public void updateToUUIDs() {
		YamlConfiguration conf = getFileConfiguration();
		
		HashSet<String> players = (HashSet<String>)conf.getKeys(false);
		
		for(String name : players) {
			if(name.equals("data-version"))
				continue;
			
			//convert name to UUID
			UUID id = IDUtility.getUUIDForPlayer(name);
			
			if(id != null) {
				ConfigurationSection sect = conf.getConfigurationSection(name);
				
				//save data old data
				String j = sect.getString("j");
				int p = sect.getInt("p");
				String t = sect.getString("t");
				String r = sect.getString("r");
				long pt = sect.getLong("pt");
				boolean ac = sect.getBoolean("ac");
				int tpid = sect.getInt("tp-id");
				
				//clear old data
				conf.set(name, null);
				
				//create new data
				sect = conf.createSection(id.toString());
				sect.set("j", j);
				sect.set("p", p);
				sect.set("t", t);
				sect.set("r", r);
				sect.set("pt", pt);
				sect.set("ac", ac);
				sect.set("tp-id", tpid);
			} else {
				IOUtility.log("CRITICAL ERROR WHEN CONVERTING player.dat: null UUID for '" + name + "'");
			}
		}
	}
	
	/*
	 * Player specific loading
	 */
	
	public EmpiresPlayer getPlayer(UUID id) {
		String idString = id.toString();
		if(getFileConfiguration().isConfigurationSection(idString)) {
			return new EmpiresPlayer(Bukkit.getPlayer(id), getFileConfiguration().getConfigurationSection(idString));
		} else {
			return null;
		}
	}
	
	public boolean createPlayer(UUID id) {
		String idString = id.toString();
		if(getFileConfiguration().isConfigurationSection(idString))
			return false;
		
		ConfigurationSection sect = getFileConfiguration().createSection(idString);
		
		sect.set("j", m_defaultCiv);//set civilization name
		sect.set("p", m_defaultPower);//set power value
		sect.set("t", m_defaultTitle);//set title
		sect.set("r", m_defaultRole.toString());//set role
		sect.set("pt", System.currentTimeMillis());
		
		//set autoclaiming
		sect.set("ac", false);
		//set TPID
		sect.set("tp-id", -1);
		
		return true;
	}
	
	public void removeDormantPlayer(UUID id) {
		//remove us from any joinable we were in
		//invokeRemovePlayerFromJoinedJoinable(_id);
		EmpiresPlayer ep = getPlayer(id);
		if(ep == null)
			return;
		
		ep.leaveJoined();
		
		String idString = id.toString();
		getFileConfiguration().set(idString, null);
	}
	
	/**
	 * Not async safe
	 * @param _currentTime
	 */
	public void purgeDormantPlayers(long _currentTime) {
		YamlConfiguration conf = getFileConfiguration();
		
		for(String player : conf.getKeys(false)) {
			if(player.equals("data-version"))
				continue;
			
			if(conf.isConfigurationSection(player)) {
				long playTime = conf.getConfigurationSection(player).getLong("pt");
				long timeDiff = (System.currentTimeMillis() - playTime)/(86400000);
				
				if(timeDiff >= 20) {//20 days
					//they're inactive
					removeDormantPlayer(UUID.fromString(player));
				}
			}
		}
	}
}
