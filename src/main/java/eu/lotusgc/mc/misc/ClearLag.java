package eu.lotusgc.mc.misc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;

public class ClearLag {
	
	static String prefix = "§aClear§cLag §7» ";
	public static int cl_time = 0;
	//15 mins equal 900 seconds | 870, 895, 896, 897, 898, 899, 900 R/T
	
	public void triggerClearlag() {
		LotusController lc = new LotusController();
		cl_time++;
		switch(cl_time) {
		case 870:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "30"));
			}
			break;
		case 895:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "5"));
			}
			break;
		case 896:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "4"));
			}
			break;
		case 897:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "3"));
			}
			break;
		case 898:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "2"));
			}
			break;
		case 899:
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.message").replace("%time%", "1"));
			}
			break;
		case 900:
			cl_time = 0;
			int worlds = 0;
			int entities = 0;
			for(World world : Bukkit.getWorlds()) {
				worlds++;
				for(Entity entity : world.getEntities()) {
					if(entity instanceof Item) {
						entities++;
						entity.remove();
					}
				}
			}
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(prefix + lc.sendMessageToFormat(all, "system.clearlag.messageFinish").replace("%worlds%", String.valueOf(worlds)).replace("%entities%", String.valueOf(entities)));
			}
		}
	}

}
