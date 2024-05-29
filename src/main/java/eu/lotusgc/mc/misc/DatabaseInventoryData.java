//Created by Christopher at 19.05.2024
package eu.lotusgc.mc.misc;

public class DatabaseInventoryData {
	
	private String rawInv;
	private String rawAr;
	private int xp;
	
	public DatabaseInventoryData(String rawInventory, String rawArmor, int xp) {
		this.rawInv = rawInventory;
		this.rawAr = rawArmor;
		this.xp = xp;
	}
	
	public int getXP() {
		return xp;
	}
	
	public String getRawArmor() {
		return rawAr;
	}
	
	public String getRawInventory() {
		return rawInv;
	}

}

