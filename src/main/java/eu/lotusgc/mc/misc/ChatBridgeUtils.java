//Created by Chris Wille at 10.02.2024
package eu.lotusgc.mc.misc;

public class ChatBridgeUtils {
	
	public static long creative = 1203443323600113727l;
	public static long creativehx = 1203443364146450462l;
	public static long survival = 1203443341757251635l;
	public static long survivalhx = 1203443391623471114l;
	public static long skyblock = 1203443412481744966l;
	public static long farmserver = 1203443429863063552l;
	
	public static String tranlateIntoMultiDataString(String server, String text) {
		String toReturn = "";
		switch(server) {
		case "Creative": toReturn = creative + "-;-" + text; break;
		case "Creative HX": toReturn = creativehx + "-;-" + text; break;
		case "Survival": toReturn = survival + "-;-" + text; break;
		case "Survival HX": toReturn = survivalhx + "-;-" + text; break;
		case "SkyBlock": toReturn = skyblock + "-;-" + text; break;
		case "Farmserver": toReturn = farmserver + "-;-" + text; break;
		}
		return toReturn;
	}

}

