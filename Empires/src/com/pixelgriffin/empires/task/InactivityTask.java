package com.pixelgriffin.empires.task;

import com.pixelgriffin.empires.Empires;

/**
 * 
 * @author Nathan
 *
 */
public class InactivityTask implements Runnable {

	@Override
	public void run() {
		Empires.m_playerHandler.purgeDormantPlayers(System.currentTimeMillis());
	}
}
