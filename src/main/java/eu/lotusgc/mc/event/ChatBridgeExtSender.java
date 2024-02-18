//Created by Chris Wille at 10.02.2024
package eu.lotusgc.mc.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.ChatBridgeUtils;

public class ChatBridgeExtSender implements Listener{
	
	@EventHandler
	public void onAchieve(PlayerAdvancementDoneEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		AdvancementDisplay ad = event.getAdvancement().getDisplay();
		long timestamp = (System.currentTimeMillis() / 1000);
		if(ad != null) {
			String text = "[<t:" + timestamp + ":f>] ⏫ **" + player.getName() + "** has completed the advancement **" + ad.getTitle() + "**";
			sendPluginMessage(player, lc.getServerName(), text);
		}
	}
	
	@EventHandler
	public void onPlayerLevelup(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		String text = "[<t:" + timestamp + ":f>] ⏫ **" + player.getName() + "** has leveled up to Level **" + event.getNewLevel() + "**.";
		sendPluginMessage(player, lc.getServerName(), text);
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		String text = "[<t:" + timestamp + ":f>] 🚀 **" + player.getName() + "** teleported like a multiverse traveller from **" + event.getFrom().getName() + "** to **" + player.getWorld().getName() + "**.";
		sendPluginMessage(player, lc.getServerName(), text);
	}
	
	static HashMap<UUID, UUID> entityKiller = new HashMap<>();
	static HashMap<UUID, UUID> playerKiller = new HashMap<>();
	
	@EventHandler
	public void onEntitySlay(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(damager.getType() == EntityType.PLAYER) {
			Player p = (Player)damager;
			entityKiller.put(entity.getUniqueId(), p.getUniqueId());
		}
		if(entity.getType() == EntityType.PLAYER) {
			Player p = (Player)entity;
			playerKiller.put(p.getUniqueId(), damager.getUniqueId());
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		Entity entity = event.getEntity();
		if(entityKiller.containsKey(entity.getUniqueId())) {
			Player killer = Bukkit.getPlayer(entityKiller.get(entity.getUniqueId()));
			entityKiller.remove(entity.getUniqueId());
			String text = "[<t:" + timestamp + ":f>] ⚔️ **" + killer.getName() + "** has killed a **" + entity.getName() + "**";
			sendPluginMessage(killer, lc.getServerName(), text);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		String text = "[<t:" + timestamp + ":f>] ☠️ **" + player.getName() + "** died.";
		sendPluginMessage(player, lc.getServerName(), text);
	}
	
	private void sendPluginMessage(Player player, String server, String text) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeUTF(ChatBridgeUtils.tranlateIntoMultiDataString(server, text));
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.sendPluginMessage(Main.main, "lgc:dccb", baos.toByteArray());
	}
}