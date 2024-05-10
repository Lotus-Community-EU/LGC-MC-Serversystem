//Created by Christopher at 09.05.2024
package eu.lotusgc.mc.command;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.misc.Playerdata;

public class GC_CMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		LotusController lc = new LotusController();
		if(!(sender instanceof Player)) {
			Runtime runtime = Runtime.getRuntime();
			DecimalFormat df = new DecimalFormat("#.##");
			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			sender.sendMessage("§7§m================§7[§cServerinfo§7]§m================");
			sender.sendMessage("§7OS: §b" + System.getProperty("os.name"));
			sender.sendMessage("§7OS Arch: §b" + System.getProperty("os.arch"));
			sender.sendMessage("§7Java Vendor: §6" + System.getProperty("java.vendor"));
			sender.sendMessage("§7Java Version: §6" + System.getProperty("java.version"));
			sender.sendMessage("§7RAM: §a" + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + " §8MB §7/ §c" + runtime.totalMemory() / 1048576L + "§8 MB §7(§e" + runtime.freeMemory() / 1048576L + "§8 MB free§7)");
			sender.sendMessage("§7CPU Cores: §d" + runtime.availableProcessors() + " §8Cores");
			sender.sendMessage("§7CPU Load: §d" + df.format(lc.getCpuUsage()) + "§8%");
			sender.sendMessage("§7Server-IP & Port: §a" + Bukkit.getIp() + "§8:§a" + Bukkit.getPort());
			sender.sendMessage("§7Servername & ID: §a" + lc.getServerName() + "§8 / §a" + lc.getServerId());
			sender.sendMessage("§7Server Date & Time: §a" + time.format(new Date()) + " §7- §a " + date.format(new Date()));
			sender.sendMessage("§7Current TPS: §a" + formatDouble());
		}else {
			Player player = (Player)sender;
			if(player.hasPermission("lgc.command.gc")) {
				Runtime runtime = Runtime.getRuntime();
				DecimalFormat df = new DecimalFormat("#.##");
				SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
				String timeZone = lc.getPlayerData(player, Playerdata.TimeZone);
				ZoneId zoneId = ZoneId.ofOffset("GMT", ZoneOffset.of(timeZone));
				SimpleDateFormat pTime = new SimpleDateFormat(lc.getPlayerData(player, Playerdata.CustomTimeFormat));
				pTime.setTimeZone(TimeZone.getTimeZone(Objects.requireNonNullElse(zoneId.getId(), "UTC")));
				SimpleDateFormat pDate = new SimpleDateFormat(lc.getPlayerData(player, Playerdata.CustomDateFormat));
				pDate.setTimeZone(TimeZone.getTimeZone(Objects.requireNonNullElse(zoneId.getId(), "UTC")));
				
				
				player.sendMessage("§7§m================§7[§cServerinfo§7]§m================");
				player.sendMessage("§7OS: §b" + System.getProperty("os.name"));
				player.sendMessage("§7OS Arch: §b" + System.getProperty("os.arch"));
				player.sendMessage("§7Java Vendor: §6" + System.getProperty("java.vendor"));
				player.sendMessage("§7Java Version: §6" + System.getProperty("java.version"));
				player.sendMessage("§7RAM: §a" + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + " §8MB §7/ §c" + runtime.totalMemory() / 1048576L + "§8 MB §7(§e" + runtime.freeMemory() / 1048576L + "§8 MB free§7)");
				player.sendMessage("§7CPU Cores: §d" + runtime.availableProcessors() + " §8Cores");
				player.sendMessage("§7CPU Load: §d" + df.format(lc.getCpuUsage()) + "§8%");
				player.sendMessage("§7Server-IP & Port: §a" + Bukkit.getIp() + "§8:§a" + Bukkit.getPort());
				player.sendMessage("§7Servername & ID: §a" + lc.getServerName() + "§8 / §a" + lc.getServerId());
				player.sendMessage("§7Server Date & Time: §a" + time.format(new Date()) + " §7- §a " + date.format(new Date()) + " §7- §a UTC");
				player.sendMessage("§7User Date & Time: §a" + pTime.format(new Date()) + " §7- §a " + pDate.format(new Date()) + " §7- §a" + timeZone);
				player.sendMessage("§7Current TPS: §a" + formatDouble());
			}else {
				lc.noPerm(player, "lgc.command.gc");
			}
		}
		return false;
	}
	
	private static Field recentTps;
	private static Object minecraftServer;
	
	private static double[] getRecentTPS() throws Throwable {
		if(minecraftServer == null) {
			Server server = Bukkit.getServer();
			Field consoleField = server.getClass().getDeclaredField("console");
			consoleField.setAccessible(true);
			minecraftServer = consoleField.get(server);
		}
		if(recentTps == null) {
			recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
			recentTps.setAccessible(true);
		}
		return (double[]) recentTps.get(minecraftServer);
	}
	
	static String formatDouble() {
		double tps = 0;
		try {
			tps = getRecentTPS()[0];
		} catch (Throwable e) {
			e.printStackTrace();
		}
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(tps);
	}
}