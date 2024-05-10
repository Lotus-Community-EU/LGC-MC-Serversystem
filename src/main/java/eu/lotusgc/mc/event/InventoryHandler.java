package eu.lotusgc.mc.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import eu.lotusgc.mc.command.SpawnSystem;
import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.InputType;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Prefix;
import eu.lotusgc.mc.misc.Serverdata;

public class InventoryHandler implements Listener{
	
	public static String navi_title = "§aNavigator";
	public static String navi_spawn = "§6Spawn";
	
	public static String language_title = "§6Languages";
	
	public static String userProfile_title = "§bProfile Settings";
	static String userProfile_resetProfile = "§4Reset Profile";
	static String userProfile_ownStats = "%player%§7's Profile";
	
	static String wt_title = "§2World§aTeleporter";
	static String wt_s_free = "§aFreebuild";
	static String wt_s_plot = "§aPlotworld";
	static String wt_f_ow = "§aOverworld";
	static String wt_f_nt = "§cNether";
	static String wt_f_te = "§8The End";
	
	public static void setNavigatorInventory(Player player) {
		Inventory mainInventory = Bukkit.createInventory(null, 3*9, navi_title);
		LotusController lc = new LotusController();
		for(int i = 0; i < 27; i++) {
			mainInventory.setItem(i, lc.defItem(Material.LIME_STAINED_GLASS_PANE, "§0", 1));
		}
		mainInventory.setItem(2, lc.naviServerItem(Material.RED_BED, "Gameslobby"));
		mainInventory.setItem(6, lc.naviServerItem(Material.NETHERITE_AXE, "Survival"));
		mainInventory.setItem(10, lc.naviServerItem(Material.GRASS_BLOCK, "SkyBlock"));
		if(lc.getServerName().equalsIgnoreCase("Survival") || lc.getServerName().equalsIgnoreCase("Farmserver")) {
			mainInventory.setItem(12, lc.defItem(Material.RECOVERY_COMPASS, wt_title, 1));
			mainInventory.setItem(14, lc.defItem(Material.EMERALD, navi_spawn, 1));
		}else {
			mainInventory.setItem(13, lc.defItem(Material.EMERALD, navi_spawn, 1));
		}
		mainInventory.setItem(16, lc.naviServerItem(Material.GOLDEN_HOE, "Farmserver"));
		mainInventory.setItem(20, lc.naviServerItem(Material.WOODEN_AXE, "Staffserver"));
		mainInventory.setItem(24, lc.naviServerItem(Material.DIAMOND_PICKAXE, "Creative"));
		player.openInventory(mainInventory);
	}
	
	public static void setWorldTPInventory(Player player) {
		Inventory worldtp = Bukkit.createInventory(null, 1*9, wt_title);
		LotusController lc = new LotusController();
		if(lc.getServerName().equalsIgnoreCase("Survival")) {
			worldtp.setItem(2, lc.wt_Item(Material.GRASS_BLOCK, wt_s_free, Bukkit.getWorld("world")));
			worldtp.setItem(6, lc.wt_Item(Material.GRASS_BLOCK, wt_s_plot, Bukkit.getWorld("plotworld")));
		}else if(lc.getServerName().equalsIgnoreCase("Farmserver")) {
			worldtp.setItem(2, lc.wt_Item(Material.GRASS_BLOCK, wt_f_ow, Bukkit.getWorld("world")));
			worldtp.setItem(4, lc.wt_Item(Material.NETHERRACK, wt_f_nt, Bukkit.getWorld("world_nether")));
			worldtp.setItem(6, lc.wt_Item(Material.GRASS_BLOCK, wt_f_te, Bukkit.getWorld("world_the_end")));
		}
		player.openInventory(worldtp);
	}
	
	public static void profileSettings(Player player) {
		Inventory mainInventory = Bukkit.createInventory(null, 9*3, userProfile_title);
		LotusController lc = new LotusController();
		mainInventory.setItem(26, lc.defItem(Material.BOOK, language_title, 1));
		player.openInventory(mainInventory);
	}
	
