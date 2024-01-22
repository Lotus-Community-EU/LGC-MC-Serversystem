package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;

public class PrivateMessageCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(command.getName().equalsIgnoreCase("msg")) {
				
			}else if(command.getName().equalsIgnoreCase("r")) {
				
			}
		}
		return false;
	}
	
	boolean hasPMBlocked(Player player) {
		boolean isBlocked = false;
		return isBlocked;
	}

}
