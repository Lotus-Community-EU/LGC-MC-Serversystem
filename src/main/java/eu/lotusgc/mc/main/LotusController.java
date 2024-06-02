package eu.lotusgc.mc.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import eu.lotusgc.mc.misc.DatabaseInventoryData;
import eu.lotusgc.mc.misc.InputType;
import eu.lotusgc.mc.misc.InventorySyncData;
import eu.lotusgc.mc.misc.InventoryUtils;
import eu.lotusgc.mc.misc.Money;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Prefix;
import eu.lotusgc.mc.misc.RAMInfo;
import eu.lotusgc.mc.misc.Serverdata;
import net.md_5.bungee.api.ChatColor;

public class LotusController {
	
	// < - - - INSTANCES FOR ALL SECTIONS GROUPED IN ORDER - - - >
	
	//Language System
	private static HashMap<String, HashMap<String, String>> langMap = new HashMap<>();
	public static HashMap<String, String> playerLanguages = new HashMap<>();
	private static List<String> availableLanguages = new ArrayList<>();
	
	//Prefix System
	private static HashMap<String, String> prefix = new HashMap<>();
	private static boolean useSeasonalPrefix = false;
	
	//Servername and ServerID
	private static String servername = "Server";
	private static String serverid = "0";
	
	//misc
	private static List<UUID> afkPlayers = new ArrayList<>();
	
	// < - - - END OF INSTANCES - - - >
	
	/* Server reads out how many columns there are for the language system. 
	 * For each entry (except the key value and optionings) a HashMap<String, String> will be created within a HashMap<String, HashMap<String, String>>
	 * Also it will "download" all keys and their respective value to have less methods for the init.
	 * IF the map keeps empty due to an unknown reason, then the path will be given out.
	 */
	public boolean initLanguageSystem() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_translations");
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd =  rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			int languageStrings = 0;
			int colToStartFrom = 0;
			if(rs.next()) {
				for(int i = 1; i <= columnCount; i++) {
					String name = rsmd.getColumnName(i);
					if(name.equals("German")) {
						colToStartFrom = i;
						break;
					}
				}
				HashMap<String, String> map;
				for(int i = colToStartFrom; i <= columnCount; i++) {
					String name = rsmd.getColumnName(i);
					availableLanguages.add(name);
					Main.logger.info("Logged language " + name + " to List");
					PreparedStatement ps1 = MySQL.getConnection().prepareStatement("SELECT path," + name + ",isGame FROM core_translations");
					ResultSet rs1 = ps1.executeQuery();
					map = new HashMap<>();
					int subLangStrings = 0;
					while(rs1.next()) {
						if(rs1.getBoolean("isGame")) {
							subLangStrings++;
							//Only get Strings, which are for the game (what would we do with website/bot string, right?)
							map.put(rs1.getString("path"), rs1.getString(name));
						}
					}
					languageStrings = subLangStrings;
					langMap.put(name, map);
				}
				Main.logger.info("langMap logged " + langMap.size() + " entries with each " + languageStrings + " entries per language.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return langMap.isEmpty();
	}
	
	public List<String> getAvailableLanguages() {
		return availableLanguages;
	}
	
	public boolean initPlayerLanguages() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mcuuid,language FROM mc_users");
			ResultSet rs = ps.executeQuery();
			int count = 0;
			while(rs.next()) {
				count++;
				playerLanguages.put(rs.getString("mcuuid"), rs.getString("language"));
			}
			rs.close();
			ps.close();
			Main.logger.info("Initialised " + count + " users for the language system. | Source: LotusController#initPlayerLanguages();");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerLanguages.isEmpty();
	}
	
	//only used, when a never-joined player joins the network.
	public void addPlayerLanguageWhenRegistered(Player player) {
		playerLanguages.put(player.getUniqueId().toString(), "English");
		Main.logger.info("Added " + player.getName() + " to the languageMap with default. | Source: LotusController#addPlayerLanguageWhenRegistered(PLAYER);");
	}
	
	//This method is used if no spaceholders needs to be translated additionally.
	public void sendMessageReady(Player player, String path) {
		player.sendMessage(getPrefix(Prefix.MAIN) + sendMessageToFormat(player, path));
	}
	
