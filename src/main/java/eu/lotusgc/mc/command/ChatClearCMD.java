package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.permission.Permission;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

@org.bukkit.plugin.java.annotation.command.Command(name="chatclear", aliases= {"cc"}, usage="/cc <private|global|anonymous>")
@Permission(name="lgc.command.chatclear.global", desc="Allows execution of clearing the chat with clear name")
@Permission(name="lgc.command.chatclear.anoynmous", desc="Allows execution of clearing the chat with no shown name")
public class ChatClearCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(args.length == 1) {
				String mode = args[0];
				if(mode.equalsIgnoreCase("private")) {
					clearMessage(player);
					lc.sendMessageReady(player, "command.cc.private");
				}else if(mode.equalsIgnoreCase("global")) {
					if(player.hasPermission("lgc.command.chatclear.global")) {
						for(Player all : Bukkit.getOnlinePlayers()) {
							clearMessage(all);
							all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "command.cc.global").replace("%displayer%", player.getDisplayName()));
						}
					}else {
						lc.noPerm(player, "lgc.command.chatclear.global");
					}
				}else if(mode.equalsIgnoreCase("anonymous")) {
					if(player.hasPermission("lgc.command.chatclear.anonymous")) {
						for(Player all : Bukkit.getOnlinePlayers()) {
							clearMessage(all);
							lc.sendMessageReady(all, "command.cc.anonymous");
						}
					}else {
						lc.noPerm(player, "lgc.command.chatclear.anonymous");
					}
				}else {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/cc <private|global|anonymous>");
				}
			}else {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/cc <private|global|anonymous>");
			}
		}
		return false;
	}
	
	private void clearMessage(Player player) {
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
		player.sendMessage("\n \n \n \n \n \n \n \n \n \n");
	}
}