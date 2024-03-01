package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import eu.lotusgc.mc.misc.MySQL;

public class KillStats implements Listener{
	
	
	/*
	 * if player kills player -> count hasKilledEntity + 1 and gotKilledByEntity + 1
	 * if player kills entity -> count hasKilledEntity + 1
	 * if entity kills player -> count gotKilledByEntity + 1
	 * 
	 */
	
	//Value = Damager, Key = Victim
	static HashMap<UUID, UUID> dmg = new HashMap<>();
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity vic = event.getEntity();
		dmg.put(vic.getUniqueId(), damager.getUniqueId());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType() == EntityType.PLAYER) {
			//should be Player killed by entity or player
			Player player = (Player) event.getEntity();
			if(dmg.containsKey(player.getUniqueId())) {
				Entity ent = Bukkit.getEntity(dmg.get(player.getUniqueId()));
				if(ent.getType() == EntityType.PLAYER) {
					Player player2 = (Player) ent;
					int kills1 = getKillStats(player, KillType.PlayerKilledByPlayer);
					setKillStats(player, KillType.PlayerKilledByPlayer, (kills1 + 1));
					
					int kills2 = getKillStats(player2, KillType.PlayerKillsPlayer);
					setKillStats(player2, KillType.PlayerKillsPlayer, (kills2 + 1));
				}else {
					int kills = getKillStats(player, KillType.PlayerKilledByEntity);
					setKillStats(player, KillType.PlayerKilledByEntity, (kills + 1));
				}
			}
		}else if(event.getEntityType() != EntityType.PLAYER) {
			//should be Entity killed by player
			LivingEntity entity = event.getEntity();
			if(dmg.containsKey(entity.getUniqueId())) {
				Entity damager = Bukkit.getEntity(dmg.get(entity.getUniqueId()));
				if(damager.getType() == EntityType.PLAYER) {
					Player killer = entity.getKiller();
					int kills = getKillStats(killer, KillType.PlayerKillsEntity);
					setKillStats(killer, KillType.PlayerKillsEntity, (kills + 1));
				}
			}
			
		}
	}
	
	void setKillStats(Player player, KillType type, int newKills) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET " + type.getColumnName() + " = ? WHERE mcuuid = ?");
			ps.setInt(1, newKills);
			ps.setString(2, player.getUniqueId().toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	int getKillStats(Player player, KillType type){
		int statistics = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT " + type.getColumnName() + " FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				statistics = rs.getInt(type.getColumnName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statistics;
	}
	
	enum KillType {
		PlayerKillsPlayer("playerKillsPlayer"),
		PlayerKillsEntity("playerKillsEntity"),
		PlayerKilledByPlayer("playerKilledByPlayer"),
		PlayerKilledByEntity("playerKilledByEntity");
		
		public String databaseColName;
		
		KillType(String databaseColName){
			this.databaseColName = databaseColName;
		}
		
		public String getColumnName() {
			return databaseColName;
		}
	}

}
