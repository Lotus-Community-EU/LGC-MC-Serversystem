package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

public class GamemodeCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player)sender;
			LotusController lc = new LotusController();
			if(args.length == 0) {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/gm <0|1|2|3> [Player]");
			}else if(args.length == 1) {
				if(player.hasPermission("lgc.command.gamemode.self")) {
					String mode = args[0];
					switch(mode) {
					case "0": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Survival")); 
						player.setGameMode(GameMode.SURVIVAL);
						break;
					case "1": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Creative")); 
						player.setGameMode(GameMode.CREATIVE);
						break;
					case "2": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Adventure")); 
						player.setGameMode(GameMode.ADVENTURE);
						break;
					case "3": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Spectator")); 
						player.setGameMode(GameMode.SPECTATOR);
						break;
					case "survival": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Survival")); 
						player.setGameMode(GameMode.SURVIVAL);
						break;
					case "creative": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Creative")); 
						player.setGameMode(GameMode.CREATIVE);
						break;
					case "adventure": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Adventure")); 
						player.setGameMode(GameMode.ADVENTURE);
						break;
					case "spectator": 
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.self").replace("%newGamemode%", "Spectator")); 
						player.setGameMode(GameMode.SPECTATOR);
						break;
					}
				}else {
					lc.noPerm(player, "lgc.command.gamemode.self");
				}
			}else if (args.length == 2) {
				if(player.hasPermission("lgc.command.gamemode.other")) {
					String mode = args[0];
					Player playerTarget = Bukkit.getPlayerExact(args[1]);
					if(playerTarget != null) {
						switch(mode) {
						case "0": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Survival").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Survival").replace("%displayer%", playerTarget.getDisplayName()));
							player.setGameMode(GameMode.SURVIVAL);
							break;
						case "1": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Creative").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Creative").replace("%displayer%", playerTarget.getDisplayName())); 
							player.setGameMode(GameMode.CREATIVE);
							break;
						case "2": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Adventure").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Adventure").replace("%displayer%", playerTarget.getDisplayName())); 
							player.setGameMode(GameMode.ADVENTURE);
							break;
						case "3": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Spectator").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Spectator").replace("%displayer%", playerTarget.getDisplayName())); 
							player.setGameMode(GameMode.SPECTATOR);
							break;
						case "survival": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Survival").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Survival").replace("%displayer%", playerTarget.getDisplayName())); 
							player.setGameMode(GameMode.SURVIVAL);
							break;
						case "creative": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Creative").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Creative").replace("%displayer%", playerTarget.getDisplayName()));
							player.setGameMode(GameMode.CREATIVE);
							break;
						case "adventure": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Adventure").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Adventure").replace("%displayer%", playerTarget.getDisplayName())); 
							player.setGameMode(GameMode.ADVENTURE);
							break;
						case "spectator": 
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.gamemode.other.self").replace("%newGamemode%", "Spectator").replace("%displayer%", playerTarget.getDisplayName()));
							playerTarget.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(playerTarget, "command.gamemode.other.recipient").replace("%newGamemode%", "Spectator").replace("%displayer%", playerTarget.getDisplayName()));
							player.setGameMode(GameMode.SPECTATOR);
							break;
						}
					}else {
						lc.sendMessageReady(player, "global.playerOffline");
					}
				}else {
					lc.noPerm(player, "lgc.command.gamemode.other");
				}
			}
		}
		return true;
	}
}