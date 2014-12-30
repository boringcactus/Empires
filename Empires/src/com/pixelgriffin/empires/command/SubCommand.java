package com.pixelgriffin.empires.command;

import org.bukkit.command.CommandSender;

/**
 * 
 * @author Nathan
 *
 */
public abstract class SubCommand {
	private String m_error;//error to be printed later if the sub command returns flase (fails)
	
	public SubCommand() {
		this.m_error = "void";
	}
	
	public String getError() {
		return m_error;
	}
	
	public void setError(String _err) {
		this.m_error = _err;
	}
	
	public abstract boolean run(CommandSender _sender, String[] _args);
}
