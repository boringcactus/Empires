package com.pixelgriffin.empires.sql;

import java.util.logging.Level;

import com.pixelgriffin.empires.Empires;

public class Error {
    public static void execute(Empires plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);     
    }
    public static void close(Empires plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}