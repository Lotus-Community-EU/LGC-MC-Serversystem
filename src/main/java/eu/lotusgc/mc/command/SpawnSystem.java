//Created by Chris Wille at 10.02.2024
package eu.lotusgc.mc.command;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.LotusManager;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

public class SpawnSystem implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(args.length == 1) {
				if(player.hasPermission("lgc.spawn.admin")) {
					setSpawn(player.getLocation(), player, args[0]);
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.spawn.set").replace("%spawn%", args[0]));
				}else {
					lc.noPerm(player, "lgc.spawn.admin");
				}
			}else {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + " §7/setspawn <name>");
			}
		}
		return true;
	}
	
	void setSpawn(Location location, Player player, String use) {
		File config = LotusManager.mainConfig;
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
		
		if(location != null) {
			cfg.set("Spawn." + use + ".World", location.getWorld().getName());
			cfg.set("Spawn." + use + ".X", location.getX());
			cfg.set("Spawn." + use + ".Y", location.getY());
			cfg.set("Spawn." + use + ".Z", location.getZ());
			cfg.set("Spawn." + use + ".YAW", location.getYaw());
			cfg.set("Spawn." + use + ".PITCH", location.getPitch());
			cfg.set("Spawn." + use + ".Timestamp.Set", System.currentTimeMillis());
		}
		if(player != null) {
			cfg.set("Spawn." + use + ".Setter", player.getUniqueId().toString());
		}
		try {
			cfg.save(config);
			Main.logger.info("Spawn has been updated by " + player.getName() + " with the attribute: spawnType=" + use);
		} catch (IOException e) {
			Main.logger.severe("Attempting to save spawn, but errored: " + e.getMessage());
		}
	}
	
	public static Location getSpawn(String use) {
		File config = LotusManager.mainConfig;
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
		
		Location location = new Location(Bukkit.getWorld(cfg.getString("Spawn." + use + ".World")), cfg.getDouble("Spawn." + use + ".X"), cfg.getDouble("Spawn." + use + ".Y"), cfg.getDouble("Spawn." + use + ".Z"), (float)cfg.getDouble("Spawn." + use + ".YAW"), (float)cfg.getDouble("Spawn." + use + ".PITCH"));
		return location;
	}
}