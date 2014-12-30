package com.pixelgriffin.empires.enums;

import org.bukkit.ChatColor;

/**
 * 
 * @author Nathan
 *
 */
public enum Relation {
	
	/*
	 * Holds possible relationships between two joinables
	 */
	
	NEUTRAL(1, ChatColor.GRAY),
	ALLY(0, ChatColor.DARK_PURPLE),
	ENEMY(2, ChatColor.RED),
	US(3, ChatColor.GREEN),
	E_K(4, ChatColor.GOLD);
	
	private int id;
	private ChatColor color;
	
	Relation(int id, ChatColor color) {
		this.id = id;
		this.color = color;
	}
	
	public boolean isEqual(Relation other) {
		
		if(this.id != other.id) {
			return false;
		}
		return true;
	}
	
	public int getIntValue() {
		return this.id;
	}
	
	public void setIntValue(int val) {
		this.id = val;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public void set(Relation r) {
		this.id = r.getIntValue();
	}
}
