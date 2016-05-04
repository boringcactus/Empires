package com.pixelgriffin.empires.command.sub;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pixelgriffin.empires.Empires;
import com.pixelgriffin.empires.command.SubCommand;
import com.pixelgriffin.empires.exception.EmpiresJoinableDoesNotExistException;
import com.pixelgriffin.empires.handler.Joinable;

/**
 * 
 * @author Nathan
 *
 */
public class SubCommandList extends SubCommand {

	@Override
	public boolean run(CommandSender _sender, String[] _args) {
		
		TreeMap<String, Integer> empiresMap = new TreeMap<String, Integer>();
		TreeMap<String, Integer> kingdomsMap = new TreeMap<String, Integer>();
		
		{
			//get display name list of all joinables
			//ArrayList<String> nameList = Empires.m_joinableHandler.getJoinableList();
			ArrayList<Joinable> all = Empires.m_joinableHandler.getAllJoinables();
			
			int powerValue;
			//for(String name : nameList) {
			for(Joinable j : all) {
				//gather empire status
				//isEmpire = Empires.m_joinableHandler.getJoinableEmpireStatus(name);
				
				//powerValue = Empires.m_joinableHandler.getJoinablePowerValue(name);
				powerValue = j.getPower();
				
				if(j.isEmpire()) {
					empiresMap.put(j.getDisplayName(), powerValue);
				} else {
					kingdomsMap.put(j.getDisplayName(), powerValue);
				}
			}
		}
		
		int totalPages = (empiresMap.keySet().size() + kingdomsMap.keySet().size()) / 10;
		totalPages += 1;
		
		_sender.sendMessage(ChatColor.AQUA + "___[Civilization List 1/"+totalPages+"]___");
		
		//TODO: add pages?
		//page counts
		/*int page = 1;
		int printCount = 1;
		
		if(_args.length == 1) {
			try {
				page = Integer.valueOf(_args[0]);
			} catch(Exception e) {
				//don't do anything, page is already at 1
			}
		}*/
		
		_sender.sendMessage(ChatColor.GOLD + "________[Empires]________");
		
		//print empires
		int fameCount = 1;
		for(Entry<String, Integer> entry : sortedMapByValues(empiresMap)) {
			_sender.sendMessage(ChatColor.GOLD + "#" + fameCount + " " + entry.getKey() + " (" + entry.getValue() + ")");
			fameCount++;
		}
		
		_sender.sendMessage(ChatColor.GRAY + "________[Kingdoms]_______");
		
		//print kingdoms
		for(Entry<String, Integer> entry : sortedMapByValues(kingdomsMap)) {
			_sender.sendMessage(ChatColor.GRAY + "#" + fameCount + " " + entry.getKey() + " (" + entry.getValue() + ")");
			fameCount++;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @author polygenelubricants, savagesun
	 * @param _map map to be sorted
	 * @return sorted set
	 */
	private <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortedMapByValues(Map<K, V> _map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {

					@Override
					public int compare(Entry<K, V> arg0, Entry<K, V> arg1) {
						//keeps values from overwriting eachother
						if(arg1.getValue().equals(arg0.getValue())) {
							return -1;
						}
						
						return arg1.getValue().compareTo(arg0.getValue());
					}
					
				}
			);
		
		sortedEntries.addAll(_map.entrySet());
		return sortedEntries;
	}
}
