package com.pixelgriffin.empires.enums;

/**
 * 
 * @author Nathan
 *
 */
public enum TerritoryGroup {
	
	/*
	 * Used to find a grouping to check for territory flags.
	 * Basically a merged Role and Relation enumeration.
	 * Numbers are ambiguous.
	 */
	
	NEUTRAL(0),
	ENEMY(1),
	ALLY(2),
	MEMBER(3),
	OFFICER_1(4),
	OFFICER_2(5),
	OFFICER_3(6),
	LEADER(7);
	
	private int id;
	
	TerritoryGroup(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static TerritoryGroup fromRole(Role myRole) {
		if(myRole.equals(Role.MEMBER)) {
			return TerritoryGroup.MEMBER;
		} else if(myRole.equals(Role.OFFICER_1)) {
			return TerritoryGroup.OFFICER_1;
		} else if(myRole.equals(Role.OFFICER_2)) {
			return TerritoryGroup.OFFICER_2;
		} else if(myRole.equals(Role.OFFICER_3)) {
			return TerritoryGroup.OFFICER_3;
		} else if(myRole.equals(Role.LEADER)) {
			return TerritoryGroup.LEADER;
		}
		
		return null;
	}
}
