package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

@org.bukkit.plugin.java.annotation.command.Command(name="time")
public class TimeCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(args.length == 2) {
				if(player.hasPermission("lgc.setTime")) {
					String mode = args[0];
					if(mode.equalsIgnoreCase("set")) {
						long time = translateTime(args[1]);
						if(time == -1) {
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/time <set|add|remove> <time (or day/noon/evening/night)>");
						}else {
							player.getWorld().setTime(time);
							// command.time.set -> %worldname% worldname, %timeTicks% getTime(), %timeDigital% translated getFullTime()
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.time.set").replace("%worldname%", player.getWorld().getName()).replace("%timeTicks%", String.valueOf(player.getWorld().getTime())).replace("%timeDigital%", parseTimeWorld(player.getWorld().getTime())));
						}
					}else if(mode.equalsIgnoreCase("add")) {
						if(args[1].matches("^[0-9]+$")) {
							int time = Integer.parseInt(args[1]);
							long oldTime = player.getWorld().getTime();
							long newTime = (oldTime + time);
							player.getWorld().setTime(newTime);
							// command.time.add
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.time.add").replace("%worldname%", player.getWorld().getName()).replace("%timeTicks%", String.valueOf(player.getWorld().getTime())).replace("%timeDigital%", parseTimeWorld(player.getWorld().getTime())));
						}else {
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/time <set|add|remove> <time (or day/noon/evening/night)>");
						}
					}else if(mode.equalsIgnoreCase("remove")) {
						if(args[1].matches("^[0-9]+$")) {
							int time = Integer.parseInt(args[1]);
							long oldTime = player.getWorld().getTime();
							long newTime = (oldTime - time);
							player.getWorld().setTime(newTime);
							// command.time.remove
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.time.remove").replace("%worldname%", player.getWorld().getName()).replace("%timeTicks%", String.valueOf(player.getWorld().getTime())).replace("%timeDigital%", parseTimeWorld(player.getWorld().getTime())));
						}else {
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/time <set|add|remove> <time (or day/noon/evening/night)>");
						}
					}else {
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/time <set|add|remove> <time (or day/noon/evening/night)>");
					}
				}else {
					lc.noPerm(player, "lgc.setTime");
				}
			}else {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/time <set|add|remove> <time (or day/noon/evening/night)>");
			}
		}
		return false;
	}
	
	long translateTime(String input) {
		if(input.equalsIgnoreCase("day")) {
			return 0;
		}else if(input.equalsIgnoreCase("noon")) {
			return 6000;
		}else if(input.equalsIgnoreCase("evening")) {
			return 13000;
		}else if(input.equalsIgnoreCase("night")) {
			return 20000;
		}else {
			if(input.matches("^[0-9]+$")) {
				return Long.valueOf(input);
			}else {
				return -1;
			}
		}
	}
	
	private String parseTimeWorld(long time) {
		long gameTime = time;
		long hours = gameTime / 1000 + 6;
		long minutes = (gameTime % 1000) * 60 / 1000;
		String ampm = "AM";
		if(hours >= 12) {
			hours -= 12; ampm = "PM";
		}
		if(hours >= 12) {
			hours -= 12; ampm = "AM";
		}
		if(hours == 0) hours = 12;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());
		return hours + ":" + mm + " " + ampm;
	}
}