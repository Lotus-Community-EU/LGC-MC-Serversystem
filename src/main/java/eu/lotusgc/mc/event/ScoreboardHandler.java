package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import eu.lotusgc.mc.command.AfKCMD;
import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.ClearLag;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Prefix;
import eu.lotusgc.mc.misc.ServerRestarter;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;

public class ScoreboardHandler implements Listener{
	
	private static HashMap<String, String> tabHM = new HashMap<>(); //HashMap for Tab
	private static HashMap<String, String> chatHM = new HashMap<>(); //HashMap for Chat
	private static HashMap<String, String> roleHM = new HashMap<>(); //HashMap for Team Priority (Sorted)
	private static HashMap<String, String> sbHM = new HashMap<>(); //HashMap for Sideboard (Like Chat, just with no additional chars)
	
	/*
	 * Scoreboard States:
	 * 0 = off
	 * 1 = default (view own info)
	 * 2 = Jobs
	 * 3 = Reports (Admin and higher - IPermissible "lgc.viewReports"
	 * 4 = Serverstatus (Admin and higher - IPermissible "lgc.viewServerhealth"
	 * 5 = Radio Information (not upon release - Radio will follow up later)
	 * 6 = Servers and player count on each one
	 * 7 = World Info and Coordinates
	 * 8 = View Players around you (normal = 500 blocks, premium = 1000, Admin and higher (2500 blocks))
	 * 9 = View Entities (except Players) around you (normal 100 blocks, premium 250 blocks, Staffs general up to 500 blocks (only the nearest 10 will be listed anyway!)
	 */
	
	private static int sbSwitch = 0;
	
	public void setScoreboard(Player player) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective("aaa", Criteria.DUMMY, "LGCINFOBOARD");
		LotusController lc = new LotusController();
		String sbPrefix = lc.getPrefix(Prefix.SCOREBOARD);
		
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		sbSwitch++;
		if(sbSwitch == 10) sbSwitch = 0; //resetting the Switcher to 0 so the views are going back again :)
		o.setDisplayName(sbPrefix);
		int sbState = getSBState(player);
		if(sbState == 0) {
			//Player chose not to have a sideboard.
		}else if(sbState == -1) {
			//Player wont see a sideboard as well, however due to an error.
			Main.logger.severe("Sideboard status reports code '-1' !");
		}else if(sbState == 1) {
			if(sbSwitch >= 0 && sbSwitch <= 5) {
				//money
				o.getScore(lc.sendMessageToFormat(player, "event.scoreboard.money")).setScore(5);
				o.getScore("§7» Pocket: §a" + lc.getPlayerData(player, Playerdata.MoneyPocket) + " §6Loti").setScore(4);
				o.getScore("§7» Bank: §e" + lc.getPlayerData(player, Playerdata.MoneyBank) + " §6Loti").setScore(3);
				o.getScore("§f§4").setScore(2);
				//role
				o.getScore(lc.sendMessageToFormat(player, "event.scoreboard.role")).setScore(1);
				o.getScore(retGroup(player)).setScore(0);
			}else if(sbSwitch >= 6 && sbSwitch <= 10) {
				//playerinfo (userid, clan, ping)
				o.getScore(lc.sendMessageToFormat(player, "event.scoreboard.userid")).setScore(7);
				o.getScore("§7» §a" + lc.getPlayerData(player, Playerdata.LotusChangeID)).setScore(6);
				o.getScore("§f§c").setScore(5);
				o.getScore("§7Clan:").setScore(4);
				o.getScore("§7» §a" + lc.getPlayerData(player, Playerdata.Clan)).setScore(3);
				o.getScore("§f§a").setScore(2);
				o.getScore("§7Ping:").setScore(1);
				o.getScore("§7» §a" + colorisePing(player.getPing())).setScore(0);
				
			}
		}else if(sbState == 2) {
			//jobs
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 3) {
			//reports (not yet implementable due to no report system existing)
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 4) {
			//server health (not yet implementable due to no API existing yet)
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 5) {
			//radio information (not yet implementable due to no radio existing for lgc yet)
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 6) {
			//servers and each players
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 7) {
			//world info
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 8) {
			//playerradar
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}else if(sbState == 9) {
			//entityradar
			o.getScore("§cView").setScore(1);
			o.getScore("§cunsupported.").setScore(0);
		}
		player.setScoreboard(sb);
		
