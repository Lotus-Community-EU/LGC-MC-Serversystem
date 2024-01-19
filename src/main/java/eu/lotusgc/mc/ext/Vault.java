package eu.lotusgc.mc.ext;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Vault implements Economy{

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return "Loticonomy";
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double amount) {
		return null;
	}

	@Override
	public String currencyNamePlural() {
		return "Loti";
	}

	@Override
	public String currencyNameSingular() {
		return "Loti";
	}

	@Override
	public boolean hasAccount(String playerName) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return true;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return true;
	}

	@Override
	public double getBalance(String playerName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBalance(String playerName, String world) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean has(String playerName, double amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getBanks() {
		List<String> banks = new ArrayList<>();
		banks.add("Lotus Bank");
		return banks;
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		return true;
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return true;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return true;
	}

}
