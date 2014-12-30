package com.pixelgriffin.empires.enums;

/**
 * 
 * @author Nathan
 *
 */
public enum TerritoryFlag {
	ALLOW_BUILD(0, TerritoryGroup.MEMBER),//finished.
	ALLOW_PEARLS(1, TerritoryGroup.MEMBER),//finished.
	ALLOW_TRIPWIRE(2, TerritoryGroup.MEMBER),//finished.
	ALLOW_WOODPLT(3, TerritoryGroup.MEMBER),//finished.
	ALLOW_STONEPLT(4, TerritoryGroup.MEMBER),//finished.
	ALLOW_STONEBTN(5, TerritoryGroup.MEMBER),//finished.
	ALLOW_WOODBTN(6, TerritoryGroup.MEMBER),//finished.
	ALLOW_LEVER(7, TerritoryGroup.MEMBER),//finished.
	ALLOW_TRAPDOOR(8, TerritoryGroup.MEMBER),//finished.
	ALLOW_FENCEGATE(9, TerritoryGroup.MEMBER),//finished.
	ALLOW_DOOR(10, TerritoryGroup.MEMBER),//finished.
	ALLOW_DISPENSER(11, TerritoryGroup.MEMBER),//finished.
	ALLOW_NOTEBLOCK(12, TerritoryGroup.MEMBER),//finished.
	ALLOW_JUKEBOX(13, TerritoryGroup.MEMBER),//finished.
	ALLOW_ANVIL(14, TerritoryGroup.MEMBER),//finished.
	ALLOW_ENCHANT(15, TerritoryGroup.MEMBER),//finished.
	ALLOW_FURNACE(16, TerritoryGroup.MEMBER),//finished.
	ALLOW_CHEST(17, TerritoryGroup.MEMBER),//finished.
	ALLOW_CRAFTING(18, TerritoryGroup.MEMBER),//finished.
	ALLOW_BREWSTAND(19, TerritoryGroup.MEMBER);//finished.
	
	private int m_id;
	private TerritoryGroup m_up;
	
	/**
	 * 
	 * @param id
	 * @param _up - the bottom value that will receive the group by default
	 */
	TerritoryFlag(int id, TerritoryGroup _up) {
		this.m_id = id;
		this.m_up = _up;
		//if you're wondering what the deal with m_up is,
		//any Territory group with a value >= _up's id will receive this
		//territory flag by default. This is to save space in the yml
		//and reduce parsing times
	}
	
	public int getId() {
		return this.m_id;
	}
	
	public TerritoryGroup getBaseGroup() {
		return m_up;
	}
}