		Team projlead = getTeam(sb, "projectlead", ChatColor.DARK_GRAY);
		Team viceProjLead = getTeam(sb, "viceprojlead", ChatColor.DARK_GRAY);
		Team staffmanager = getTeam(sb, "staffmanager", ChatColor.DARK_GRAY);
		Team staffsupervisor = getTeam(sb, "staffsupervisor", ChatColor.DARK_GRAY);
		Team developer = getTeam(sb, "developer", ChatColor.DARK_GRAY);
		Team headofcommunity = getTeam(sb, "headofcommunity", ChatColor.DARK_GRAY);
		Team humanresources = getTeam(sb, "humanresources", ChatColor.DARK_GRAY);
		Team qualityassman = getTeam(sb, "qualityassman", ChatColor.DARK_GRAY);
		Team admin = getTeam(sb, "admin", ChatColor.GRAY);
		Team builder = getTeam(sb, "builder", ChatColor.GRAY);
		Team designer = getTeam(sb, "designer", ChatColor.GRAY);
		Team moderator = getTeam(sb, "moderator", ChatColor.GRAY);
		Team support = getTeam(sb, "support", ChatColor.GRAY);
		Team translator = getTeam(sb, "translator", ChatColor.GRAY);
		Team addon = getTeam(sb, "addon", ChatColor.GRAY);
		Team retired = getTeam(sb, "retired", ChatColor.WHITE);
		Team beta = getTeam(sb, "beta", ChatColor.WHITE);
		Team userg = getTeam(sb, "user", ChatColor.WHITE);
		Team afk = sb.registerNewTeam("00500");
		afk.setPrefix("§9");
		afk.setColor(ChatColor.DARK_AQUA);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			//Lotus Internal
			String nick = lc.getPlayerData(all, Playerdata.Nick);
			String clan = lc.getPlayerData(all, Playerdata.Clan);
			String id = lc.getPlayerData(player, Playerdata.LotusChangeID);
			if(nick.equalsIgnoreCase("none")) {
				all.setCustomName(all.getName());
			}else {
				all.setCustomName(nick);
			}
			if(clan.equalsIgnoreCase("none")) {
				clan = "";
			}
			
			//LuckPerms
			UserManager um = Main.luckPerms.getUserManager();
			User user = um.getUser(all.getName());
			
