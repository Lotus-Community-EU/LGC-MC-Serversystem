package eu.lotusgc.mc.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.LotusManager;
import eu.lotusgc.mc.main.Main;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.Prefix;
import eu.lotusgc.mc.misc.TextCryptor;


@org.bukkit.plugin.java.annotation.command.Command(name="msg")
@org.bukkit.plugin.java.annotation.command.Command(name="r")
@org.bukkit.plugin.java.annotation.command.Command(name="msgopt")
public class PrivateMessageCMD implements CommandExecutor{
	
	/*
	 * Commands
	 * - /msg <Player> <Message> | Sends a private message to specified player (If target has not blocked sender or MSGs)
	 * - /r <Message> | Replies to last message to the player whom messaged you last time.
	 * - /msgopt <global|block|unblock> [Player] | If arg[0] global is selected [Player] is not needed, if un/block is taken, then said player will be un/blocked to message the blocker.
	 *  e.g. /msgopt global | Turns private messages on/off -> /msgopt block APlayer123 | APlayer123 will be blocked from DMing the blocker (except APlayer123 is Staff, abuse can be reported!)
	 *  
	 *  Permissions
	 *  - lgc.bypassMSG | Players with that permission set can bypass the blocked state (No matter if the sender is blocked globally or personally).
	 */
	
