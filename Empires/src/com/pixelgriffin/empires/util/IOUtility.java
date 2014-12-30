package com.pixelgriffin.empires.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * 
 * @author Nathan
 *
 */
public class IOUtility {
	public static void log(String _msg) {
		System.out.println("[Empires] " + _msg);
	}
	
	public static void log(String _msg, ChatColor _color) {
		Bukkit.getServer().getConsoleSender().sendMessage("[Empires] " + _color + _msg);
	}
}
