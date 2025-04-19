//Created by Maurice H. at 19.04.2025
package eu.lotusgc.mc.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Prefix;

public class OnlinemapCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player)sender;
			String serverName = new LotusController().getServerName();
			Location location = player.getLocation();
			try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mapPort FROM mc_serverstats WHERE servername = ?")){
				ps.setString(1, serverName);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					int mapPort = rs.getInt("mapPort");
					String url = "http://map.lotusgaming.community:" + mapPort + "/?worldname=" + location.getWorld().getName() + "&mapname=flat&zoom=6" + "&x=" + location.getBlockX() + "&y=" + location.getBlockY() + "&z=" + location.getBlockZ();
					player.sendMessage(new LotusController().getPrefix(Prefix.MAIN) + "§7Map-URL: §a" + url);
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}