	static HashMap<UUID, UUID> map = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consoleSend);
		}else {
			Player player = (Player) sender;
			LotusController lc = new LotusController();
			if(command.getName().equalsIgnoreCase("msg")) {
				if(args.length == 0) {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/msg <Player> <Message>");
				}else {
					Player receiver = Bukkit.getPlayer(args[0]);
					if(receiver != null) {
						StringBuilder sb = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}
						String message = sb.toString().substring(0, (sb.toString().length() - 1));
						if(globalMSGBlock(receiver)) {
							if(amIBlocked(player, receiver)) {
								if(player.hasPermission("lgc.command.msg.bypass")) {
									// command.msg.receiver.BlockedPMBypassInfo -> %displayer% has blocked you, however you can bypass it!
									receiver.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
									player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + receiver.getDisplayName() + "§7: " + message);
									map.put(receiver.getUniqueId(), player.getUniqueId());
									map.put(player.getUniqueId(), receiver.getUniqueId());
								}else {
									// command.msg.sender.BlockedByReceiver -> Your message has not been sent, you are blocked by %displayer%!
									player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.msg.sender.BlockedByReceiver").replace("%displayer%", receiver.getDisplayName()));
								}
							}else {
								receiver.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
								player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + receiver.getDisplayName() + "§7: " + message);
								map.put(receiver.getUniqueId(), player.getUniqueId());
								map.put(player.getUniqueId(), receiver.getUniqueId());
							}
						}else {
							if(player.hasPermission("lgc.command.msg.bypass")) {
								// command.msg.receiver.DisabledPMBypassInfo -> %displayer% has disabled private messages, however you can bypass it!
								receiver.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
								player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + receiver.getDisplayName() + "§7: " + message);
								map.put(receiver.getUniqueId(), player.getUniqueId());
								map.put(player.getUniqueId(), receiver.getUniqueId());
							}else {
								// command.msg.receiver.DisabledPMs -> %displayer% has disabled private messages!
								player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.msg.receiver.DisabledPMs").replace("%displayer%", receiver.getDisplayName()));
							}
						}
					}else {
						lc.sendMessageReady(player, "global.playerOffline");
					}
				}
			}else if(command.getName().equalsIgnoreCase("r")) {
				if(args.length == 0) {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/r <Message>");
				}else {
					if(map.containsKey(player.getUniqueId())) {
						//when a recent message has been logged
						Player target = Bukkit.getPlayer(map.get(player.getUniqueId()));
						if(target != null) {
							StringBuilder sb = new StringBuilder();
							for(int i = 0; i < args.length; i++) {
								sb.append(args[i]).append(" ");
							}
							String message = sb.toString().substring(0, (sb.toString().length() - 1));
							//player online
							if(globalMSGBlock(target)) {
								if(amIBlocked(player, target)) {
									if(player.hasPermission("lgc.command.msg.bypass")) {
										target.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
										player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + target.getDisplayName() + "§7: " + message);
									}else {
										player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.msg.sender.BlockedByReceiver").replace("%displayer%", target.getDisplayName()));
									}
								}else {
									target.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
									player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + target.getDisplayName() + "§7: " + message);
								}
							}else {
								if(player.hasPermission("lgc.command.msg.bypass")) {
									target.sendMessage(lc.getPrefix(Prefix.PMSYS) + player.getDisplayName() + lc.sendMessageToFormat(player, "command.msg.you") + "§7: " + message);
									player.sendMessage(lc.getPrefix(Prefix.PMSYS) + lc.sendMessageToFormat(player, "command.msg.you") + target.getDisplayName() + "§7: " + message);
									map.put(target.getUniqueId(), player.getUniqueId());
									map.put(player.getUniqueId(), target.getUniqueId());
								}else {
									player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "command.msg.receiver.DisabledPMs").replace("%displayer%", target.getDisplayName()));
								}
							}
						}else {
							//player offline
							lc.sendMessageReady(player, "global.playerOffline");
						}
					}else {
						// command.reply.noRecentMessage -> You had no active private message yet in this session!
						lc.sendMessageReady(player, "command.reply.noRecentMessage");
					}
				}
			}else if(command.getName().equalsIgnoreCase("msgopt")) {
				if(args.length == 1) {
					// global
				}else if(args.length == 2) {
					// un/block
					String mode = args[0];
					String sTarget = args[1];
					if(sTarget.length() >= 3 && sTarget.length() <= 16 && sTarget.matches("[a-zA-Z0-9_]+$")) {
						Player target = Bukkit.getPlayer(sTarget);
						if(target != null) {
							if(mode.equalsIgnoreCase("block")) {
								addPlayerBlock(player, target, true);
							}else if(mode.equalsIgnoreCase("unblock")) {
								addPlayerBlock(player, target, true);
							}else {
								player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/msgopt <global*|block|unblock> [Player]");
							}
						}else {
							lc.sendMessageReady(player, "global.playerOffline");
						}
					}else {
						player.sendMessage("String must not contain special characters and must be within 3 - 16 chars!");
					}
				}else {
					player.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(player, "global.args") + "§7/msgopt <global*|block|unblock> [Player]");
					lc.sendMessageReady(player, "command.msgopt.info");
					// command.msgopt.info -> Info: Player is only needed when block/unblock is being used!
				}
			}
		}
		return false;
	}
	
	//addremove -> if true, then add, if false, then remove
	void addPlayerBlock(Player blocker, Player target, boolean addRemove){
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(LotusManager.mainConfig);
		String password = cfg.getString("Password.PMs");
		LotusController lc = new LotusController();
		if(hasPlayerARow(blocker)) {
			String data = "";
			String newData = "";
			int newBlockCount = 0;
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_msgblock WHERE blocker = ?");
				ps.setString(1, blocker.getUniqueId().toString());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					data = rs.getString("hasBlocked");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			List<String> uuids = translateEncodedData(data, password);
			if(addRemove) {
				if(uuids.contains(target.getUniqueId().toString())) {
					blocker.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(blocker, "command.msgopt.blocked.alreadyBlocked").replace("%displayer%", target.getDisplayName()));
				}else {
					uuids.add(target.getUniqueId().toString());
					blocker.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(blocker, "command.msgopt.block.addedBlock").replace("%displayer%", target.getDisplayName()));
				}
			}else {
				if(uuids.contains(target.getUniqueId().toString())) {
					uuids.remove(target.getUniqueId().toString());
					blocker.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(blocker, "command.msgopt.unblock.removedBlock").replace("%displayer%", target.getDisplayName()));
				}else {
					uuids.add(target.getUniqueId().toString());
					blocker.sendMessage(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(blocker, "command.msgopt.unblock.alreadyUnblocked").replace("%displayer%", target.getDisplayName()));
				}
			}
			newBlockCount = uuids.size();
			newData = translateDecodedData(uuids, password);
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_msgblock SET hasBlocked = ?, blockCount = ? WHERE blocker = ?");
				ps.setString(1, newData);
				ps.setInt(2, newBlockCount);
				ps.setString(3, blocker.getUniqueId().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			List<String> uuids = new ArrayList<>();
			uuids.add(target.getUniqueId().toString());
			String encodedData = translateDecodedData(uuids, password);
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_msgblock(blocker, hasBlocked, blockCount) VALUES (?, ?, ?)");
				ps.setString(1, blocker.getUniqueId().toString());
				ps.setString(2, encodedData);
				ps.setInt(3, 1);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	boolean hasPlayerARow(Player player) {
		boolean playerHasRow = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_msgblock WHERE blocker = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				playerHasRow = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerHasRow;
	}
	
	//receiver must be specified, otherwise program does not know where to look.
	boolean amIBlocked(Player sender, Player receiver) {
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(LotusManager.mainConfig);
		String password = cfg.getString("Password.PMs");
		String hashedBlocklist = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT hasBlocked FROM mc_msgblock WHERE blocker = ?");
			ps.setString(1, receiver.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				hashedBlocklist = rs.getString("hasBlocked");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<String> uuids = translateEncodedData(hashedBlocklist, password);
		return uuids.contains(sender.getUniqueId().toString());
	}
	
	//Receiver player must be set.
	// if true, user allows pms, if false, user disallows pms
	boolean globalMSGBlock(Player player) {
		boolean blocked = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT allowMSG FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				blocked = rs.getBoolean("allowMSG");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blocked;
	}
	
	List<String> translateEncodedData(String input, String password) {
		input = TextCryptor.decrypt(input, password.toCharArray());
		List<String> uuids = new ArrayList<>();
		String[] args = input.split(";");
		for(String string : args) {
			uuids.add(string);
		}
		return uuids;
	}
	
	String translateDecodedData(List<String> input, String password) {
		StringBuilder sb = new StringBuilder();
		for(String string : input) {
			sb.append(string);
			sb.append(";");
		}
		String list = sb.toString().substring(0, (sb.toString().length() - 1));
		list = TextCryptor.encrypt(list, password.toCharArray());
		return list;
	}

}
