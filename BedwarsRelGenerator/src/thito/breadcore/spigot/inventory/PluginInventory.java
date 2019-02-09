package thito.breadcore.spigot.inventory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import thito.breadcore.utils.Util;

public class PluginInventory implements InventoryHolder {

	private static boolean isRegistered = false;
	public static void registerListener(JavaPlugin core) {
		if (!isRegistered) {
			isRegistered = true;
			core.getServer().getPluginManager().registerEvents(new Listener() {
				
				@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
				public void click(InventoryClickEvent e) {
					final InventoryView view = e.getView();
					if (view != null) {
						final Inventory top = view.getTopInventory();
						if (top != null && top.getHolder() instanceof PluginInventory) {
							PluginInventory inv = (PluginInventory)top.getHolder();
							e.setCancelled(!inv.isAllowInteract());
							if (top == e.getClickedInventory()) {
								Consumer<InventoryClickEvent> cons = inv.getConsumer(e.getRawSlot());
								if (cons != null) {
									cons.accept(e);
								}
							}
						}
					}
				}
				
				@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
				public void drag(InventoryDragEvent e) {
					final InventoryView view = e.getView();
					if (view != null) {
						final Inventory top = view.getTopInventory();
						if (top != null && top.getHolder() instanceof PluginInventory) {
							final PluginInventory inv = (PluginInventory)top.getHolder();
							e.setCancelled(!inv.isAllowInteract());
						}
					}
				}
				
				public String toString() {
					return "PluginInventoryListener";
				}
			}, core);
		}
	}
	private boolean allowInteract;
	private Inventory inv;
	private Map<Integer,Consumer<InventoryClickEvent>> clicks = new HashMap<>();
	public PluginInventory addConsumer(int slot,Consumer<InventoryClickEvent> ev) {
		clicks.put(slot, ev);
		return this;
	}
	public boolean isAllowInteract() {
		return allowInteract;
	}
	public Consumer<InventoryClickEvent> getConsumer(int slot) {
		return clicks.get(slot);
	}
	public PluginInventory setAllowInteract(boolean allow) {
		allowInteract =allow;
		return this;
	}
	public void clearConsumers() {
		clicks.clear();
	}
	@Override
	public Inventory getInventory() {
		return inv;
	}
	private void setInventory(Inventory inv) {
		this.inv = inv;
	}
	public static PluginInventory create(int size,String title) {
		if (Util.getVersionNumber() <= 8 && title.length() > 32) title = title.substring(0, 32);
		PluginInventory pl = new PluginInventory();
		Inventory inv = Bukkit.createInventory(pl, size, ChatColor.translateAlternateColorCodes('&', title));
		pl.setInventory(inv);
		return pl;
	}
	public static PluginInventory create(InventoryType type,String title) {
		if (Util.getVersionNumber() <= 8 && title.length() > 32) title = title.substring(0, 32);
		PluginInventory pl = new PluginInventory();
		Inventory inv = Bukkit.createInventory(pl, type, ChatColor.translateAlternateColorCodes('&', title));
		pl.setInventory(inv);
		if (!(inv.getHolder() instanceof PluginInventory)) {
			/*
			 * Seems the server doesn't hold the inventory holder
			 */
			try {
				Class<?> craftInventoryCustom = Util.craft("inventory.CraftInventoryCustom");
				Constructor<?> constr = craftInventoryCustom.getConstructor(InventoryHolder.class,InventoryType.class,String.class);
				inv = (Inventory)constr.newInstance(pl,type,ChatColor.translateAlternateColorCodes('&', title));
				pl.setInventory(inv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pl;
	}
	
	public static ItemStack create(XMaterial type,String display,String...lore) {
		return create(type,display,Arrays.asList(lore));
	}
	
	public static ItemStack create(XMaterial type,String display,List<String> lore) {
		ItemStack item = type.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', display));
		ArrayList<String> lores = new ArrayList<>();
		for (int i = 0 ; i < lore.size();i ++) {
			String a = lore.get(i);
			a = ChatColor.translateAlternateColorCodes('&', a);
			String last = null;
			try {
				for (String s : WordUtils.wrap(a, 30).split(System.lineSeparator())) {
					if (last == null) {
						lores.add(last=s);
					} else {
						lores.add(last=ChatColor.getLastColors(last)+s);
					}
				}
			} catch (Exception e) {
				lores.add(a);
			}
		}
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}

}
