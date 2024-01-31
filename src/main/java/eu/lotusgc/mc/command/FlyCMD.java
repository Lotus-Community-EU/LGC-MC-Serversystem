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
import org.bukkit.event.player.PlayerQuitEvent;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

@org.bukkit.plugin.java.annotation.command.Command(name="fly")
public class FlyCMD implements CommandExecutor, Listener {
	
	public static List<UUID> flyList = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(args.length == 0) {
				if(player.hasPermission("lgc.command.fly.self")) {
					if(flyList.contains(player.getUniqueId())) {
						//remove
						flyList.remove(player.getUniqueId());
						player.setAllowFlight(false);
						lc.sendMessageReady(player, "command.fly.self.disabled"); //You can't fly anymore.
					}else {
						//add
						flyList.add(player.getUniqueId());
						player.setAllowFlight(true);
						lc.sendMessageReady(player, "command.fly.self.enabled"); //You can fly now.
					}
				}else {
					lc.noPerm(player, "lgc.command.fly.self");
				}
			}else if(args.length == 1) {
				if(player.hasPermission("lgc.command.fly.other")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(target == null) {
						lc.sendMessageReady(player, "global.playerOffline");
					}else {
						if(flyList.contains(target.getUniqueId())) {
							//remove target
							flyList.remove(target.getUniqueId());
							target.setAllowFlight(false);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.fly.other.self.disable").replace("%displayer%", target.getDisplayName()));
							target.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(target, "command.fly.other.target.disable").replace("%displayer%", player.getDisplayName()));
						}else {
							//add
							flyList.add(target.getUniqueId());
							target.setAllowFlight(true);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.fly.other.self.enable").replace("%displayer%", target.getDisplayName()));
							target.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(target, "command.fly.other.target.enable").replace("%displayer%", player.getDisplayName()));
						}
					}
				}else {
					lc.noPerm(player, "lgc.command.fly.other");
				}
			}
		}
		return true;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(flyList.contains(event.getPlayer().getUniqueId())) flyList.remove(event.getPlayer().getUniqueId());
	}
}