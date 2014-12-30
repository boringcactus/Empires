package com.pixelgriffin.empires.enums;

/**
 * 
 * @author Nathan
 *
 */
public enum GroupPermission {
	
	/*
	 * Holds the role permission for a civilization
	 */
	
	//NAME(ID, DEFAULT_MEMBER, DEFAULT_OFFICER)
	RENAME(0, false, false),
	SET_DESC(1, false, false),
	KICK(2, false, true),
	PROMOTE(3, false, false),
	DEMOTE(4, false, false),
	DISBAND(5, false, false),
	WITHDRAW(6, false, false),
	RELATION(7, false, false),
	CLAIM(8, false, false),
	UNCLAIM(9, false, false),
	INVITE(10, false, true),
	SECEDE(11, false, false),
	REMOVE(12, false, false),
	ACCESS(13, false, false),
	PERMS(14, false, false),
	SET_HOME(15, false, false),
	SET_TITLE(16, false, true);
	
	GroupPermission(int _id, boolean _member, boolean _officer) {
		this.m_id = _id;
		this.m_member = _member;
		this.m_officer = _officer;
	}
	
	private int m_id;
	
	private boolean m_member, m_officer;
	
	public int getID() {
		return this.m_id;
	}
	
	public boolean getOfficerDefault() {
		return this.m_officer;
	}
	
	public boolean getMemberDefault() {
		return this.m_member;
	}
}
