package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;

@org.bukkit.plugin.java.annotation.command.Command(name="workbench", desc="Opens the Workbench at any location", permission="lgc.command.workbench", aliases= {"wb"})
public class WorkbenchCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(player.hasPermission("lgc.command.workbench")) {
				lc.sendMessageReady(player, "command.workbench.open");
				player.openWorkbench(null, true);
			}else {
				lc.noPerm(player, "lgc.command.workbench");
			}
		}
		return true;
	}
}