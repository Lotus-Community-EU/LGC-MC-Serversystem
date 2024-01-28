package eu.lotusgc.mc.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import eu.lotusgc.mc.command.ChatClearCMD;
import eu.lotusgc.mc.command.FlyCMD;
import eu.lotusgc.mc.command.GamemodeCMD;
import eu.lotusgc.mc.command.Homesystem;
import eu.lotusgc.mc.command.InvseeCMD;
import eu.lotusgc.mc.command.OpenCommand;
import eu.lotusgc.mc.command.PrivateMessageCMD;
import eu.lotusgc.mc.command.TimeCMD;
import eu.lotusgc.mc.command.WorkbenchCMD;
import eu.lotusgc.mc.event.JoinLeaveEvent;
import eu.lotusgc.mc.event.KillStats;
import eu.lotusgc.mc.event.ScoreboardHandler;
import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.SyncServerdata;
import net.luckperms.api.LuckPerms;

public class LotusManager {
	
	public static File mainFolder = new File("plugins/LotusGaming");
	public static File mainConfig = new File("plugins/LotusGaming/config.yml");
	public static File propsConfig = new File("plugins/LotusGaming/propertiesBackup.yml");
	
	//will be loaded as first upon plugin loading!
		public void preInit() {
			long current = System.currentTimeMillis();
			
			//Configs
			
			
			if(!mainFolder.exists()) mainFolder.mkdirs();
			if(!mainConfig.exists()) try { mainConfig.createNewFile(); } catch (Exception ex) { };
			if(!propsConfig.exists()) try { propsConfig.createNewFile(); } catch (Exception ex) { };
			
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(mainConfig);
			cfg.addDefault("MySQL.Host", "127.0.0.1");
			cfg.addDefault("MySQL.Port", "3306");
			cfg.addDefault("MySQL.Database", "TheDataBaseTM");
			cfg.addDefault("MySQL.Username", "user");
			cfg.addDefault("MySQL.Password", "pass");
			cfg.addDefault("Password.PMs", "APassword");
			cfg.options().copyDefaults(true);
			
			try { cfg.save(mainConfig); Homesystem.initialiseFile(); } catch (Exception ex) { }
			
			if(!cfg.getString("MySQL.Password").equalsIgnoreCase("pass")) {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			}
			
			Bukkit.getConsoleSender().sendMessage("§aPre-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
		}
		
		//Commands and Event registers will be thrown in here!
		public void mainInit() {
			long current = System.currentTimeMillis();
			
			Main.main.getCommand("s").setExecutor(new OpenCommand());
			Main.main.getCommand("profile").setExecutor(new OpenCommand());
			Main.main.getCommand("gm").setExecutor(new GamemodeCMD());
			Main.main.getCommand("cc").setExecutor(new ChatClearCMD());
			Main.main.getCommand("invsee").setExecutor(new InvseeCMD());
			Main.main.getCommand("sethome").setExecutor(new Homesystem());
			Main.main.getCommand("delhome").setExecutor(new Homesystem());
			Main.main.getCommand("listhomes").setExecutor(new Homesystem());
			Main.main.getCommand("home").setExecutor(new Homesystem());
			Main.main.getCommand("time").setExecutor(new TimeCMD());
			Main.main.getCommand("msg").setExecutor(new PrivateMessageCMD());
			Main.main.getCommand("msgopt").setExecutor(new PrivateMessageCMD());
			Main.main.getCommand("r").setExecutor(new PrivateMessageCMD());
			Main.main.getCommand("fly").setExecutor(new FlyCMD());
			Main.main.getCommand("workbench").setExecutor(new WorkbenchCMD());
			
			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new KillStats(), Main.main);
			pm.registerEvents(new JoinLeaveEvent(), Main.main);
			pm.registerEvents(new ScoreboardHandler(), Main.main);
			pm.registerEvents(new FlyCMD(), Main.main);
			
			Bukkit.getConsoleSender().sendMessage("§aMain-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
		}
		
		//will loaded as last - for schedulers and misc stuff
		public void postInit() {
			long current = System.currentTimeMillis();
			
			LotusController lc = new LotusController();
			lc.initLanguageSystem();
			lc.initPlayerLanguages();
			lc.initPrefixSystem();
			lc.loadServerIDName();
			
			SyncServerdata.startScheduler();
			ScoreboardHandler.initRoles();
			new ScoreboardHandler().startScheduler(0, 40, 20);
			
			Main.luckPerms = (LuckPerms) Bukkit.getServer().getServicesManager().load(LuckPerms.class);
			
			Bukkit.getConsoleSender().sendMessage("§aPost-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
		}

}
