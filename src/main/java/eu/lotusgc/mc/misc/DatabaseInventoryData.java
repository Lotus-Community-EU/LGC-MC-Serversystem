//Created by Christopher at 19.05.2024
package eu.lotusgc.mc.misc;

public class DatabaseInventoryData {
	
	private String rawInv;
	private String rawAr;
	private String rawECInv;
	private float xp;
	private int level;
	
	public DatabaseInventoryData(String rawInventory, String rawArmor, String rawEnderChestInventory, float xp, int level) {
		this.rawInv = rawInventory;
		this.rawAr = rawArmor;
		this.rawECInv = rawEnderChestInventory;
		this.xp = xp;
		this.level = level;
	}
	
	public float getXP() {
		return xp;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getRawArmor() {
		return rawAr;
	}
	
	public String getRawInventory() {
		return rawInv;
	}
	
	public String getRawEnderChestInventory() {
		return rawECInv;
	}

}

