//Created by Maurice H. at 30.12.2024
package eu.lotusgc.mc.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;

public class BackpackHandler implements Listener {
	
	public static void openBackpack(Player player) {
		LotusController lc = new LotusController();
		Inventory inventory = null;
		if(player.hasPermission("lgc.backpack.premium")) {
			inventory = Bukkit.createInventory(null, 9*6, "§6Backpack");
			inventory.setContents(lc.getBPData(player));
		}else {
			inventory = Bukkit.createInventory(null, 9*3, "§6Backpack");
			inventory.setContents(lc.getBPData(player));
		}
		player.openInventory(inventory);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getView().getTitle().equals("§6Backpack")) {
			LotusController lc = new LotusController();
			Main.logger.info("Backpack closed, attempting to save contents.");
			lc.saveBPData(player, event.getInventory().getContents());
			Main.logger.info("Saved content. Check!");
		}
	}
}