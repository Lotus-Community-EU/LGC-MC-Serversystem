package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.event.BackpackHandler;
import eu.lotusgc.mc.event.InventoryHandler;
import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.InputType;
import eu.lotusgc.mc.misc.Serverdata;

public class OpenCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(command.getName().equalsIgnoreCase("s")) {
				lc.sendMessageReady(player, "event.hotbar.open.navigator");
				InventoryHandler.setNavigatorInventory(player);
			}else if(command.getName().equalsIgnoreCase("profile")) {
				lc.sendMessageReady(player, "command.profile.open");
				InventoryHandler.profileSettings(player);
			}else if(command.getName().equalsIgnoreCase("backpack") || command.getName().equalsIgnoreCase("bp")) {
				boolean whitelistedServer = lc.translateBoolean(lc.getServerData(lc.getServerName(), Serverdata.AllowPlayerInventorySync, InputType.Servername));
				if(whitelistedServer) {
					lc.sendMessageReady(player, "command.backpack.open");
					BackpackHandler.openBackpack(player);
				}else {
					lc.sendMessageReady(player, "command.backpack.incompatibleServer");
				}
				
			}
		}
		return true;
	}
}