package com.pixelgriffin.empires.chat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dthielke.Herochat;
import com.dthielke.api.Channel;
import com.dthielke.api.ChannelStorage;
import com.dthielke.api.Chatter;
import com.dthielke.api.MessageFormatSupplier;
import com.dthielke.api.TagFormatter;

public class KingdomChat implements Channel {

	//private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
	//private final ChannelStorage chStore = Herochat.getChannelManager().getStorage();
	//private final MessageFormatSupplier fmt = Herochat.getChannelManager();
	
	
	@Override
	public String formatTag(String arg0, Player arg1, Channel arg2) {
		return "";
	}

	@Override
	public boolean addTag(String arg0, TagFormatter arg1) {
		return false;
	}

	@Override
	public void announce(String arg0) {
	}

	@Override
	public boolean banMember(Chatter arg0, boolean arg1) {
		return false;
	}

	@Override
	public Set<String> getBans() {
		return Collections.emptySet();
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GOLD;
	}

	@Override
	public double getCost() {
		return 0;
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public String getFormat() {
		return "{color}[{nick}§l§f {role}{joined}{title}{color}§f{prefix}{sender}{color}] §f{msg}";
	}

	@Override
	public MessageFormatSupplier getFormatSupplier() {
		return Herochat.getChannelManager();
	}

	@Override
	public Set<String> getModerators() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getMutes() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return "Kingdom";
	}

	@Override
	public String getNick() {
		return "K";
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public Set<String> getWorlds() {
		Set<String> ret = new HashSet<String>();
		for(World w : Bukkit.getWorlds()) {
			ret.add(w.getName());
		}
		
		return ret;
	}

	@Override
	public boolean hasWorld(String arg0) {
		return true;
	}

	@Override
	public boolean hasWorld(World arg0) {
		return true;
	}

	@Override
	public boolean isBanned(String arg0) {
		return false;
	}

	@Override
	public boolean isCrossServer() {
		return false;
	}

	@Override
	public boolean isCrossWorld() {
		return true;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isModerator(String arg0) {
		return false;
	}

	@Override
	public boolean isMuted() {
		return false;
	}

	@Override
	public boolean isMuted(String arg0) {
		return false;
	}

	@Override
	public boolean isShortcutAllowed() {
		return true;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public boolean isVerbose() {
		return false;
	}

	@Override
	public boolean kickMember(Chatter arg0, boolean arg1) {
		return false;
	}

	@Override
	public void onMemberJoin(boolean arg0, String arg1) {
		
	}

	@Override
	public void onMemberLeave(boolean arg0, String arg1) {
		
	}

	@Override
	public void removeWorld(String arg0, boolean arg1) {
		
	}

	@Override
	public void sendRawMessage(String arg0) {
		
	}

	@Override
	public void setBanned(String arg0, boolean arg1, boolean arg2) {
		
	}

	@Override
	public void setBans(Collection<String> arg0, boolean arg1) {
		
	}

	@Override
	public void setColor(ChatColor arg0, boolean arg1) {
		
	}

	@Override
	public void setCost(double arg0, boolean arg1) {
		
	}

	@Override
	public void setCrossServer(boolean arg0, boolean arg1) {
		
	}

	@Override
	public void setCrossWorld(boolean arg0, boolean arg1) {
		
	}

	@Override
	public void setDistance(int arg0, boolean arg1) {
		
	}

	@Override
	public void setFormat(String arg0, boolean arg1) {
	}

	@Override
	public void setModerator(String arg0, boolean arg1, boolean arg2) {
		
	}

	@Override
	public void setModerators(Collection<String> arg0, boolean arg1) {
	}

	@Override
	public void setMuted(boolean arg0, boolean arg1) {
	}

	@Override
	public void setMuted(String arg0, boolean arg1, boolean arg2) {
	}

	@Override
	public void setMutes(Collection<String> arg0, boolean arg1) {
	}

	@Override
	public void setNick(String arg0, boolean arg1) {
		
	}

	@Override
	public void setPassword(String arg0, boolean arg1) {
		
	}

	@Override
	public void setShortcutAllowed(boolean arg0, boolean arg1) {
		
	}

	@Override
	public void setVerbose(boolean arg0, boolean arg1) {
		
	}

	@Override
	public void setWorlds(Collection<String> arg0, boolean arg1) {
		
	}
	
}