	//This method is used if spaceholders needs to be translated before sending (or if the target is NOT a player).
	public String sendMessageToFormat(Player player, String path) {
		String toReturn = returnString(returnLanguage(player), path);
		if(toReturn.equalsIgnoreCase("none")) {
			return returnString("English", path);
		}else {
			return toReturn;
		}
	}
	
	//This method is returns the player's selected language.
	public String returnLanguage(Player player) {
		String defaultLanguage = "English";
		if(playerLanguages.containsKey(player.getUniqueId().toString())) {
			defaultLanguage = playerLanguages.get(player.getUniqueId().toString());
		}
		return defaultLanguage;
	}
	
	//This method is just for one string, the NoPerm one
	public void noPerm(Player player, String lackingPermissionNode) {
		player.sendMessage(getPrefix(Prefix.System) + sendMessageToFormat(player, "global.noPermission").replace("%permissionNode%", lackingPermissionNode));
	}
	
	//This method returns the String from the language selected.
	private String returnString(String language, String path) {
		if(langMap.containsKey(language)) {
			HashMap<String, String> localMap = langMap.get(language);
			if(localMap.containsKey(path)) {
				return ChatColor.translateAlternateColorCodes('&', localMap.get(path));
			}else {
				return "The path '" + path + "' does not exist!";
			}
		}else {
			return "The language '" + language + "' does not exist!";
		}
	}
	
	// < - - - END OF LANGUAGE SYSTEM - - - >
	// < - - - BEGIN OF THE PREFIX SYSTEM - - - >
	
	//initialise the Prefix System (also used to re-load it after a command reload)
	public void initPrefixSystem() {
		if(!prefix.isEmpty()) prefix.clear();
		
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_prefix");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				if(rs.getString("type").equalsIgnoreCase("UseSeason")) {
					useSeasonalPrefix = translateToBool(rs.getString("prefix"));
					if(useSeasonalPrefix) {
						Main.logger.info("Using Seasonal Prefix | Source: LotusController#initPrefixSystem()");
					}else {
						Main.logger.info("Using Normal Prefix | Source: LotusController#initPrefixSystem()");
					}
				}
				prefix.put(rs.getString("type"), rs.getString("prefix").replace('&', '§'));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean translateToBool(String input) {
		switch(input) {
		case "TRUE": return true;
		case "FALSE": return false;
		default: return false;
		}
	}
	
	//get Prefix with the Enum class "eu.lotusgc.mc.misc.Prefix"
	public String getPrefix(Prefix prefixType) {
		String toReturn = "";
		switch(prefixType) {
		case MAIN: if(useSeasonalPrefix) { toReturn = prefix.get("SEASONAL_MAIN"); } else { toReturn = prefix.get("MAIN"); }
			break;
		case PMSYS: toReturn = prefix.get("PMSYS");
			break;
		case SCOREBOARD: if(useSeasonalPrefix) { toReturn = prefix.get("SEASONAL_SB"); } else { toReturn = prefix.get("SCOREBOARD"); }
			break;
		case System: toReturn = prefix.get("SYSTEM");
			break;
		default: toReturn = prefix.get("MAIN");
			break;
		}
		return toReturn;
	}
	
	// < - - - END OF PREFIX SYSTEM - - - >
	// < - - - BEGIN OF THE ITEMSTACKS - - - >
	
