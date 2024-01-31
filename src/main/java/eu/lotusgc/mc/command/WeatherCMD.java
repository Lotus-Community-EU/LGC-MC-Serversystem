package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

@org.bukkit.plugin.java.annotation.command.Command(name="weather")
public class WeatherCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player)sender;
			LotusController lc = new LotusController();
			if(args.length == 0 || args.length >= 3) {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/weather <clear|rain|thunder> [duration]");
			}else if(args.length == 1) {
				String mode = args[0];
				if(mode.equalsIgnoreCase("clear")) {
					if(player.hasPermission("lgc.command.weather")) {
						player.getWorld().setClearWeatherDuration(1800*20);
						lc.sendMessageReady(player, "command.weather.clear.notime");
					}else {
						lc.noPerm(player, "lgc.command.weather");
					}
				}else if(mode.equalsIgnoreCase("rain")) {
					if(player.hasPermission("lgc.command.weather")) {
						player.getWorld().setStorm(true);
						player.getWorld().setWeatherDuration(1800*20);
						lc.sendMessageReady(player, "command.weather.rain.notime");
					}else {
						lc.noPerm(player, "lgc.command.weather");
					}
				}else if(mode.equalsIgnoreCase("thunder")) {
					if(player.hasPermission("lgc.command.weather")) {
						player.getWorld().setThundering(true);
						player.getWorld().setThunderDuration(1800*20);
						lc.sendMessageReady(player, "command.weather.thunder.notime");
					}else {
						lc.noPerm(player, "lgc.command.weather");
					}
				}else {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/weather <clear|rain|thunder> [duration]");
				}
			}else if(args.length == 2) {
				String mode = args[0];
				if(args[1].matches("^[0-9]+$")) {
					int durInTicks = (Integer.parseInt(args[1]) * 20);
					if(mode.equalsIgnoreCase("clear")) {
						if(player.hasPermission("lgc.command.weather")) {
							player.getWorld().setClearWeatherDuration(durInTicks);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.weather.clear.time").replace("%ticks%", String.valueOf(durInTicks)));
						}else {
							lc.noPerm(player, "lgc.command.weather");
						}
					}else if(mode.equalsIgnoreCase("rain")) {
						if(player.hasPermission("lgc.command.weather")) {
							player.getWorld().setStorm(true);
							player.getWorld().setWeatherDuration(durInTicks);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.weather.rain.time").replace("%ticks%", String.valueOf(durInTicks)));
						}else {
							lc.noPerm(player, "lgc.command.weather");
						}
					}else if(mode.equalsIgnoreCase("thunder")) {
						if(player.hasPermission("lgc.command.weather")) {
							player.getWorld().setThundering(true);
							player.getWorld().setThunderDuration(durInTicks);
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.weather.thunder.time").replace("%ticks%", String.valueOf(durInTicks)));
						}else {
							lc.noPerm(player, "lgc.command.weather");
						}
					}else {
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/weather <clear|rain|thunder> [duration]");
					}
				}else {
					//command.weather.onlyNumbers -> Please use only alphanumeric characters (0 - 9)!
					lc.sendMessageReady(player, "command.weather.onlyNumbers");
				}
			}
		}
		return true;
	}

}
