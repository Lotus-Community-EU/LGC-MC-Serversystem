//Created by Maurice H. at 27.04.2025
package eu.lotusgc.mc.event;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class NotifyBiomeChange implements Listener {
	
	static HashMap<Player, String> biomeMap = new HashMap<>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String biome = player.getLocation().getBlock().getBiome().toString().replace("_", " ");
		if (biome != null) {
			if (biomeMap.containsKey(player)) {
				if (!biomeMap.get(player).equals(biome)) {
					biomeMap.put(player, biome);
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§aYou have entered a new biome: §6" + upperCaseWords(biome)));
				}
			} else {
				biomeMap.put(player, biome);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(biomeMap.containsKey(player)) {
			biomeMap.remove(player);
		}
	}
	
	String upperCaseWords(String input) {
		String words[] = input.replaceAll("\\s+",  " ").trim().split(" ");
		String newSentence = "";
		for(String word : words) {
			for(int i = 0; i < word.length(); i++) {
				newSentence = newSentence + ((i == 0) ? word.substring(i, i+1).toUpperCase() :
					(i != word.length() - 1) ? word.substring(i, i+ 1).toLowerCase() : word.substring(i, i+1).toLowerCase().toLowerCase() + " ");
			}
		}
		return newSentence;
	}
}