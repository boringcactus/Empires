package com.pixelgriffin.empires.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.util.IOUtility;

/**
 * 
 * @author Nathan
 *
 */
public abstract class DataHandler {
	private final String m_fileDirectory = "plugins/Empires/";
	
	private final YamlConfiguration m_fileConfiguration;
	
	public DataHandler() {
		m_fileConfiguration = new YamlConfiguration();
	}
	
	/*
	 * Data updates / versions
	 */
	
	public int getDataVersion() {
		IOUtility.log("Data-version: " + getFileConfiguration().getInt("data-version"), ChatColor.AQUA);
		
		return getFileConfiguration().getInt("data-version");
	}
	
	public void updateToVersioning() {
		getFileConfiguration().set("data-version", 1);
	}
	
	/*
	 * Data IO
	 */
	
	protected YamlConfiguration getFileConfiguration() {
		return m_fileConfiguration;
	}
	
	protected String getFileDirectory() {
		return m_fileDirectory;
	}
	
	protected void createDirectory() {
		File f = new File(m_fileDirectory);
		
		if(!f.exists()) {
			f.mkdir();
		}
	}
	
	/**
	 * 
	 * @param conf - YamlConfiguration for this handler
	 * @param file - file of stored data, do not include directory path
	 */
	protected void loadConfigSafe(String file, JavaPlugin inst) {
		try {
			getFileConfiguration().load(getFileDirectory() + file);
		} catch (FileNotFoundException e) {
			File f = new File(getFileDirectory() + file);
			try {
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
			
			//Do not save the YML, if an error occurred
			//the YML would be empty and it would overwrite the old data.
			Empires.SAVE_FILE = false;
			
			System.out.println("An error occurred loading a data file. Disabling plugin");
			Bukkit.getPluginManager().disablePlugin(inst);
		}
	}
	
	
	protected void saveConfigSafe(String file) {
		try {
			getFileConfiguration().save(getFileDirectory() + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
