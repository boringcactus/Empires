package com.pixelgriffin.empires.task;

import com.pixelgriffin.empires.Empires;

/**
 * 
 * @author Nathan
 *
 */
public class SaveTask implements Runnable {

	@Override
	public void run() {
		Empires.m_joinableHandler.saveFile();
		Empires.m_playerHandler.saveFile();
		Empires.m_boardHandler.saveFile();
	}
}
