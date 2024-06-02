package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
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

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.InputType;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Serverdata;

public class JoinLeaveEvent implements Listener{
	
	static HashMap<UUID, Long> timeMap = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		new ScoreboardHandler().setScoreboard(player);
		event.setJoinMessage("§7[§a+§7] " + player.getDisplayName());
		updateOnlineStatus(player.getUniqueId(), true);
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
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		event.setQuitMessage("§7[§c-§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer().getUniqueId(), false);
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
	private void updateOnlineStatus(UUID uuid, boolean status) {
		try {
			PreparedStatement ps;
			if(status) {
				LotusController lc = new LotusController();
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET isOnline = ?, currentLastServer = ? WHERE mcuuid = ?");
				ps.setBoolean(1, status);
				ps.setString(2, lc.getServerName());
				ps.setString(3, uuid.toString());
			}else {
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET isOnline = ? WHERE mcuuid = ?");
				ps.setBoolean(1, status);
				ps.setString(2, uuid.toString());
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