	public static void setLanguageInventory(Player player) {
		Inventory mainInventory = Bukkit.createInventory(null, 9*3, language_title);
		LotusController lc = new LotusController();
		int slot = 0;
		String playerLang = lc.getPlayerData(player, Playerdata.Language);
		for(String string : lc.getAvailableLanguages()) {
			String fancyName = langs.getOrDefault(string, "Error!");
			if(string.equalsIgnoreCase(playerLang)) {
				mainInventory.setItem(slot, lc.loreItem(Material.GREEN_CONCRETE_POWDER, 1, "§6" + fancyName, "§7Language is:", "§7» §a" + string));
			}else {
				mainInventory.setItem(slot, lc.loreItem(Material.GRAY_CONCRETE_POWDER, 1, "§a" + fancyName, "§7Language is:", "§7» §a" + string));
			}
			slot++;
		}
		player.openInventory(mainInventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem() == null) return;
		if(event.getView().getTitle().equalsIgnoreCase(navi_title)) {
			LotusController lc = new LotusController();
			event.setCancelled(true);
			if(event.getCurrentItem() == null && event.getCurrentItem().getItemMeta() == null) return;
			String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
			HashMap<String, String> fancyNames = getServerFancynames();
			if(fancyNames.containsKey(itemName)) {
				String bungeeName = fancyNames.get(itemName);
				if(itemName.equalsIgnoreCase("staffserver")) {
					if(player.hasPermission("lgc.joinStaffserver")) {
						if(lc.translateBoolean(lc.getServerData(bungeeName, Serverdata.OnlineStatus, InputType.BungeeKey))){
							if(lc.translateBoolean(lc.getServerData(bungeeName, Serverdata.LockedStatus, InputType.BungeeKey))) {
								if(player.hasPermission("lgc.bypassServerlock")) {
									sendPlayerToServer(player, itemName, bungeeName, lc);
								}else {
									Main.logger.info(player.getName() + " tried to join a locked server.");
								}
							}else {
								sendPlayerToServer(player, itemName, bungeeName, lc);
							}
						}
					}else {
						lc.noPerm(player, "lgc.joinStaffserver");
					}
				}else {
					if(lc.translateBoolean(lc.getServerData(bungeeName, Serverdata.OnlineStatus, InputType.BungeeKey))){
						if(lc.translateBoolean(lc.getServerData(bungeeName, Serverdata.LockedStatus, InputType.BungeeKey))) {
							if(player.hasPermission("lgc.bypassServerlock")) {
								sendPlayerToServer(player, itemName, bungeeName, lc);
							}else {
								Main.logger.info(player.getName() + " tried to join a locked server.");
							}
						}else {
							sendPlayerToServer(player, itemName, bungeeName, lc);
						}
					}
				}
			}else {
				if(itemName.equalsIgnoreCase(navi_spawn)) {
					Location spawn = SpawnSystem.getSpawn("mainSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}else if(itemName.equalsIgnoreCase(wt_title)) {
					player.closeInventory();
					setWorldTPInventory(player);
				}
			}
		}else if(event.getView().getTitle().equalsIgnoreCase(language_title)) {
			event.setCancelled(true);
			LotusController lc = new LotusController();
			if(event.getCurrentItem() == null && event.getCurrentItem().getItemMeta() == null) return;
			String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
			itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1)).substring(2);
			if(findAndUpdatePlayerLanguage(player, itemName)) {
				//Updated language to %language% successfully!
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "event.languageInventory.success").replace("%language%", itemName));
			}else {
				//Error whilst updating to language %language%
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "event.languageInventory.error").replace("%language%", itemName));
			}
		}else if(event.getView().getTitle().equalsIgnoreCase(wt_title)) {
			event.setCancelled(true);
			LotusController lc = new LotusController();
			if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
			String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
			if(lc.getServerName().equals("Farmserver")) {
				if(itemName.equals(wt_f_ow)) {
					Location spawn = SpawnSystem.getSpawn("mainSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}else if(itemName.equals(wt_f_nt)) {
					Location spawn = SpawnSystem.getSpawn("netherSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}else if(itemName.equals(wt_f_te)) {
					Location spawn = SpawnSystem.getSpawn("theendSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}
			}else if(lc.getServerName().equals("Survival")) {
				if(itemName.equals(wt_s_free)) {
					Location spawn = SpawnSystem.getSpawn("freebuildSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}else if(itemName.equals(wt_s_plot)) {
					Location spawn = SpawnSystem.getSpawn("plotworldSpawn");
					player.closeInventory();
					player.teleport(spawn);
				}
			}
		}else if(event.getView().getTitle().equalsIgnoreCase(userProfile_title)) {
			event.setCancelled(true);
			if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
			String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
			if(itemName.equals(language_title)) {
				event.setCancelled(true);
				player.closeInventory();
				setLanguageInventory(player);
			}
		}
	}
	
	private void sendPlayerToServer(Player player, String fancyName, String destinationServer, LotusController lc) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeUTF("Connect");
			dos.writeUTF(destinationServer);
			player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "event.navigator.sendPlayer.success"));
			Main.logger.info(player.getName() + " has been sent to " + destinationServer + " successfully.");
		} catch (IOException e) {
			Main.logger.severe(player.getName() + " attempted to be sent to " + destinationServer + " but failed!");
			e.printStackTrace();
		}
		player.sendPluginMessage(Main.main, "BungeeCord", baos.toByteArray());
	}
	
	private static HashMap<String, String> servers = new HashMap<>();
	private static HashMap<String, String> langs = new HashMap<>();
	
	public static void loadServer() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT displayname,bungeeKey FROM mc_serverstats");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				servers.put(rs.getString("displayname"), rs.getString("bungeeKey"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			LotusController lc = new LotusController();
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_translations WHERE path = ?");
			ps.setString(1, "mcinternal.language");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				for(String string : lc.getAvailableLanguages()) {
					langs.put(string, rs.getString(string));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean findAndUpdatePlayerLanguage(Player player, String newLanguage) {
		boolean success = false;
		if(langs.containsKey(newLanguage)) {
			LotusController.playerLanguages.put(player.getUniqueId().toString(), newLanguage);
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET language = ? WHERE mcuuid = ?");
				ps.setString(1, newLanguage);
				ps.setString(2, player.getUniqueId().toString());
				ps.executeUpdate();
				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			success = false;
		}
		return success;
	}
	
	private HashMap<String, String> getServerFancynames(){
		return servers;
	}

}