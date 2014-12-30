package com.pixelgriffin.empires.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import com.pixelgriffin.empires.handler.BoardHandler;

/**
 * 
 * @author Nathan
 *
 * @deprecated
 * 
 * Used to migrate old file format.
 * Canned in favor of saving money & time.
 */
public class FileUtility {
	
	private static final String m_migrateBoardFile = "plugins/Empires/board.migrate";
	private static final String m_migrateJoinableFile = "plugins/Empires/joinable.migrate";
	private static final String m_migratePlayerFile = "plugins/Empires/players.migrate";
	
	/**
	 * Pretty easy, just changes IGNORE_RELATION settings and the way access is stored
	 */
	public static void migrateBoardFile() {
		File f = new File(m_migrateBoardFile);
		
		//file does not exist, can't migrate file
		if(!f.exists())
			return;
		
		YamlConfiguration oldConf = new YamlConfiguration();
		try {
			oldConf.load(m_migrateBoardFile);
		} catch(Exception e) {
			e.printStackTrace();
			
			return;
		}
		
		File f2 = new File("plugins/Empires/" + BoardHandler.m_file);
		
		//new board file doesn't exist..
		if(!f2.exists()) {
			try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				
				return;
			}
		}
		
		YamlConfiguration newConf = new YamlConfiguration();
		
		try {
			newConf.load("plugins/Empires/" + BoardHandler.m_file);
		} catch(Exception e) {
			e.printStackTrace();
			
			return;
		}
		
		ConfigurationSection workingSect;
		ConfigurationSection newSect;
		ConfigurationSection newFlagSect;
		ConfigurationSection oldFlagSect;
		String path;
		for(String key : oldConf.getKeys(true)) {
			if(oldConf.isConfigurationSection(key)) {
				workingSect = oldConf.getConfigurationSection(key);
				
				//is it territory?
				if(workingSect.contains("h")) {
					path = workingSect.getCurrentPath();
					
					newSect = newConf.createSection(path);
					newSect.set("h", workingSect.get("h"));
					newSect.set("a", new ArrayList<String>());
					newSect.set("ignore-relations", false);
					
					newFlagSect = newSect.createSection("f");
					oldFlagSect = workingSect.getConfigurationSection("f");
					
					ArrayList<String> flags;
					for(String flagKey : oldFlagSect.getKeys(false)) {
						flags = (ArrayList<String>)oldFlagSect.getList(flagKey);
						
						//?
						if(flags.contains("IGNORE_RELATION")) {
							flags.remove("IGNORE_RELATION");
							
							newSect.set("ingore-relations", true);
						}
						
						//set
						newFlagSect.set(flagKey, flags);
					}
				}
			}
		}
		
		try {
			newConf.save(f2);
			f.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads in the old JSON file and converts it to YML
	 */
	public static void migrateJoinableFile() {
		File f = new File(m_migrateJoinableFile);
		
		//file does not exist, can't migrate file
		if(!f.exists())
			return;
		
		try {
			BufferedReader br;
			
			br = new BufferedReader(new FileReader(f));
			
			Gson gson = new GsonBuilder().create();
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return;
		}
	}
	
	/**
	 * Reads in the old player YML and converts it to the new YML format
	 */
	public static void migratePlayerFile() {
		File f = new File(m_migratePlayerFile);
		
		//file does not exist, can't migrate file
		if(!f.exists())
			return;
		
		
	}
}
