package eu.lotusgc.mc.command;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Playerdata;
import eu.lotusgc.mc.misc.Prefix;

public class Homesystem implements CommandExecutor{
	
	/*
	 * Commands:
	 * - /sethome <homename (mArgs)> | Sets the home, e.g. /sethome my house (case insensitive)
	 * - /delhome <homename (mArgs)> | Removes the home (Cannot be undone)
	 * - /listhomes | List all homes on the current server you are on (Is not global!)
	 * - /home <homename (mArgs)> | Teleports you to the specified home or errors.
	 * 
	 * For a later time:
	 * - /home-admin home <Player> <homename (mArgs)>
	 * - /home-admin delhome <Player> <homename (mArgs)>
	 * - /home-admin un/block <Player> <time> [Reason (only on block)]
	 * 
	 * Permissions:
	 * - lgc.useHomesystem | General Permission
	 * - lgc.bypassHomeLimit | Permission to bypass Homelimit (6 Homes per Server)
	 */
	{
		maxHomes = getMaxHomes();
	}
	
	static File homes = new File("plugins/LotusGaming/homes.yml");
	static int maxHomes = 0;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(command.getName().equalsIgnoreCase("sethome")) {
				if(player.hasPermission("lgc.useHomesystem")) {
					if(args.length == 0) {
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/sethome <Homename>");
					}else {
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}
						String text = sb.toString();
						addHome(player, text, lc.getServerName());
					}
				}else {
					lc.noPerm(player, "lgc.useHomesystem");
				}
			}else if(command.getName().equalsIgnoreCase("delhome")) {
				if(player.hasPermission("lgc.useHomesystem")) {
					if(args.length == 0) {
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/delhome <Homename>");
					}else {
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}
						String text = sb.toString();
						deleteHome(player, text, lc.getServerName());
					}
				}else {
					lc.noPerm(player, "lgc.useHomesystem");
				}
			}else if(command.getName().equalsIgnoreCase("listhomes")) {
				if(player.hasPermission("lgc.useHomesystem")) {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_homes WHERE mcuuid = ? AND server = ?");
						ps.setString(1, player.getUniqueId().toString());
						ps.setString(2, lc.getServerName());
						ResultSet rs = ps.executeQuery();
						int homeCount = 0;
						SimpleDateFormat sdf = new SimpleDateFormat(lc.getPlayerData(player, Playerdata.CustomTimeFormat) + " " + lc.getPlayerData(player, Playerdata.CustomDateFormat));
						while(rs.next()) {
							homeCount++;
							player.sendMessage("§a" + homeCount + "§7.) §6" + rs.getString("name") + "§7 - §9" + sdf.format(new Date(rs.getLong("setAt"))));
						}
						// command.listhomes.success -> You have in total %homecount% homes.
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.listhomes.success").replace("%homecount%", String.valueOf(homeCount)));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else {
					lc.noPerm(player, "lgc.useHomesystem");
				}
			}else if(command.getName().equalsIgnoreCase("home")) {
				if(player.hasPermission("lgc.useHomesystem")) {
					if(args.length == 0) {
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/home <Homename>");
					}else {
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}
						String text = sb.toString();
						tpToHome(player, text, lc.getServerName());
					}
				}else {
					lc.noPerm(player, "lgc.useHomesystem");
				}
			}
		}
		return true;
	}
	
	public static void initialiseFile() throws IOException {
		if(!homes.exists()) {
			homes.createNewFile();
		}
	}
	
	int getMaxHomes() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT maxHomes FROM mc_serverstats WHERE servername = ?");
			ps.setString(1, new LotusController().getServerName());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt("maxHomes");
			}else {
				return 6; //If no row exist yet, return 6 for default.
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 6;
		}
	}
	
	void updateHomeCount(Player player, Count type) {
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(homes);
		if(type == Count.UP) {
			if(cfg.contains(player.getUniqueId().toString())) {
				int old = cfg.getInt(player.getUniqueId().toString());
				cfg.set(player.getUniqueId().toString(), (old + 1));
			}else {
				cfg.set(player.getUniqueId().toString(), 1);
			}
		}else if(type == Count.DOWN) {
			if(cfg.contains(player.getUniqueId().toString())) {
				int old = cfg.getInt(player.getUniqueId().toString());
				cfg.set(player.getUniqueId().toString(), (old - 1));
			}
		}
		try {
			cfg.save(homes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	int getHomeCount(Player player) {
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(homes);
		if(cfg.contains(player.getUniqueId().toString())) {
			return cfg.getInt(player.getUniqueId().toString());
		}else {
			return 0;
		}
	}
	
	void deleteHome(Player player, String homename, String server) {
		if(existHome(player, homename, server)) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM mc_homes WHERE mcuuid = ? AND server = ? AND name = ?");
				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, server);
				ps.setString(3, homename);
				ps.executeUpdate();
				updateHomeCount(player, Count.DOWN);
				LotusController lc = new LotusController();
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.delhome.success").replace("%homename%", homename));
				// command.delhome.success
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			LotusController lc = new LotusController();
			player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.delhome.homedoesnotexist").replace("%homename%", homename));
			// command.delhome.homedoesnotexist -> The home %homename% doesn't exist!
		}
	}
	
	void addHome(Player player, String homename, String server) {
		LotusController lc = new LotusController();
		if(existHome(player, homename, server)) {
			player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sethome.homeexistalready").replace("%homename%", homename));
			// command.sethome.homeexistalready -> The homename %homename% exists already!
		}else {
			if(getHomeCount(player) <= maxHomes) {
				try {
					PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_homes(mcuuid, server, name, positionData, setAt) VALUES (?, ?, ?, ?, ?)");
					ps.setString(1, player.getUniqueId().toString());
					ps.setString(2, server);
					ps.setString(3, homename);
					ps.setString(4, translateLocationPosData(player.getLocation()));
					ps.setLong(5, System.currentTimeMillis());
					ps.executeUpdate();
					updateHomeCount(player, Count.UP);
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sethome.success").replace("%homename%", homename));
					//command.sethome.success -> The home has been set as %homename%
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else {
				if(player.hasPermission("lgc.bypassHomeLimit")) {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_homes(mcuuid, server, name, positionData, setAt) VALUES (?, ?, ?, ?, ?)");
						ps.setString(1, player.getUniqueId().toString());
						ps.setString(2, server);
						ps.setString(3, homename);
						ps.setString(4, translateLocationPosData(player.getLocation()));
						ps.setLong(5, System.currentTimeMillis());
						ps.executeUpdate();
						updateHomeCount(player, Count.UP);
						player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sethome.success").replace("%homename%", homename));
						lc.sendMessageReady(player, "command.sethome.overLimit.bypass");
						//command.sethome.success -> The home has been set as %homename%
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else {
					lc.sendMessageReady(player, "command.sethome.overLimit.error");
				}
			}
		}
	}
	
	void tpToHome(Player player, String homename, String server) {
		LotusController lc = new LotusController();
		if(existHome(player, homename, server)) {
			String posData = "";
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_homes WHERE mcuuid = ? AND server = ? AND name = ?");
				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, server);
				ps.setString(3, homename);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					posData = rs.getString("positionData");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(posData.isEmpty()) {
				Main.logger.severe("Error in Homesystem#tpToHome@checkIfposDataIsEmpty");
			}else {
				Location location = translatePosDataLocation(posData);
				player.teleport(location);
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.home.success").replace("%homename%", homename));
			}
		}else {
			player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.delhome.homedoesnotexist").replace("%homename%", homename));
		}
	}
	
	boolean existHome(Player player, String homename, String server) {
		boolean doExist = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_homes WHERE mcuuid = ? AND server = ? AND name = ?");
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, server);
			ps.setString(3, homename);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				doExist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return doExist;
	}
	
	String translateLocationPosData(Location location) {
		String world = location.getWorld().getName();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		double yaw = location.getYaw();
		double pitch = location.getPitch();
		return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
	}
	
	Location translatePosDataLocation(String posData) {
		String[] args = posData.split(";");
		String world = args[0];
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[3]);
		double yaw = Double.parseDouble(args[4]);
		double pitch = Double.parseDouble(args[5]);
		Location location = new Location(Bukkit.getWorld(world), x, y, z, (float)yaw, (float)pitch);
		return location;
	}
	
	enum Count {
		UP,
		DOWN;
	}
	
	
}