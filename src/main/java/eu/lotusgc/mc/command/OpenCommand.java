package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.event.InventoryHandler;
import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;

public class OpenCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			if(command.getName().equalsIgnoreCase("s")) {
				LotusController lc = new LotusController();
				lc.sendMessageReady(player, "event.hotbar.open.navigator");
				InventoryHandler.setNavigatorInventory(player);
			}else if(command.getName().equalsIgnoreCase("profile")) {
				LotusController lc = new LotusController();
				lc.sendMessageReady(player, "command.profile.open");
				InventoryHandler.profileSettings(player);
			}
		}
		return true;
	}
}
