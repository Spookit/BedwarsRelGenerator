package thito.bedwarsrelgenerator;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.handlers.TeamHandler;
import thito.breadcore.spigot.nbt.ItemStackTag;
import thito.breadcore.spigot.nbt.NBTConstants;

public class Util {

	public static void main(String[]args) {
		System.out.println(Arrays.toString(long[].class.getMethods()));
	}
	public static boolean isTool(Material m) {
		return m != null && (
				m.name().endsWith("PICKAXE") ||
				m.name().endsWith("AXE") ||
				m.name().endsWith("SHOVEL") ||
				m.name().endsWith("HOE"));
	}
	
	@SafeVarargs
	public static <T> String toString(T...ts) {
		if (ts.length == 0) return "- Empty -";
		String tsx = Arrays.toString(ts);
		return tsx.substring(1,tsx.length()-1);
	}
	
	public static <T> String toString(Collection<T> ts) {
		if (ts.isEmpty()) return "- Empty -";
		String tsx = ts.toString();
		return tsx.substring(1, tsx.length()-1);
	}
	
	public static boolean isSword(Material m) {
		return m != null && m.name().endsWith("SWORD");
	}
	
	public static boolean isArmor(Material m) {
		return m != null && (
				m.name().endsWith("HELMET") ||
				m.name().endsWith("CHESTPLATE") ||
				m.name().endsWith("LEGGINGS") ||
				m.name().endsWith("BOOTS"));
	}
	public static boolean isLoadout(ItemStack item) {
		return item != null && new ItemStackTag(item).getNBT().hasKeyOfType("bwg_loadout",NBTConstants.STRING);
	}
	public static boolean isLoadout(ItemStack item,String signature) {
		return new ItemStackTag(item).getNBT().getString("bwg_loadout").equals(signature);
	}
	public static void placeNeatfully(Player p,ItemStack item) {
		String name = item.getType().name();
		if (name.endsWith("HELMET")) {
			p.getInventory().setHelmet(item);
		} else if (name.endsWith("CHESTPLATE")) {
			p.getInventory().setChestplate(item);
		} else if (name.endsWith("LEGGINGS")) {
			p.getInventory().setLeggings(item);
		} else if (name.endsWith("BOOTS")) {
			p.getInventory().setBoots(item);
		} else {
			p.getInventory().addItem(item);
		}
	}
	
	public static void modify(ItemStack item,TeamHandler h) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			if (meta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) meta).setColor(h.getTeam().getColor().getColor());
			}
			item.setItemMeta(meta);
		}
	}
	
	public static void replaceAndPlaceLoadout(Player p,ItemStack item,String signature) {
		String name = item.getType().name();
		if (name.endsWith("HELMET")) {
			p.getInventory().setHelmet(item);
		} else if (name.endsWith("CHESTPLATE")) {
			p.getInventory().setChestplate(item);
		} else if (name.endsWith("LEGGINGS")) {
			p.getInventory().setLeggings(item);
		} else if (name.endsWith("BOOTS")) {
			p.getInventory().setBoots(item);
		} else {
			if (p.getItemInHand() != null && p.getItemInHand().getType() == item.getType() &&
					isLoadout(p.getItemInHand(),signature)) {
				p.setItemInHand(item);
				return;
			}
			int index = 0;
			int lastIndex = -1;
			for (ItemStack i : p.getInventory().getContents()) {
				if (i != null && i.getType() == item.getType() && isLoadout(i,signature)) {
					p.getInventory().removeItem(i);
					lastIndex = index;
				}
				index++;
			}
			if (lastIndex >= 0) {
				p.getInventory().setItem(lastIndex, item);
			} else {
				p.getInventory().addItem(item);
			}
		}
	}
	static long[] duration = {60,60,24,31,12};
	static String[] description = {"seconds","minutes","hours","months","years"};
	public static String formatSeconds(long sec) {
		if (sec < 0) sec = 0;
		long mins = sec / 60;
		long seconds = sec - mins * 60;
		String fm = new String();
		if (mins < 10) 
			fm+=0;
		fm+=mins+":";
		if (seconds < 10)
			fm+=0;
		fm+=seconds;
		return fm;
	}
	public static void setLoadout(ItemStack item,String signature) {
		if (signature != null) {
			ItemStackTag tag = new ItemStackTag(item);
			tag.getNBT().setString("bwg_loadout", signature);
			tag.apply(item);
		} else {
			ItemStackTag tag = new ItemStackTag(item);
			tag.getNBT().remove("bwg_loadout");
			tag.apply(item);
		}
	}
	public static String format(String string,Object...args) {
		if (string != null) {
			string = ChatColor.translateAlternateColorCodes('&', string);
			for (int i = 0; i < args.length; i++) {
				string = string.replace("{"+i+"}", args[i]+"");
			}
		} else {
			return "";
		}
		return string;
	}
	
}
