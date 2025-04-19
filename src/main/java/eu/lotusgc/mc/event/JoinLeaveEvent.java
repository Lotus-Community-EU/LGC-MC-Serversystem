package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import eu.lotusgc.mc.command.SpawnSystem;
import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.InputType;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Serverdata;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;

public class JoinLeaveEvent implements Listener{
	
	static HashMap<UUID, Long> timeMap = new HashMap<>();
	static HashMap<String, String> nameHM = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		new ScoreboardHandler().setScoreboard(player);
		event.setJoinMessage("§7[§a+§7] " + player.getDisplayName());
		updateOnlineStatus(player, true);
		timeMap.put(player.getUniqueId(), (System.currentTimeMillis() / 1000));
		boolean whitelistedServer = lc.translateBoolean(lc.getServerData(lc.getServerName(), Serverdata.AllowPlayerInventorySync, InputType.Servername));
		if(whitelistedServer) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.main, new Runnable() {
				@Override
				public void run() {
					lc.onInvSyncJoinFunction(player);
				}
			}, 5L);
		}
		if(!player.hasPlayedBefore()) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.main, new Runnable() {
				@Override
				public void run() {
					player.teleport(SpawnSystem.getSpawn("main"));
				}
			}, 2L);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		event.setQuitMessage("§7[§c-§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer(), false);
		if(timeMap.containsKey(event.getPlayer().getUniqueId())) {
			long timeStamp = timeMap.get(event.getPlayer().getUniqueId());
			long playtime = ((System.currentTimeMillis() / 1000) - timeStamp);
			long oldPlayTime = getPlaytime(event.getPlayer());
			long newPlayTime = (playtime + oldPlayTime);
			updatePlaytime(event.getPlayer(), newPlayTime);
		}
		boolean whitelistedServer = lc.translateBoolean(lc.getServerData(lc.getServerName(), Serverdata.AllowPlayerInventorySync, InputType.Servername));
		if(whitelistedServer) {
			ItemStack[] inv = player.getInventory().getContents();
			ItemStack[] armor = player.getInventory().getArmorContents();
			ItemStack[] enderChest = player.getEnderChest().getStorageContents();
			lc.onDataSaveFunction(player, inv, armor, enderChest);
		}
	}
	
	//update the online status (true for online, false for offline)
	private void updateOnlineStatus(Player player, boolean status) {
		try {
			PreparedStatement ps;
			if(status) {
				LotusController lc = new LotusController();
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET isOnline = ?, currentLastServer = ?, playerGroup = ? WHERE mcuuid = ?");
				ps.setBoolean(1, status);
				ps.setString(2, lc.getServerName());
				ps.setString(3, retGroup(player));
				ps.setString(4, player.getUniqueId().toString());
			}else {
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET isOnline = ? WHERE mcuuid = ?");
				ps.setBoolean(1, status);
				ps.setString(2, player.getUniqueId().toString());
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private int getPlaytime(Player player) {
		String playtime = new LotusController().getPlayerData(player, Playerdata.Playtime);
		if(playtime.matches("^[-0-9]+$")) {
			return Integer.parseInt(playtime);
		}else {
			return -1;
		}
	}

	private void updatePlaytime(Player player, long newPlaytime) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET playTime = ? WHERE mcuuid = ?");
			ps.setLong(1, newPlaytime);
			ps.setString(2, player.getUniqueId().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String retGroup(Player player) {
		String group = "";
		UserManager um = Main.luckPerms.getUserManager();
		User user = um.getUser(player.getName());
		group = returnPrefix(user.getPrimaryGroup());
		return group;
	}
	
	public static void initRoles() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_ranks");
			ResultSet rs = ps.executeQuery();
			nameHM.clear();
			int count = 0;
			while(rs.next()) {
				count++;
				nameHM.put(rs.getString("ingame_id"), rs.getString("name"));
			}
			Main.logger.info("Downloaded " + count + " roles for the Roleupdater. | Source: JoinEvent#initRoles();");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String returnPrefix(String roleId) {
		String toReturn = "";
		if(nameHM.containsKey(roleId)) {
			toReturn = nameHM.get(roleId);
		}else {
			toReturn = "Player";
		}
		return toReturn;
	}
	
}
