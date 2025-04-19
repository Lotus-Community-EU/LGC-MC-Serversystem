package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;

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
				InventoryView view = MenuType.CRAFTING.create(player, "§aMobile Workbench");
				player.openInventory(view);
			}else {
				lc.noPerm(player, "lgc.command.workbench");
			}
		}
		return true;
	}
}