			if(AfKCMD.afkList.contains(all.getUniqueId())) {
				afk.addEntry(all.getName());
				all.setPlayerListName("§9" + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
			}else {
				// if player is not afk
				if(user.getPrimaryGroup().equalsIgnoreCase("projectlead")) {
					projlead.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("viceprojectleader")) {
					viceProjLead.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("staffmanager")) {
					staffmanager.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("staffsupervisor")) {
					staffsupervisor.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("developer")) {
					developer.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("headofcommunity")) {
					headofcommunity.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("humanresources")) {
					humanresources.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("qualityassman")) {
					qualityassman.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("admin")) {
					admin.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("builder")) {
					builder.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("designer")) {
					designer.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("moderator")) {
					moderator.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("support")) {
					support.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("translator")) {
					translator.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("addon")) {
					addon.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("retired")) {
					retired.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else if(user.getPrimaryGroup().equalsIgnoreCase("beta")) {
					beta.addEntry(all.getName());
					all.setDisplayName(returnPrefix(user.getPrimaryGroup(), RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix(user.getPrimaryGroup(), RankType.TAB) + all.getCustomName() + " §7(§a" + id + "§7) §f" + clan);
				}else {
					userg.addEntry(all.getName());
					all.setDisplayName(returnPrefix("player", RankType.CHAT) + all.getCustomName());
					all.setPlayerListName(returnPrefix("player", RankType.TAB) + all.getCustomName() + "§7ID: §a" + id + " §f" + clan);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		setScoreboard(event.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		String message = ChatColor.translateAlternateColorCodes('&', event.getMessage().replace("%", "%%"));
		
		if(lc.getServerName().equalsIgnoreCase("Farmserver")) {
			String world = "";
			if(player.getWorld().getEnvironment() == World.Environment.NORMAL) {
				world = "§aOverworld";
			}else if(player.getWorld().getEnvironment() == World.Environment.NORMAL) {
				world = "§cNether";
			}else if(player.getWorld().getEnvironment() == World.Environment.NORMAL) {
				world = "§0The End";
			}
			event.setFormat("§7[" + world + "§7] " + player.getDisplayName() + " §7(" + lc.getPlayerData(player, Playerdata.LotusChangeID)+ "): " + message);
		}else {
			event.setFormat(player.getDisplayName() + " §7(" + lc.getPlayerData(player, Playerdata.LotusChangeID)+ "): " + message);
		}
		
	}
	
	static int getSBState(Player player) {
		LotusController lc = new LotusController();
		if(lc.getPlayerData(player, Playerdata.SideboardState).matches("^[0-9]+$")) {
			return Integer.parseInt(lc.getPlayerData(player, Playerdata.SideboardState));
		}else {
			return -1;
		}
	}
	
	String colorisePing(int ping) {
		String toReturn = "";
		if(ping >= 0 && ping <= 99) {
			toReturn = "§a" + ping + "§7ms";
		}else if(ping >= 100 && ping <= 250) {
			toReturn = "§e" + ping + "§7ms";
		}else if(ping >= 251 && ping <= 400) {
			toReturn = "§c" + ping + "§7ms";
		}else if(ping >= 401) {
			toReturn = "§4" + ping + "§7ms";
		}
		return toReturn;
	}
	
	public Team getTeam(Scoreboard scoreboard, String role, ChatColor chatcolor) {
		Team team = scoreboard.registerNewTeam(returnPrefix(role, RankType.TEAM));
		team.setPrefix(returnPrefix(role, RankType.TAB));
		team.setColor(chatcolor);
		team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER); //TBD for removal if issues arise.
		return team;
	}
	
	
	
	private static String retGroup(Player player) {
		String group = "";
		UserManager um = Main.luckPerms.getUserManager();
		User user = um.getUser(player.getName());
		switch(user.getPrimaryGroup()) {
		default: group = user.getPrimaryGroup(); break;
		}
		return group;
	}
	
	public static void initRoles() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_ranks");
			ResultSet rs = ps.executeQuery();
			tabHM.clear();
			chatHM.clear();
			roleHM.clear();
			sbHM.clear();
			int count = 0;
			while(rs.next()) {
				count++;
				tabHM.put(rs.getString("ingame_id"), rs.getString("colour") + rs.getString("short"));
				chatHM.put(rs.getString("ingame_id"), rs.getString("colour") + rs.getString("name"));
				roleHM.put(rs.getString("ingame_id"), rs.getString("priority"));
				sbHM.put(rs.getString("ingame_id"), rs.getString("name"));
			}
			Main.logger.info("Downloaded " + count + " roles for the Prefix System. | Source: ScoreboardHandler#initRoles();");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//needs World#getTime()
	@SuppressWarnings("unused")
	private String parseTimeWorld(long time) {
		long gameTime = time;
		long hours = gameTime / 1000 + 6;
		long minutes = (gameTime % 1000) * 60 / 1000;
		String ampm = "AM";
		if(hours >= 12) {
			hours -= 12; ampm = "PM";
		}
		if(hours >= 12) {
			hours -= 12; ampm = "AM";
		}
		if(hours == 0) hours = 12;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());
		return hours + ":" + mm + " " + ampm;
	}
	
	private String returnPrefix(String role, RankType type) {
		String toReturn = "";
		if(type == RankType.TAB) {
			if(tabHM.containsKey(role)) {
				toReturn = tabHM.get(role) + " §7» ";
			}else {
				toReturn = "&cDEF";
			}
		}else if(type == RankType.CHAT) {
			if(chatHM.containsKey(role)) {
				toReturn = chatHM.get(role) + " §7» ";
			}else {
				toReturn = "&cDEF";
			}
		}else if(type == RankType.SIDEBOARD) {
			if(sbHM.containsKey(role)) {
				toReturn = sbHM.get(role);
			}else {
				toReturn = "DEF";
			}
		}else if(type == RankType.TEAM) {
			if(roleHM.containsKey(role)) {
				toReturn = roleHM.get(role);
			}else {
				Random r = new Random();
				toReturn = "0" + r.nextInt(0, 250) + "0";
			}
		}
		toReturn = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', toReturn); //transforms & -> §
		toReturn = LotusController.translateHEX(toReturn); //translates HEX Color Codes into Minecraft (Custom Color Codes ability)
		return toReturn;
	}
	
	public enum RankType {
		TAB,
		SIDEBOARD,
		CHAT,
		TEAM
	}
	
	public void startScheduler(int delay, int sideboardRefresh, int tabRefresh) {
		//SYNC TASK - ONLY FOR THE SIDEBOARD
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					setScoreboard(all);
				}
			}
		}.runTaskTimer(Main.main, delay, sideboardRefresh);
		
		//For tasks which needs to run on main thread
		new BukkitRunnable() {
			@Override
			public void run() {
				new ServerRestarter().triggerRestart();
				new ClearLag().triggerClearlag();
			}
		}.runTaskTimer(Main.main, delay, tabRefresh);
		
		//For tasks which can run on alternative threads (async)
		new BukkitRunnable() {
			@Override
			public void run() {
				LotusController lc = new LotusController();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.setPlayerListHeaderFooter("§cLotus §aGaming §fCommunity", "§7Server: §a" + lc.getServerName() + "\n§7Time: §a" + sdf.format(new Date()) + "\n§7Ping: §a" + all.getPing());
				}
			}
		}.runTaskTimerAsynchronously(Main.main, delay, tabRefresh);
	}

}
