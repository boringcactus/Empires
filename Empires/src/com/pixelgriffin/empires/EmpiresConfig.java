package com.pixelgriffin.empires;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresConfig {
	
	/*
	 * Static members loaded from the m_configuration file are housed here
	 */
	
	public static ArrayList<String> m_blacklist = new ArrayList<String>();//done
	//public static ArrayList<String> m_teleportOkayList = new ArrayList<String>();
	public static ArrayList<String> m_whitelistPlaceableBlocks = new ArrayList<String>();
	public static ArrayList<String> m_whitelistBreakableBlocks = new ArrayList<String>();
	
	public static int m_maxTitleLength;
	public static int m_powerMax;//done
	public static int m_power2Max;//done
	public static boolean m_interworldClaim;//done
	public static int m_kingdomCost;//done
	public static int m_empireCost;//done
	public static boolean m_detachClaim;//done
	public static boolean m_deathPowerLoss;//done
	public static boolean m_moneyBack;//done
	//public static boolean m_interteleport;//done
	public static boolean m_damageReduc;//done
	public static boolean m_empireCreation;//done
	public static boolean m_kingdomCreation;//done
	public static boolean m_mobSpawnManaging;//done
	public static String m_defaultChatFormat;
	
	//dependency settings
	public static boolean m_useHerochat;
	public static boolean m_useVault;
	
	public static FileConfiguration m_config;
	
	@SuppressWarnings("unchecked")
	public static void loadConfiguration(Empires inst) {
		//m_config
		m_config = inst.getConfig();
		
		if(!m_config.contains("blacklist")) {
			m_config.set("blacklist", m_blacklist);
		} else {
			m_blacklist = (ArrayList<String>) m_config.getList("blacklist");
		}
		
		if(!m_config.contains("whitelistPlaceableBlocks")) {
			m_whitelistPlaceableBlocks.add(Material.TNT.toString());
			m_config.set("whitelistPlaceableBlocks", m_whitelistPlaceableBlocks);
		} else {
			m_whitelistPlaceableBlocks = (ArrayList<String>) m_config.getList("whitelistPlaceableBlocks");
		}
		
		if(!m_config.contains("whitelistBreakableBlocks")) {
			m_whitelistBreakableBlocks.add(Material.TNT.toString());
			m_config.set("whitelistBreakableBlocks", m_whitelistBreakableBlocks);
		} else {
			m_whitelistBreakableBlocks = (ArrayList<String>) m_config.getList("whitelistBreakableBlocks");
		}
		
		if(!m_config.contains("defaultChatFormat")) {
			m_defaultChatFormat = "{color}[{nick}�l�f {role}{joined}{title}{color}�f{sender}{color}] �f{msg}";
			m_config.set("defaultChatFormat", m_defaultChatFormat);
		} else {
			m_defaultChatFormat = m_config.getString("defaultChatFormat");
		}
		
		/*if(!m_config.contains("teleportOkayList")) {
			m_teleportOkayList.add(Bukkit.getWorlds().get(0).getName());
			m_config.set("teleportOkayList", m_teleportOkayList);
		} else {
			m_teleportOkayList = (ArrayList<String>) m_config.getList("teleportOkayList");
		}*/
		
		if(!m_config.contains("powerMax")) {
			m_config.set("powerMax", 15);
			m_powerMax = 15;
		} else {
			m_powerMax = m_config.getInt("powerMax");
		}
		
		if(!m_config.contains("power2Max")) {
			m_config.set("power2Max", 20);
			m_power2Max = 20;
		} else {
			m_power2Max = m_config.getInt("power2Max");
		}
		
		if(!m_config.contains("interworldClaim")) {
			m_config.set("interworldClaim", true);
			m_interworldClaim = true;
		} else {
			m_interworldClaim = m_config.getBoolean("interworldClaim");
		}
		
		if(!m_config.contains("kingdomCost")) {
			m_config.set("kingdomCost", 100);
			m_kingdomCost = 100;
		} else {
			m_kingdomCost = m_config.getInt("kingdomCost");
		}
		
		if(!m_config.contains("empireCost")) {
			m_config.set("empireCost", 200);
			m_empireCost = 200;
		} else {
			m_empireCost = m_config.getInt("empireCost");
		}
		
		if(!m_config.contains("maxTitleLength")) {
			m_config.set("maxTitleLength", 10);
			m_maxTitleLength = 10;
		} else {
			m_maxTitleLength = m_config.getInt("maxTitleLength");
		}
		
		if(!m_config.contains("detachClaim")) {
			m_config.set("detachClaim", false);
			m_detachClaim = false;
		} else {
			m_detachClaim = m_config.getBoolean("detachClaim");
		}
		
		if(!m_config.contains("deathPowerLoss")) {
			m_config.set("deathPowerLoss", true);
			m_deathPowerLoss = true;
		} else {
			m_deathPowerLoss = m_config.getBoolean("deathPowerLoss");
		}
		
		if(!m_config.contains("disbandMoneyBack")) {
			m_config.set("disbandMoneyBack", false);
			m_moneyBack = false;
		} else {
			m_moneyBack = m_config.getBoolean("disbandMoneyBack");
		}
		
		/*if(!m_config.contains("interteleport")) {
			m_config.set("interteleport", true);
			m_interteleport = true;
		} else {
			m_interteleport = m_config.getBoolean("interteleport");
		}*/
		
		if(!m_config.contains("damageReduction")) {
			m_config.set("damageReduction", true);
			m_damageReduc = true;
		} else {
			m_damageReduc = m_config.getBoolean("damageReduction");
		}
		
		if(!m_config.contains("empireCreation")) {
			m_config.set("empireCreation", true);
			m_empireCreation = true;
		} else {
			m_empireCreation = m_config.getBoolean("empireCreation");
		}
		
		if(!m_config.contains("kingdomCreation")) {
			m_config.set("kingdomCreation", true);
			m_kingdomCreation = true;
		} else {
			m_kingdomCreation = m_config.getBoolean("kingdomCreation");
		}
		
		if(!m_config.contains("mobSpawnFlag")) {
			m_config.set("mobSpawnFlag", true);
			m_mobSpawnManaging = true;
		} else {
			m_mobSpawnManaging = m_config.getBoolean("mobSpawnFlag");
		}
		
		//externals
		if(!m_config.contains("useVault")) {
			m_config.set("useVault", false);
			m_useVault = false;
		} else {
			m_useVault = m_config.getBoolean("useVault");
		}
		
		if(!m_config.contains("useHerochat")) {
			m_config.set("useHerochat", false);
			m_useHerochat = false;
		} else {
			m_useHerochat = m_config.getBoolean("useHerochat");
		}
		
		saveConfig();
	}	
	
	public static void saveBlacklist() {
		m_config.set("blacklist", m_blacklist);
	}
	
	public static void saveConfig() {
		try {
			m_config.save("plugins/Empires/config.yml");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
