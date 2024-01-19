package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;

public class JoinLeaveEvent implements Listener{
	
	static HashMap<UUID, Long> timeMap = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage("§7[§a+§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer().getUniqueId(), true);
		timeMap.put(event.getPlayer().getUniqueId(), (System.currentTimeMillis() / 1000));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage("§7[§c-§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer().getUniqueId(), false);
		if(timeMap.containsKey(event.getPlayer().getUniqueId())) {
			long timeStamp = timeMap.get(event.getPlayer().getUniqueId());
			long playtime = ((System.currentTimeMillis() / 1000) - timeStamp);
			long oldPlayTime = getPlaytime(event.getPlayer());
			long newPlayTime = (playtime + oldPlayTime);
			updatePlaytime(event.getPlayer(), newPlayTime);
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
