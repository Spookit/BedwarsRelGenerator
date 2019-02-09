package thito.breadcore.utils;

import org.bukkit.ChatColor;

public class StrUtil {

	public static String capitalizeEnum(Enum<?> n) {
		String builder = new String();
		for (String s : n.name().split("_")) {
			builder+=String.valueOf(s.charAt(0)).toUpperCase()+s.substring(1).toLowerCase();
		}
		return builder;
	}
	
	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static String format(String s,Object...objects) {
		for (int i = 0; i < objects.length; i++) {
			s = s.replace("{"+i+"}", String.valueOf(objects[i]));
		}
		return s;
	}
	
	public static String textJson(String s) {
		return "{\"text\":\""+s+"\"}";
	}
}
