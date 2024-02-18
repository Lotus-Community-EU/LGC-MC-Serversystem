//Created by Chris Wille at 10.02.2024
package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

public class TP_Command implements CommandExecutor{
	
	/*
	 * Commands:
	 * - /tp <x> <y> <z>    | Teleports you to coordinates
	 * - /tp <player>       | Teleports you to the player
	 * - /tphere <player>   | Teleports the player to you
	 * - /tpall             | Teleports all players to you
	 */

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(command.getName().equalsIgnoreCase("tp")) {
				if(args.length == 1) {
					//for player
					if(player.hasPermission("lgc.command.tp.player")) {
						if(args[0].length() <= 2 || args[0].length() >= 17) {
							lc.sendMessageReady(player, "global.wrongPlayernamelength");
						}else {
							Player target = Bukkit.getPlayerExact(args[0]);
							if(target == null) {
								lc.sendMessageReady(player, "global.playerOffline");
							}else {
								player.teleport(target.getLocation());
								player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.tp.player").replace("%displayer%", target.getDisplayName()));
							}
						}
					}else {
						lc.noPerm(player, "lgc.command.tp.player");
					}
				}else if(args.length == 3) {
					//for location
					if(player.hasPermission("lgc.command.tp.location")) {
						if(args[0].matches("^[0-9 -]+$") && args[1].matches("^[0-9 -]+$") && args[2].matches("^[0-9 -]+$")) {
							int x = Integer.parseInt(args[0]);
							int y = Integer.parseInt(args[1]);
							int z = Integer.parseInt(args[2]);
							Location location = new Location(player.getWorld(), x, y, z);
							player.teleport(location);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.tp.location").replace("%location_x%", args[0]).replace("%location_y%", args[1]).replace("%location_z%", args[2]));
						}
					}else {
						lc.noPerm(player, "lgc.command.tp.location");
					}
				}else {
					//args
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/tp <Player> §a§lor §7/tp <x> <y> <z>");
				}
			}else if(command.getName().equalsIgnoreCase("tphere")) {
				if(args.length == 1) {
					if(player.hasPermission("lgc.command.tphere")) {
						if(args[0].length() <= 2 || args[0].length() >= 17) {
							lc.sendMessageReady(player, "global.wrongPlayernamelength");
						}else {
							Player target = Bukkit.getPlayerExact(args[0]);
							if(target == null) {
								lc.sendMessageReady(player, "global.playerOffline");
							}else {
								target.teleport(player.getLocation());
								player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.tphere.player").replace("%displayer%", target.getDisplayName()));
							}
						}
					}else {
						lc.noPerm(player, "lgc.command.tphere");
					}
				}else {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/tphere <Player>");
				}
			}else if(command.getName().equalsIgnoreCase("tpall")) {
				if(player.hasPermission("lgc.command.tpall")) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						all.teleport(player.getLocation());
					}
					lc.sendMessageReady(player, "command.tpall");
				}else {
					lc.noPerm(player, "lgc.command.tpall");
				}
			}
		}
		return true;
	}
}