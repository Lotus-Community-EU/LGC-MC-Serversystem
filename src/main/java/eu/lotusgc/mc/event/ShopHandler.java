//Created by Chris Wille at 29.02.2024
package eu.lotusgc.mc.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.inventory.ItemStack;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.misc.Money;
import eu.lotusgc.mc.misc.MySQL;
import net.md_5.bungee.api.ChatColor;

public class ShopHandler implements Listener {
	
	/*
	 *  Adminshop
	 *  Sign:
	 *  [shop]
	 *  Material
	 *  Amount (1-64)
	 *  OPT1: buy:sell | OPT2: buy
	 * 
	 */
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		LotusController lc = new LotusController();
		if(event.getLine(0).equalsIgnoreCase("[shop]")) {
			if(player.hasPermission("lgc.event.adminshop.create")) {
				Material material = Material.matchMaterial(event.getLine(1));
				if(material != null) {
					int amount = 1;
					if(event.getLine(2).matches("^[0-9]+$")) {
						amount = Integer.parseInt(event.getLine(2));
						int maxAmount = material.getMaxStackSize();
						if(amount > maxAmount) {
							amount = maxAmount;
							player.sendMessage("item can only be stacked up to " + maxAmount + " items!!!!!!!");
						}
						String l3 = event.getLine(3);
						boolean isBuySell = false;
						for(int i = 0; i < l3.length(); i++) {
							char c = l3.charAt(i);
							if(Character.toString(c).equals(":")) {
								isBuySell = true;
								break;
							}
						}
						if(isBuySell) {
							String[] split = l3.split(":");
							event.setLine(0, "§cAdminshop");
							event.setLine(1, "§6" + material.getKey().getKey().toUpperCase());
							event.setLine(2, "§7Amount: §a" + amount);
							event.setLine(3, "§a" + split[0] + " §7| §c" + split[1]);
							addShopSign(event.getBlock().getLocation(), material, amount, Double.parseDouble(split[0]), Double.parseDouble(split[1]), player, lc.getServerName());
						}else {
							event.setLine(0, "§cAdminshop");
							event.setLine(1, "§6" + material.getKey().getKey().toUpperCase());
							event.setLine(2, "§7Amount: §a" + amount);
							event.setLine(3, "§a" + l3);
							addShopSign(event.getBlock().getLocation(), material, amount, Double.parseDouble(l3), -1, player, lc.getServerName());
						}
						player.sendMessage("shop sign created");
					}else {
						player.sendMessage("amount must be alphanumerical and only can contain positive values!");
					}
				}else {
					player.sendMessage("invalid item");
				}
			}
		}
	}
	
	@EventHandler
	public void onSignOpen(PlayerSignOpenEvent event) {
		LotusController lc = new LotusController();
		if(isShopSign(event.getSign().getLocation(), lc.getServerName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(isSign(event.getBlock().getType())) {
			LotusController lc = new LotusController();
			if(isShopSign(event.getBlock().getLocation(), lc.getServerName())) {
				deleteShopSign(event.getBlock().getLocation(), lc.getServerName());
			}
		}
	}
	
	@EventHandler
	public void onSignInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(event.getClickedBlock() == null) return;
		if(isSign(event.getClickedBlock().getType())) {
			Player player = event.getPlayer();
			Sign sign = (Sign) event.getClickedBlock().getState();
			LotusController lc = new LotusController();
			if(action == Action.LEFT_CLICK_BLOCK) {
				//sell
				if(isShopSign(sign.getLocation(), lc.getServerName())) {
					Material material = Material.matchMaterial(ChatColor.stripColor(sign.getTargetSide(player).getLine(1)));
					if(material != null) {
						String[] prices = getPrices(sign.getLocation(), lc.getServerName());
						int sellPrice = 0;
						if(prices[1].equalsIgnoreCase("-1")) {
							player.sendMessage("you cant sell here!");
						}else {
							if(prices[1].matches("^[0-9]+$")) {
								sellPrice = Integer.parseInt(prices[1]);
								String oldCount = sign.getTargetSide(player).getLine(2).substring(12);
								int amt = Integer.parseInt(oldCount);
								ItemStack is = new ItemStack(material, amt);
								if(player.getInventory().getItemInMainHand() != null) {
									ItemStack is1 = player.getInventory().getItemInMainHand();
									if(is1.isSimilar(is)) {
										int invAmt = is1.getAmount();
										if(invAmt >= amt) {
											if((invAmt - amt) == 0) {
												player.getInventory().removeItem(is);
												lc.addMoney(player, sellPrice, Money.POCKET);
												player.sendMessage("Sold " + amt + " " + is1.getType().toString().toUpperCase() + " and got " + sellPrice + " loti--");
											}else {
												ItemStack clone = is1.clone();
												clone.setAmount((invAmt - amt));
												player.getInventory().removeItem(is);
												player.getInventory().setItemInMainHand(clone);
												lc.addMoney(player, sellPrice, Money.POCKET);
												player.sendMessage("Sold " + amt + " " + is1.getType().toString().toUpperCase() + " and got " + sellPrice + " loti");
											}
										}else if(invAmt < amt) {
											player.sendMessage("You need at least " + amt + " " + is.getType().toString().toUpperCase());
										}
									}else {
										player.sendMessage("You need " + is.getType().toString().toUpperCase() + " to be able to sell here dumbfuck.");
									}
								}
							}else {
								player.sendMessage("errored.");
							}
						}
					}else {
						player.sendMessage("mat null");
					}
				}else {
					player.sendMessage("is not a shop sign (DBQ)");
				}
			}else if(action == Action.RIGHT_CLICK_BLOCK) {
				//buy
				if(isShopSign(sign.getLocation(), lc.getServerName())) {
					Material material = Material.matchMaterial(ChatColor.stripColor(sign.getTargetSide(player).getLine(1)));
					if(material != null) {
						String[] prices = getPrices(sign.getLocation(), lc.getServerName());
						int buyPrice = 0;
						if(prices[0].matches("^[0-9]+$")) {
							buyPrice = Integer.parseInt(prices[0]);
							if(lc.hasEnoughFunds(player, buyPrice, Money.POCKET)) {
								String oldCount = sign.getTargetSide(player).getLine(2).substring(12);
								int amt = Integer.parseInt(oldCount);
								ItemStack is = new ItemStack(material, amt);
								lc.removeMoney(player, buyPrice, Money.POCKET);
								player.getInventory().addItem(is);
							}else {
								player.sendMessage("geh arbeiten arschwixer");
							}
						}else {
							player.sendMessage("errored.");
						}
					}
				}
			}
		}
	}
	
	String[] getPrices(Location location, String server) {
		String args = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT buyPrice,sellPrice FROM mc_adminshop WHERE server = ? AND world = ? AND x = ? AND y = ? AND z = ?");
			ps.setString(1, server);
			ps.setString(2, location.getWorld().getName());
			ps.setInt(3, location.getBlockX());
			ps.setInt(4, location.getBlockY());
			ps.setInt(5, location.getBlockZ());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				args = rs.getInt("buyPrice") + ":" + rs.getInt("sellPrice");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return args.split(":");
	}
	
	void addShopSign(Location location, Material material, int amount, double buyPrice, double sellPrice, Player player, String server) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_adminshop(server,creator,world,x,y,z,material,amount,buyPrice,sellPrice) VALUES (?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, server);
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, location.getWorld().getName());
			ps.setInt(4, location.getBlockX());
			ps.setInt(5, location.getBlockY());
			ps.setInt(6, location.getBlockZ());
			ps.setString(7, material.getKey().getKey().toUpperCase());
			ps.setInt(8, amount);
			ps.setDouble(9, buyPrice);
			ps.setDouble(10, sellPrice);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void deleteShopSign(Location location, String server) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM mc_adminshop WHERE world = ? AND x = ? AND y = ? AND z = ? AND server = ?");
			ps.setString(1, location.getWorld().getName());
			ps.setInt(2, location.getBlockX());
			ps.setInt(3, location.getBlockY());
			ps.setInt(4, location.getBlockZ());
			ps.setString(5, server);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	boolean isShopSign(Location location, String server) {
		boolean treasureSign = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_adminshop WHERE x = ? AND y = ? AND z = ? AND world = ? AND server = ?");
			ps.setInt(1, location.getBlockX());
			ps.setInt(2, location.getBlockY());
			ps.setInt(3, location.getBlockZ());
			ps.setString(4, location.getWorld().getName());
			ps.setString(5, server);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				treasureSign = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return treasureSign;
	}
	
	boolean isSign(Material input) {
		List<Material> signs = new ArrayList<>();
		signs.add(Material.OAK_WALL_SIGN);
		signs.add(Material.OAK_WALL_HANGING_SIGN);
		signs.add(Material.OAK_SIGN);
		signs.add(Material.SPRUCE_WALL_SIGN);
		signs.add(Material.SPRUCE_WALL_HANGING_SIGN);
		signs.add(Material.SPRUCE_SIGN);
		signs.add(Material.BIRCH_WALL_SIGN);
		signs.add(Material.BIRCH_WALL_HANGING_SIGN);
		signs.add(Material.BIRCH_SIGN);
		signs.add(Material.JUNGLE_WALL_SIGN);
		signs.add(Material.JUNGLE_WALL_HANGING_SIGN);
		signs.add(Material.JUNGLE_SIGN);
		signs.add(Material.ACACIA_WALL_SIGN);
		signs.add(Material.ACACIA_WALL_HANGING_SIGN);
		signs.add(Material.ACACIA_SIGN);
		signs.add(Material.DARK_OAK_WALL_SIGN);
		signs.add(Material.DARK_OAK_WALL_HANGING_SIGN);
		signs.add(Material.DARK_OAK_SIGN);
		signs.add(Material.MANGROVE_WALL_SIGN);
		signs.add(Material.MANGROVE_WALL_HANGING_SIGN);
		signs.add(Material.MANGROVE_SIGN);
		signs.add(Material.CHERRY_WALL_SIGN);
		signs.add(Material.CHERRY_WALL_HANGING_SIGN);
		signs.add(Material.CHERRY_SIGN);
		signs.add(Material.BAMBOO_WALL_SIGN);
		signs.add(Material.BAMBOO_WALL_HANGING_SIGN);
		signs.add(Material.BAMBOO_SIGN);
		signs.add(Material.CRIMSON_WALL_SIGN);
		signs.add(Material.CRIMSON_WALL_HANGING_SIGN);
		signs.add(Material.CRIMSON_SIGN);
		signs.add(Material.WARPED_WALL_SIGN);
		signs.add(Material.WARPED_WALL_HANGING_SIGN);
		signs.add(Material.WARPED_SIGN);
		return signs.contains(input);
	}

}