package eu.lotusgc.mc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.main.LotusController;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.Prefix;

public class InvseeCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(args.length == 1) {
				if(player.hasPermission("lgc.command.invsee")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(target != null) {
						if(target != player) {
							player.openInventory(target.getInventory());
							player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.invsee.success").replace("%displayer%", target.getDisplayName()));
							//command.invsee.success -> You can now interact with %displayer%'s inventory!
						}else {
							lc.sendMessageReady(player, "command.invsee.samePlayer");
							//command.invsee.samePlayer -> You can't open your own Inventory in here!
						}
					}else {
						lc.sendMessageReady(player, "global.playerOffline");
					}
				}else {
					lc.noPerm(player, "lgc.command.invsee");
				}
				
			}else {
				player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/invsee <Player>");
			}
		}
		return true;
	}

}
