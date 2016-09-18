package com.pixelgriffin.empires.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.pixelgriffin.empires.enums.Role;

public class SQLitePlayerConfiguration extends SQLiteConfiguration {
	
	protected String UUIDString;
	
	public SQLitePlayerConfiguration(UUID id) {
		super("emp_player_data", "'id' varchar(32) NOT NULL, 'j' varchar(32) NOT NULL, 'p' int(10) NOT NULL, 't' varchar(32) NOT NULL, 'r' varchar(32) NOT NULL, 'pt' int(11) NOT NULL, 'ac' int(5) NOT NULL, 'tpid' int(11) NOT NULL, PRIMARY KEY('id')");
		
		if(id != null) {
			UUIDString = id.toString();
		}
	}
	
	/*public void loadData() throws SQLException {
		if(UUIDString != null) {
			Connection c = createConnection();
			if(c == null) {
				throw new SQLException("Connection could not be created");
			}
			
			PreparedStatement s = c.prepareStatement("SELECT * FROM emp_player_data WHERE id='" + UUIDString + "';");
			ResultSet res = s.executeQuery();
			
			while(res.next()) {
				if(res.getString("id").equals(UUIDString)) {
					j = res.getString("j");
					p = res.getInt("p");
					t = res.getString("t");
					r = res.getString("r");
					pt = res.getLong("pt");
					ac = res.getBoolean("ac");
					tpid = res.getInt("tpid");
				}
			}
			
			res.close();
			s.close();
			c.close();
		} else {
			throw new SQLException("Null ID used to lookup data");
		}
	}*/
	
	
	/*public void testWrite() {
		Connection c = createConnection();
		if(c == null)
			return;
		
		try {
			//PreparedStatement s = c.prepareStatement("INSERT INTO " + table + " (id, j, p, t, r, pt, ac, tpid) " + 
			//										 "VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
			PreparedStatement s = c.prepareStatement("INSERT INTO " + table + " (id, j, p, t, r, pt, ac, tpid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			
			s.setString(1, "123abc-456789-1028356");
			s.setString(2, "Dicksquad");
			s.setInt(3, 0);
			s.setString(4, "Swaglord");
			s.setString(5, Role.LEADER.toString());
			s.setLong(6, 1049594L);
			s.setBoolean(7, false);
			s.setLong(8, 105487L);
			
			s.executeUpdate();
			
			s.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	
}
