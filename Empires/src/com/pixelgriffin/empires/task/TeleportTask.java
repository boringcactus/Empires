package com.pixelgriffin.empires.task;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pixelgriffin.empires.Empires;

/**
 * 
 * @author Nathan
 *
 */
public class TeleportTask implements Runnable {

	private Player m_who;
	private Location m_where;
	
	public TeleportTask(Player _who, Location _where) {
		m_who = _who;
		m_where = _where;
	}
	
	@Override
	public void run() {
		m_who.teleport(m_where);
		
		//clear tpid
		Empires.m_playerHandler.setPlayerTPID(m_who.getUniqueId(), -1);
	}

}
