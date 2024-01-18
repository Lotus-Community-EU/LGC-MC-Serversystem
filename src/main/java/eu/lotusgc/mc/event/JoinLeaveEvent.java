package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.lotusgc.mc.misc.MySQL;

public class JoinLeaveEvent implements Listener{
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage("§7[§a+§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer().getUniqueId(), true);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage("§7[§c-§7] " + event.getPlayer().getDisplayName());
		updateOnlineStatus(event.getPlayer().getUniqueId(), false);
	}
	
	//update the online status (true for online, false for offline)
		private void updateOnlineStatus(UUID uuid, boolean status) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET isOnline = ? WHERE mcuuid = ?");
				ps.setBoolean(1, status);
				ps.setString(2, uuid.toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

}
