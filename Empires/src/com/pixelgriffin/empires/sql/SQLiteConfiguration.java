package com.pixelgriffin.empires.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.util.IOUtility;

public abstract class SQLiteConfiguration {
	private final String databaseName = "Empires.db";
	
	protected String table;
	protected String format;
	
	public SQLiteConfiguration(String tableName, String dataFormat) {
		table = tableName;
		format = dataFormat;
		
		executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + "(" + dataFormat +");");
	}
	
	public Connection createConnection() {
		Connection conn = null;
		
		File dataFolder = new File(Empires.m_instance.getDataFolder(), databaseName);
		if(!dataFolder.exists()) {
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println("dir: " + dataFolder.getPath());
		IOUtility.log("CREATING SQLITE CONNECTION", ChatColor.YELLOW);
		
         try {
        	 Class.forName("org.sqlite.JDBC");
        	 
			 conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
		} catch (SQLException e) {
			//connection error
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//JDBC not supported
			e.printStackTrace();
		}
         
         return conn;
	}
	
	protected void executeUpdate(String statement) {
		try {
			Connection c = createConnection();
			if(c != null) {
				IOUtility.log("EXECUTING STATEMENT", ChatColor.YELLOW);
				Statement state = c.createStatement();
				state.executeUpdate(statement);
				state.close();
				c.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
