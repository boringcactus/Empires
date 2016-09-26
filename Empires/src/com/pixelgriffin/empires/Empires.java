package com.pixelgriffin.empires;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelgriffin.empires.chat.EmpiresListenerChat;
import com.pixelgriffin.empires.chat.old.EmpiresListenerChatLegacy;
import com.pixelgriffin.empires.command.EmpiresCommands;
import com.pixelgriffin.empires.handler.BoardHandler;
import com.pixelgriffin.empires.handler.JoinableHandler;
import com.pixelgriffin.empires.handler.PlayerHandler;
import com.pixelgriffin.empires.listener.EmpiresListenerPlayerGeneral;
import com.pixelgriffin.empires.listener.EmpiresListenerPlayerRestriction;
import com.pixelgriffin.empires.task.InactivityTask;
import com.pixelgriffin.empires.task.PowerUpdateTask;
import com.pixelgriffin.empires.task.SaveTask;
import com.pixelgriffin.empires.util.IOUtility;

/**
 * 
 * @author Nathan
 *
 */
public class Empires extends JavaPlugin {
	
	public static final int VERSION = 1;
	public static boolean SAVE_FILE = true;
	
	public static final JoinableHandler m_joinableHandler = new JoinableHandler();
	public static final PlayerHandler m_playerHandler = new PlayerHandler();
	public static final BoardHandler m_boardHandler = new BoardHandler();
	
	public static Economy m_economy;
	
	public static boolean m_vaultActive = false;
	public static boolean m_herochatActive = false;
	public static boolean m_tagAPIActive = false;
	
	public static Empires m_instance;
	
	private EmpiresCommands m_commandExecutor;
	private EmpiresAPI m_api;
	
	@Override
	public void onEnable() {
		//file migration would go here, but it's cancelled
		//just a heads up to future programmers
		
		//load the config
		EmpiresConfig.loadConfiguration(this);
		
		/*
		 * External dependencies
		 */
		PluginManager pManager = Bukkit.getPluginManager();
		
		//Vault
		if(EmpiresConfig.m_useVault) {
			if(pManager.getPlugin("Vault") != null) {
				//gather service provider
				RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
				
				//check if it exists
				if(provider != null) {
					m_economy = provider.getProvider();
					
					//did we properly get the economy instance?
					if(m_economy != null) {
						m_vaultActive = true;
						
						//inform users we have hooked
						IOUtility.log("Vault recognized!");
					}
				}
			}
		}
		
		//Herochat
		if(EmpiresConfig.m_useHerochat) {
			if(pManager.getPlugin("Herochat") != null) {
				m_herochatActive = true;
				//create channels for usage
				//Herochat.getChannelManager().addChannel(new CityChatOld());
				/*ArrayList<com.dthielke.api.Channel> channels = (ArrayList<com.dthielke.api.Channel>)Herochat.getChannelManager().getChannels();
				boolean containsAlly = false;
				boolean containsKingdoms = false;
				
				for(com.dthielke.api.Channel ch : channels) {
					if(ch.getName().equalsIgnoreCase("kingdom")) {
						containsKingdoms = true;
						
						try {
							YamlConfiguration conf = new YamlConfiguration();
							conf.load(new File("plugins/Herochat/channels/" + ch.getName() + ".yml"));
							ch.setFormat(conf.getString("format"), true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							e.printStackTrace();
						}
						
					} else if(ch.getName().equalsIgnoreCase("ally")) {
						containsAlly = true;
						
						try {
							YamlConfiguration conf = new YamlConfiguration();
							conf.load(new File("plugins/Herochat/channels/" + ch.getName() + ".yml"));
							ch.setFormat(conf.getString("format"), true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							e.printStackTrace();
						}
					}
					
					if(containsKingdoms && containsAlly)
						break;
				}*/
				
					//if(!containsAlly
					//Herochat.getChannelManager().addChannel(new VoidChat());
					
				if(pManager.getPlugin("Herochat").getDescription().getVersion().contains("5.7")) {
					pManager.registerEvents(new EmpiresListenerChat(), this);
				} else {
					new EmpiresListenerChatLegacy(this);//registers self
				}
					//register listener to format chatting
					
					//pManager.registerEvents(new EmpiresListenerChat(), this);
					
					//inform users we have hooked
				IOUtility.log("Herochat recognized!");
			}
		}
		
		/*
		 * Plugin
		 */
		
		//initialize YAML data streams
		try {
			m_joinableHandler.loadFile(this);
			m_playerHandler.loadFile(this);
			m_boardHandler.loadFile(this);
			
			EmpiresDataUpdater updater = new EmpiresDataUpdater();
			updater.checkDataForUpdate();
		} catch(Exception e) {
			e.printStackTrace();
			
			IOUtility.log("A fatal error occurred initializing Empires");
			IOUtility.log("Empires is shutting down...");
			
			return;
		}
		
		//set commands, single command trunk
		m_commandExecutor = new EmpiresCommands();
		
		getCommand("Empires").setExecutor(m_commandExecutor);
		
		//set the listeners
		pManager.registerEvents(new EmpiresListenerPlayerGeneral(), this);
		pManager.registerEvents(new EmpiresListenerPlayerRestriction(), this);
		
		//register tasks
		Bukkit.getScheduler().runTaskTimer(this, new PowerUpdateTask(), 20L * 60 * 1, 20L * 60 * 1);//set power updates for every 1 minutes
		Bukkit.getScheduler().runTaskTimer(this, new InactivityTask(), 20 * 1L, 20 * 60 * 60 * 2);//check for inactivity every 2 hours and once on startup
		Bukkit.getScheduler().runTaskTimer(this, new SaveTask(), 20 * 60 * 5, 20 * 60 * 5);//save YML every five minutes
		
		//set our instance
		m_instance = this;
		
		//create API
		m_api = new EmpiresAPI();
		
		//test
		//IOUtility.log("STARTING TEST", ChatColor.YELLOW);
		//SQLitePlayerConfiguration data = new SQLitePlayerConfiguration(null);
		//data.testWrite();
	}
	
	public EmpiresAPI getAPI() {
		return m_api;
	}
	
	@Override
	public void onDisable() {
		if(SAVE_FILE) {
			m_joinableHandler.saveFile();
			m_playerHandler.saveFile();
			m_boardHandler.saveFile();
		}
	}
}
