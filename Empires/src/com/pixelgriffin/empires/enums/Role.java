package com.pixelgriffin.empires.enums;

/**
 * 
 * @author Nathan
 *
 */
public enum Role {
	
	/*
	 * Numbers determined by their power in a civilization
	 */
	
	MEMBER(1, ""),
	OFFICER_1(2, "★"),
	OFFICER_2(3, "★★"),
	OFFICER_3(4, "★★★"),
	LEADER(5, "^");
	
	private int value;
	private String prefix;
	
	Role(int val, String prefix) {
		this.value = val;
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public int getIntValue() {
		//used to calculate command importance/priority
		return this.value;
	}
	
	public void setIntValue(int val) {
		this.value = val;
	}
	
	public void set(Role r) {
		this.value = r.getIntValue();
	}
	
	public static Role getRoleFromInt(int _val) {
		for(Role r : Role.values()) {
			if(r.getIntValue() == _val)
				return r;
		}
		
		return null;
	}
}
