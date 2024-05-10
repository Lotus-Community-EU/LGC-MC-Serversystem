package eu.lotusgc.mc.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;

public class AfKCMD implements Listener, CommandExecutor{
	
	public static List<UUID> afkList = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(afkList.contains(player.getUniqueId())) {
				afkList.remove(player.getUniqueId());
				lc.sendMessageReady(player, "command.afk.remove");
			}else {
				afkList.add(player.getUniqueId());
				lc.sendMessageReady(player, "command.afk.add");
			}
		}
		return true;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		LotusController lc = new LotusController();
		if(afkList.contains(event.getPlayer().getUniqueId())) {
			afkList.remove(event.getPlayer().getUniqueId());
			lc.sendMessageReady(event.getPlayer(), "command.afk.remove"); //You are not AFK anymore.
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		LotusController lc = new LotusController();
		if(afkList.contains(event.getPlayer().getUniqueId())) {
			afkList.remove(event.getPlayer().getUniqueId());
			lc.sendMessageReady(event.getPlayer(), "command.afk.remove"); //You are not AFK anymore.
		}
	}
}