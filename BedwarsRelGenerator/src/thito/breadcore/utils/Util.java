package thito.breadcore.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Util {
	public static void main(String[]args) throws Exception {
	}
	public static int safeRange(int min,int target,int max) {
		return Math.min(min, Math.max(target, max));
	}
	public static String getVersion() {
		final String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}
	
	public static int getVersionNumber() {
		final String name = getVersion().substring(3);
		return Integer.valueOf(name.substring(0, name.length() - 4));
	}
    public static String toString(Object o) {
    	if (o instanceof Location) {
    		Location loc = (Location)o;
    		return loc.getWorld().getName()+":"+loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ();
    	}
    	return String.valueOf(o);
    }
    static Map<String,Class<?>> CRAFT_CACHE = new HashMap<>();
    public static Class<?> craft(String className) {
        try {
        	Class<?> cache = CRAFT_CACHE.get(className);
        	if (cache != null) return cache;
            String classPath = "org.bukkit.craftbukkit." + getBukkitVersion() +"."+ className;
            Class<?> cl = Class.forName(classPath);
            CRAFT_CACHE.put(className, cl);
            return cl;
        } catch (Exception e) {
            return null;
        }
    }
    static Map<String,Class<?>> NMS_CACHE = new HashMap<>(); 
    public static Class<?> nms(String className) {
        try {
        	Class<?> cache = NMS_CACHE.get(className);
        	if (cache != null) return cache;
        	if (className.contains("$")) {
        		String[] split = className.split("\\$",2);
        		if (split.length > 1) {
        			Class<?> cl = nms(split[1]);
        			if (cl != null) {
        				NMS_CACHE.put(className, cl);
        				return cl;
        			}
        		}
        	}
            String classPath = "net.minecraft.server." + getBukkitVersion() +"."+ className;
            Class<?> cl = Class.forName(classPath);
            NMS_CACHE.put(className, cl);
            return cl;
        } catch (Exception e) {
            return null;
        }
    }
    
	public static enum PacketVersion {
		V1_7_R1, V1_7_R2,V1_7_R3,V1_7_R4,V1_8_R1(true),V1_8_R2(true),V1_8_R3(true),V1_9_R1(true),V1_9_R2(true),V1_10_R1(true),V1_11_R1(true),V1_12_R1(true),V1_13_R1,V1_13_R2,UNKNOWN;
		boolean s;
		PacketVersion() {
			s = false;
		}
		PacketVersion(boolean supported) {
			s = supported;
		}
		public boolean isSupported() {
			return s;
		}
		public String toString() {
			return asDouble()+"("+name()+")";
		}
		public double asDouble() {
			switch(this) {
			case V1_7_R1:
				return 1.7;
			case V1_7_R2:
				return 1.7;
			case V1_7_R3:
				return 1.7;
			case V1_7_R4:
				return 1.7;
			case V1_8_R1:
				return 1.8;
			case V1_8_R2:
				return 1.8;
			case V1_8_R3:
				return 1.8;
			case V1_9_R1:
				return 1.9;
			case V1_9_R2:
				return 1.9;
			case V1_10_R1: return 1.10;
			case V1_11_R1: return 1.11;
			case V1_12_R1: return 1.12;
			case V1_13_R1: return 1.13;
			case V1_13_R2: return 1.13;
			case UNKNOWN: return 0;
			}
			return 0;
		}
	}
	
	public static PacketVersion getPacketVersion() {
		switch(getBukkitVersion()) {
		case "v1_7_R1":
			return PacketVersion.V1_7_R1;
		case "v1_7_R2":
			return PacketVersion.V1_7_R2;
		case "v1_7_R3":
			return PacketVersion.V1_7_R3;
		case "v1_7_R4":
			return PacketVersion.V1_7_R4;
		case "v1_8_R1":
			return PacketVersion.V1_8_R1;
		case "v1_8_R2":
			return PacketVersion.V1_8_R2;
		case "v1_8_R3":
			return PacketVersion.V1_8_R3;
		case "v1_9_R1":
			return PacketVersion.V1_9_R1;
		case "v1_9_R2":
			return PacketVersion.V1_9_R2;
		case "v1_10_R1":
			return PacketVersion.V1_10_R1;
		case "v1_11_R1":
			return PacketVersion.V1_11_R1;
		case "v1_12_R1":
			return PacketVersion.V1_12_R1;
		case "v1_13_R1":
			return PacketVersion.V1_13_R1;
		case "v1_13_R2":
			return PacketVersion.V1_13_R2;
			default:
				return PacketVersion.UNKNOWN;
		}
	}
	public static String getBukkitVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
}
