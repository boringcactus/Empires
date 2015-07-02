package com.pixelgriffin.empires;

import com.pixelgriffin.empires.util.IOUtility;

/**
 * 
 * Handles updating any outdated data files
 * 
 * @author Nathan
 *
 */
public class EmpiresDataUpdater {
	
	/**
	 * Checks to see if there are any outdated data files and updates them accordingly
	 * 
	 * 
	 */
	public void checkDataForUpdate() {
		IOUtility.log("Checking for updated data versions..");
		
		//read in versions
		int joinableVersion = Empires.m_joinableHandler.getDataVersion();
		int boardVersion = Empires.m_boardHandler.getDataVersion();
		int playerVersion = Empires.m_playerHandler.getDataVersion();
		
		if(joinableVersion != Empires.VERSION)
			updateJoinableFile(joinableVersion);
		if(boardVersion != Empires.VERSION)
			updateBoardFile(boardVersion);
		if(playerVersion != Empires.VERSION)
			updatePlayerFile(playerVersion);
		
		IOUtility.log("Everything is up to date!");
	}
	
	/**
	 * @param oldVersion The version of the data file
	 */
	private void updateJoinableFile(int oldVersion) {
		IOUtility.log("Updating joinable file...");
		
		//requires UUID & data versioning update
		//pay close attention here, in order to add a new update, we must check
		//if the oldVersion < VERSION, if it is, then we must invoke any
		//updates that the latest version involves. Do not change any
		//old version update methods, or change what is added in the if statement
		//for another older version update. This way, very old formats will
		//update sequentially and not get screwed up.
		if(oldVersion < 1) {
			Empires.m_joinableHandler.updateToUUIDs();
			Empires.m_joinableHandler.updateToVersioning();
		}
		
		/*
		 * Example addition:
		 * 
		 * if(oldVersion < 2) {
		 *      Empires.m_joinableHandler.updateToBinaryFormat();
		 * }
		 * 
		 */
		
		Empires.m_joinableHandler.saveFile();
	}
	
	private void updateBoardFile(int oldVersion) {
		IOUtility.log("Updating board file...");
		
		//requires data versioning update
		if(oldVersion < 1) {
			Empires.m_boardHandler.updateToVersioning();
		}
		
		Empires.m_boardHandler.saveFile();
	}
	
	private void updatePlayerFile(int oldVersion) {
		IOUtility.log("Updating player file...");
		
		//requires UUID & versioning update
		if(oldVersion < 1) {
			Empires.m_playerHandler.updateToUUIDs();
			Empires.m_playerHandler.updateToVersioning();
		}
		
		Empires.m_playerHandler.saveFile();
	}
}