	public ItemStack defItem(Material material, String displayName, int amount) {
		ItemStack is = new ItemStack(material, amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack defItemRandom(List<Material> materialList, String displayName, int amount, String... lore) {
		Random random = new Random();
		int randomIndex = random.nextInt(materialList.size());
		ItemStack is = new ItemStack(materialList.get(randomIndex), amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		List<String> loreList = new ArrayList<>();
		for(String string : lore) {
			loreList.add(string);
		}
		im.setLore(loreList);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack potionItem(int amount, PotionType potionType, String displayName, Color potionColor) {
		ItemStack is = new ItemStack(Material.POTION, amount);
		PotionMeta pm = (PotionMeta) is.getItemMeta();
		pm.setBasePotionType(potionType);
		pm.setColor(potionColor);
		pm.setDisplayName(displayName);
		is.setItemMeta(pm);
		return is;
	}
	
	public ItemStack enchantedItem(Material material, int amount, String displayName, Enchantment enchantment) {
		ItemStack is = new ItemStack(material, amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		im.addEnchant(enchantment, 1, true);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack loreItem(Material material, int amount, String displayname, String... lore) {
		List<String> loreList = new ArrayList<String>();
		for(String string : lore) {
			loreList.add(string);
		}
		ItemStack is = new ItemStack(material, amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayname);
		im.setLore(loreList);
		is.setItemMeta(im);
		return is;
	}
	
	@Deprecated
	public ItemStack skullItem(int amount, String displayname, String skullOwner) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(skullOwner);
		skullMeta.setDisplayName(displayname);
		skull.setItemMeta(skullMeta);
		return skull;
	}
	
	public ItemStack skullItem(int amount, String displayname, Player skullOwner) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner.getUniqueId()));
		skullMeta.setDisplayName(displayname);
		skull.setItemMeta(skullMeta);
		return skull;
	}
	
	public ItemStack naviServerItem(Material material, String servername) {
		ArrayList<String> lore = new ArrayList<>();
		ItemStack is = new ItemStack(material, 1);
		ItemMeta im = is.getItemMeta();
		HashMap<String, String> map = getServerData(servername);
		boolean online = translateBoolean(map.get("isOnline"));
		boolean isMaintenance = translateBoolean(map.get("isMonitored"));
		boolean isLocked = translateBoolean(map.get("isLocked"));
		String fancyName = map.get("displayname");
		String currentPlayers = map.get("currentPlayers");
		String joinlevel = translateJoinLevel(map.get("req_joinlevel"));
		if(online) {
			lore.add("§7Online: §ayes");
			lore.add("§2" + currentPlayers + " §7Players");
		}else {
			lore.add("§7Online: §cno");
		}
		lore.add("§7Server Entry Level:");
		lore.add(joinlevel);
		if(isMaintenance) {
			lore.add("§7Monitored: §cyes");
		}
		if(isLocked) {
			lore.add("§7Locked: §cyes");
		}
		im.setLore(lore);
		im.setDisplayName(fancyName);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack wt_Item(Material mat, String dpname, World world) {
		List<String> lore = new ArrayList<>();
		ItemStack item = new ItemStack(mat, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(dpname);
		lore.add("§7Current Players on");
		lore.add("§a" + dpname + "§7: §a" + world.getPlayers().size());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	// < - - - ENC OF THE ITEMSTACKS - - - >
	// < - - - BEGIN OF THE MONEY API - - - >
	
	public double getMoney(Player player, Money type) {
		double money = 0.0;
		if(type == Money.BANK) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT money_bank FROM mc_users WHERE mcuuid = ?");
				ps.setString(1, player.getUniqueId().toString());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					money = rs.getDouble("money_bank");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if(type == Money.POCKET) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT money_pocket FROM mc_users WHERE mcuuid = ?");
				ps.setString(1, player.getUniqueId().toString());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					money = rs.getDouble("money_pocket");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return money;
	}
	
	public int getInterestLevel(Player player) {
		int interestLevel = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT money_interestLevel FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				interestLevel = rs.getInt("money_interestLevel");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interestLevel;
	}
	
	public boolean hasAccount(Player player) {
		return true;
		//Automatic true, as upon joining the server everything will be created in database - thus also has an account!
	}
	
	public void setMoney(Player player, double money, Money type) {
		BigDecimal dec = new BigDecimal(money).setScale(2, RoundingMode.DOWN);
		if(type == Money.BANK) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET money_bank = ? WHERE mcuuid = ?");
				ps.setDouble(1, dec.doubleValue());
				ps.setString(2, player.getUniqueId().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if(type == Money.POCKET) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET money_pocket = ? WHERE mcuuid = ?");
				ps.setDouble(1, dec.doubleValue());
				ps.setString(2, player.getUniqueId().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addMoney(Player player, double moneyToAdd, Money type) {
		double oldMoney = getMoney(player, type);
		double newMoney = (oldMoney + moneyToAdd);
		setMoney(player, newMoney, type);
	}
	
	//if return is true, enough funds were there and a transaction was made; if false, no transaction was made and no money was removed.
	public boolean removeMoney(Player player, double moneyToRemove, Money type) {
		boolean hadEnoughFunds = false;
		double oldMoney = getMoney(player, type);
		if(moneyToRemove > oldMoney) {
			hadEnoughFunds = false;
		}else {
			double newMoney = (oldMoney - moneyToRemove);
			setMoney(player, newMoney, type);
			hadEnoughFunds = true;
		}
		return hadEnoughFunds;
	}
	
	public void setInterestLevel(Player player, int newInterestLevel) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET money_interestLevel = ? WHERE mcuuid = ?");
			ps.setInt(1, newInterestLevel);
			ps.setString(2, player.getUniqueId().toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasEnoughFunds(Player player, double moneyToCheck, Money type) {
		double current = getMoney(player, type);
		return (current > moneyToCheck);
	}
	
	// < - - - END OF THE MONEY API - - - >
	// < - - - BEGIN OF THE MISC UTILS - - - >
	
	//load server id and name into cache
	public void loadServerIDName() {
		File file = new File("server.properties");
		Properties p = new Properties();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
			p.load(bis);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		servername = p.getProperty("server-name");
		serverid = p.getProperty("server-id");
	}
	
	//get the server name
	public String getServerName() {
		return servername;
	}
	
	//get the server id
	public String getServerId() {
		return serverid;
	}
	
	//Original by Grubsic (LGC Vice Project Leader) | Thank you for your contributions! <3
	private static final Pattern HEX_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");
	public static String translateHEX(String text) {
		Matcher matcher = HEX_PATTERN.matcher(text);
		while(matcher.find()) { text = text.replace(matcher.group(), ChatColor.of(matcher.group()).toString()); }
		return text;
	}
	
	public String getServerData(String servername, Serverdata data, InputType type) {
		String toReturn = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT " + data.getColumnName() + " FROM mc_serverstats WHERE " + type.getColumnName() + " = ?");
			ps.setString(1, servername);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				toReturn = rs.getString(data.getColumnName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	public HashMap<String, String> getServerData(String servername) {
		HashMap<String, String> hashMap = new HashMap<>();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_serverstats WHERE servername = ?");
			ps.setString(1, servername);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				for(Serverdata data : Serverdata.values()) {
					hashMap.put(data.getColumnName(), rs.getString(data.getColumnName()));
				}
			}else {
				for(Serverdata data : Serverdata.values()) {
					hashMap.put(data.getColumnName(), "none");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hashMap;
	}
	
	public String getPlayerData(Player player, Playerdata data) {
		String toReturn = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT " + data.getColumnName() + " FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				toReturn = rs.getString(data.getColumnName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	public HashMap<String, String> getPlayerData(Player player){
		HashMap<String, String> hashMap = new HashMap<>();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				for(Playerdata data : Playerdata.values()) {
					hashMap.put(data.getColumnName(), rs.getString(data.getColumnName()));
				}
			}else {
				for(Playerdata data : Playerdata.values()) {
					hashMap.put(data.getColumnName(), "none");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hashMap;
	}
	
	public String getRAMInfo(RAMInfo type) {
		String toReturn = "";
		Runtime runtime = Runtime.getRuntime();
		if(type == RAMInfo.ALLOCATED) {
			toReturn = runtime.totalMemory() / 1048576L + "";
		}else if(type == RAMInfo.USING) {
			toReturn = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + "";
		}else if(type == RAMInfo.FREE) {
			toReturn = runtime.freeMemory() / 1048576L + "";
		}
		return toReturn;
	}

	public boolean translateBoolean(String input) {
		switch(input) {
		case "0": return false;
		case "false": return false;
		case "1": return true;
		case "true": return true;
		default: Main.logger.severe("Error in LotusController#translateBoolean() - expected 0,1,true,false but got " + input); return false;
		}
	}
	
	public int translateInt(String input) {
		if(input.matches("^[0-9]+-$")) {
			return Integer.parseInt(input);
		}else {
			return -1;
		}
	}
	
	public String translateJoinLevel(String input) {
		switch(input) {
		case "ALPHA": return "§cAlpha";
		case "BETA": return "§dBeta";
		case "EVERYONE": return "§aEveryone";
		case "STAFF": return "§cStaff";
		default: Main.logger.severe("Error in LotusController#translateJoinLevel() - expected ALPHA,BETA,STAFF,EVERYONE but got " + input); return "§aEveryone";
		}
	}
	
	public boolean isPlayerAFK(Player player) {
		if(afkPlayers.contains(player.getUniqueId())) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setPlayerN_AFK(Player player, boolean status) {
		if(status) {
			afkPlayers.add(player.getUniqueId());
		}else {
			if(afkPlayers.contains(player.getUniqueId())) afkPlayers.remove(player.getUniqueId());
		}
	}
	
	static long lastSystemTime = 0;
	static long lastProcessCpuTime = 0;
	static int availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	
	public synchronized double getCpuUsage() {
		if(lastSystemTime == 0) {
			baselineCounters();
		}
		long systemTime = System.nanoTime();
		long processCpuTime = 0;
		
		if(ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean) {
			processCpuTime = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
		}
		double cpuUsage = (double) (processCpuTime - lastProcessCpuTime) / (systemTime - lastSystemTime)*100.0;
		lastSystemTime = systemTime;
		lastProcessCpuTime = processCpuTime;
		return cpuUsage / availableProcessors;
	}
	
	private void baselineCounters() {
		lastSystemTime = System.nanoTime();
		if(ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean) {
			lastProcessCpuTime = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
		}
	}
	
	
	// INV SYNC Section
	
	public void onInvSyncJoinFunction(final Player player) {
		final InventorySyncData syncData = new InventorySyncData();
		backupAndReset(player, syncData);
		DatabaseInventoryData data = getData(player);
		setInventory(player, data, syncData);
	}
	
	private void backupAndReset(Player player, InventorySyncData syncData) {
		syncData.setBackupInventory(player.getInventory().getContents());
		syncData.setBackupArmor(player.getInventory().getArmorContents());
		syncData.setBackupEnderChest(player.getEnderChest().getContents());
		player.setItemOnCursor(null);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}
	
	public DatabaseInventoryData getData(Player player) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT invSync_inv_main,invSync_inv_armor,invSync_xp,invSync_level,invSync_inv_enderchest FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return new DatabaseInventoryData(rs.getString("invSync_inv_main"), rs.getString("invSync_inv_armor"), rs.getString("invSync_inv_enderchest"), rs.getFloat("invSync_xp"), rs.getInt("invSync_level"));
			}else {
				return null;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private void setInventory(final Player player, DatabaseInventoryData data, InventorySyncData syncData) {
		if(!data.getRawInventory().matches("none")) {
			player.getInventory().setContents(decodeItems(data.getRawInventory()));
		}else {
			player.getInventory().setContents(syncData.getBackupInventory());
		}
		if(!data.getRawArmor().matches("none")) {
			player.getInventory().setArmorContents(decodeItems(data.getRawArmor()));
		}else {
			player.getInventory().setArmorContents(syncData.getBackupArmor());
		}
		if(!data.getRawEnderChestInventory().matches("none")) {
			player.getEnderChest().setContents(decodeItems(data.getRawEnderChestInventory()));
		}else {
			player.getEnderChest().setContents(syncData.getBackupEnderChest());
		}
		player.setExp(data.getXP());
		player.setLevel(data.getLevel());
		player.updateInventory();
	}
	
	public void onDataSaveFunction(Player player, ItemStack[] inventory, ItemStack[] armor, ItemStack[] enderChest) {
		String invS = "";
		String armorS = "";
		String enderChestP = "";
		if(inventory != null) {
			invS = encodeItems(inventory);
		}
		if(armor != null) {
			armorS = encodeItems(armor);
		}
		if(enderChest != null) {
			enderChestP = encodeItems(enderChest);
		}
		setData(player, invS, armorS, enderChestP);
	}
	
	public String encodeItems(ItemStack[] items) {
		if(LotusManager.useProtocolLib) {
			return InventoryUtils.saveModdedStacksData(items);
		}else {
			return InventoryUtils.itemStackArrayToBase64(items);
		}
		
	}
	
	public ItemStack[] decodeItems(String data) {
		if(LotusManager.useProtocolLib) {
			ItemStack[] is = InventoryUtils.restoreModdedStacks(data);
			if(is == null) {
				try {
					is = InventoryUtils.itemStackArrayFromBase64(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return is;
		}else {
			try {
				return InventoryUtils.itemStackArrayFromBase64(data);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	public void setData(Player player, String inventory, String armor, String enderChest) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET invSync_inv_main = ?, invSync_inv_armor = ?, invSync_xp = ?, invSync_level = ?, invSync_inv_enderchest = ? WHERE mcuuid = ?");
			ps.setString(1, inventory);
			ps.setString(2, armor);
			ps.setFloat(3, player.getExp());
			ps.setInt(4, player.getLevel());
			ps.setString(5, enderChest);
			ps.setString(6, player.getUniqueId().toString());
			ps.executeUpdate();
			Main.logger.info("setData has been triggered.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}