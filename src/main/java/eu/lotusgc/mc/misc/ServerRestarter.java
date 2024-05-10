package eu.lotusgc.mc.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;

public class ServerRestarter {
	
	public void triggerRestart(){
		LotusController lc = new LotusController();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		//Restarter sends out a message 2h, 1h, 30m, 10m, 5m, 1m, 30s, 5s, 4s, 3s, 2s, 1s before stop
		//server restarts at 3am as most people will be asleep there.
		
		//System >> The server restarts in x %time%
		String time = sdf.format(new Date());
		
		if(time.matches("01:00:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "2").replace("%timetype%", "hours"));
			}
		}else if(time.matches("02:00:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "1").replace("%timetype%", "hours"));
			}
		}else if(time.matches("02:30:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "30").replace("%timetype%", "mins"));
			}
		}else if(time.matches("02:50:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "10").replace("%timetype%", "mins"));
			}
		}else if(time.matches("02:55:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "5").replace("%timetype%", "mins"));
			}
		}else if(time.matches("02:59:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "1").replace("%timetype%", "min"));
			}
		}else if(time.matches("02:59:30")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "30").replace("%timetype%", "seconds"));
			}
		}else if(time.matches("02:59:55")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "5").replace("%timetype%", "seconds"));
			}
		}else if(time.matches("02:59:56")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "4").replace("%timetype%", "seconds"));
			}
		}else if(time.matches("02:59:57")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "3").replace("%timetype%", "seconds"));
			}
		}else if(time.matches("02:59:58")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "2").replace("%timetype%", "seconds"));
			}
		}else if(time.matches("02:59:59")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(all, "system.restart.message").replace("%time%", "1").replace("%timetype%", "second"));
			}
		}else if(time.matches("03:00:00")) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.kickPlayer("§aLotus §cGaming §fCommunity\n \n§7The server is rebooting now. \n§aJoin back in a minute!");
			}
			Bukkit.shutdown();
		}
		
	}

}
