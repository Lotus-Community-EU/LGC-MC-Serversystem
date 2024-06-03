//Created by Christopher at 19.05.2024
package eu.lotusgc.mc.misc;

import org.bukkit.inventory.ItemStack;

public class InventorySyncData {
	
	private ItemStack[] backupInv;
	private ItemStack[] backupAr;
	private ItemStack[] enderChest;
	private Boolean syncComplete;
	
	public InventorySyncData() {
		this.backupInv = null;
		this.backupAr = null;
		this.syncComplete = false;
		this.enderChest = null;
	}
	
	public void setSyncStatus(boolean syncStatus) {
		syncComplete = syncStatus;
	}
	
	public Boolean getSyncStatus() {
		return syncComplete;
	}
	
	public ItemStack[] getBackupArmor() {
		return backupAr;
	}
	
	public ItemStack[] getBackupInventory() {
		return backupInv;
	}
	
	public ItemStack[] getBackupEnderChest() {
		return enderChest;
	}
	
	public void setBackupInventory(ItemStack[] backupInventory) {
		backupInv = backupInventory;
	}
	
	public void setBackupArmor(ItemStack[] backupArmor) {
		backupAr = backupArmor;
	}
	
	public void setBackupEnderChest(ItemStack[] backupInventory) {
		enderChest = backupInventory;
	}

}

