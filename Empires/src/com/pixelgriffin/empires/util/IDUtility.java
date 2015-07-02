package com.pixelgriffin.empires.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class IDUtility {
	public static UUID getUUIDForPlayer(String name) {
		UUID id = null;
		
		OfflinePlayer player = null;
		player = Bukkit.getPlayer(name);
		if(player == null)
			player = Bukkit.getOfflinePlayer(name);
		
		if(player != null)
			id = player.getUniqueId();
		
		return id;
	}
}
