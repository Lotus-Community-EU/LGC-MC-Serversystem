package eu.lotusgc.mc.command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Prefix;

public class ScoreboardChangeCMD implements CommandExecutor{
	
	/*
	 * Scoreboard States:
	 * 0 = off
	 * 1 = default (view own info)
	 * 2 = Jobs
	 * 3 = Reports (Admin and higher - IPermissible "lgc.viewReports"
	 * 4 = Serverstatus (Admin and higher - IPermissible "lgc.viewServerhealth"
	 * 5 = Radio Information (not upon release - Radio will follow up later)
	 * 6 = Servers and player count on each one
	 * 7 = World Info and Coordinates
	 * 8 = View Players around you (normal = 500 blocks, premium = 1000, Admin and higher (2500 blocks))
	 * 9 = View Entities (except Players) around you (normal 100 blocks, premium 250 blocks, Staffs general up to 500 blocks (only the nearest 10 will be listed anyway!)
	 * 
	 * command.sb.off -> You've turned the sideboard off.
	 * command.sb.status -> Switched sideboard to %status%.
	 */

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player)sender;
			LotusController lc = new LotusController();
			if(args.length == 1) {
				switch(args[0]) {
				case "off": updateSB(player, 0); lc.sendMessageReady(player, "command.sb.off"); break;
				case "default": updateSB(player, 1); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Default")); ;break;
				case "job": updateSB(player, 2); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Jobs")); break;
				case "reports": if(player.hasPermission("lgc.command.sb.report")) { updateSB(player, 3); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Reports")); }else { lc.noPerm(player, "lgc.command.sb.report"); } break;
				case "serverstatus": if(player.hasPermission("lgc.command.sb.server")) { updateSB(player, 4); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Serverstatus")); }else { lc.noPerm(player, "lgc.command.sb.server"); } break;
				//case "radio": updateSB(player, 5); break; //not yet enabled, as LGC don't have a radio yet (thus no data to display)
				case "server": updateSB(player, 6); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Servers")); break;
				case "world": updateSB(player,7); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Worldinfo")); break;
				case "players": updateSB(player, 8); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Players")); break;
				case "entities": updateSB(player, 9); player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.sb.status").replace("%status%", "Entities")); break;
				default: player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/sb <off|default|job|reports|serverstatus|server|world|players|entities>"); break;
				}
			}else {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/sb <off|default|job|reports|serverstatus|server|world|players|entities>");
			}
		}
		return true;
	}
	
	void updateSB(Player player, int code) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET scoreboardState = ? WHERE mcuuid = ?");
			ps.setInt(1, code);
			ps.setString(2, player.getUniqueId().toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
