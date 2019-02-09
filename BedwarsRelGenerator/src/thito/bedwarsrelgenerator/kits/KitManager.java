package thito.bedwarsrelgenerator.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;
import thito.breadcore.utils.StrUtil;

public class KitManager implements CommandExecutor {

	private final Set<Kit> kits = new HashSet<>();
	private final String prefix = StrUtil.color("&8[&aBWG&8] &7");
	private final ItemStack BARS = PluginInventory.create(XMaterial.IRON_BARS, "&7");
	private final ItemStack BORDER = PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7");
	private final ItemStack NEXT = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lNEXT&8 ]");
	private final ItemStack PREV = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lPREVIOUS&8 ]");
	private final int[] borders = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};
	private final int[] contents = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,26,28,29,30,31,32,33,34,37,38,39,40,41,42,43,44};
	public Kit get(String name) {
		for (Kit k : kits) {
			if (k.getName().equals(name)) {
				return k;
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command var2, String var3, String[] args) {
		if (!sender.hasPermission("bedwarsrelgenerator.admin")) {
			sender.sendMessage(prefix+"You don't have permission to do this!");
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length > 1) {
					Kit kit = new Kit(args[1]);
					if (kits.contains(kit)) {
						sender.sendMessage(prefix+"Kit with name '"+args[1]+"' is already exists!");
						return true;
					}
					kits.add(kit);
					sender.sendMessage(prefix+"Kit has just created! Do '/bwgkit edit "+args[1]+"' to edit it.");
					sender.sendMessage(prefix+"Saving...");
					save();
					sender.sendMessage(prefix+"Saved!");
					return true;
				}
				sender.sendMessage(prefix+"Create a kit. Usage: /bwgkit create <name>");
				return true;
			}
			if (args[0].equalsIgnoreCase("edit")) {
				if (args.length > 1) {
					Kit k = get(args[1]);
					if (k == null) {
						sender.sendMessage(prefix+"Invalid Kit: "+args[1]);
						return true;
					}
					if (!(sender instanceof Player)) {
						sender.sendMessage(prefix+"You must be a player to do this!");
						return true;
					}
					this.edit((Player)sender, k);
					return true;
				}
				sender.sendMessage(prefix+"Edit a kit. Usage: /bwgkit edit <kit>");
				return true;
			}
			if (args[0].equalsIgnoreCase("list")) {
				if (kits.isEmpty()) {
					sender.sendMessage(prefix+"Empty :P");
					return true;
				}
				sender.sendMessage(StrUtil.color("&7&m---------------------&8[ &aKit List &8]&7&m------------------"));
				for (Kit k : kits) {
					sender.sendMessage(StrUtil.color("&7- &e"+k.getName()));
				}
				return true;
 			}
			if (args[0].equalsIgnoreCase("rename")) {
				if (args.length > 2) {
					String from = args[1];
					String to = args[2];
					Kit k = get(from);
					if (k == null) {
						sender.sendMessage(prefix+"Kit not found: "+from);
						return true;
					}
					if (get(to) != null) {
						sender.sendMessage(prefix+"Name replacement for that kit is already exists!");
						return true;
					}
					k.setName(to);
					sender.sendMessage(prefix+"Kit '"+from+"' has been renamed to '"+to+"'");
					sender.sendMessage(prefix+"Saving...");
					save();
					sender.sendMessage(prefix+"Saved!");
					return true;
				}
				sender.sendMessage(prefix+"Rename a kit. Usage: /bwgkit rename <from> <to>");
				return true;
			}
			if (args[0].equalsIgnoreCase("delete")) {
				if (args.length > 1) {
					Kit k = get(args[1]);
					if (k == null) {
						sender.sendMessage(prefix+"Kit not found: "+args[1]);
						return true;
					}
					if (args.length > 2) {
						kits.remove(k);
						sender.sendMessage(prefix+"Kit '"+args[1]+"' has been deleted!");
						sender.sendMessage(prefix+"Saving...");
						save();
						sender.sendMessage(prefix+"Saved!");
						return true;
					}
					sender.sendMessage(prefix+"Please do /bwgkit delete "+args[1]+" CONFIRM");
					return true;
				}
				sender.sendMessage(prefix+"Delete a kit. Usage: /bwgkit delete <name>");
				return true;
			}
			if (args[0].equalsIgnoreCase("save")) {
				sender.sendMessage(prefix+"Saving...");
				save();
				sender.sendMessage(prefix+"Saved!");
				return true;
			}
			if (args[0].equalsIgnoreCase("setpermission")) {
				if (args.length > 2) {
					String from = args[1];
					String to = args[2];
					Kit k = get(from);
					if (k == null) {
						sender.sendMessage(prefix+"Kit not found: "+from);
						return true;
					}
					k.setPermission(to.equalsIgnoreCase("default") ? null : to);
					sender.sendMessage(prefix+"Kit '"+from+"' permission has been changed to '"+to+"'");
					sender.sendMessage(prefix+"Saving...");
					save();
					sender.sendMessage(prefix+"Saved!");
					return true;
				}
				sender.sendMessage(prefix+"Set kit permission");
				return true;
			}
		}
		sender.sendMessage(new String[] {
				StrUtil.color("&7&m---------------------&8[ &aKit Manager &8]&7&m------------------"),
				StrUtil.color("&e/bwgkit create <name> &f- Create a kit"),
				StrUtil.color("&e/bwgkit edit <name> &f- Edit a kit"),
				StrUtil.color("&e/bwgkit list &f- Shows available kits"),
				StrUtil.color("&e/bwgkit rename <name> <to> &f- Rename a kit"),
				StrUtil.color("&e/bwgkit setpermission <name> <perm|default> &f- Set kit permission"),
				StrUtil.color("&e/bwgkit delete <name> &f- delete a kit"),
				StrUtil.color("&e/bwgkit save &f- Save the kit datas")
		});
		return true;
	}
	
	public boolean hasKit(ArenaHandler h) {
		for (Kit k : kits) {
			if (k.getAllowedArenas().contains(h.getGame().getName())) return true;
		}
		return false;
	}
	public void view(Player p,Kit k,int page,Consumer<Kit> selector,Runnable back) {
		PluginInventory inv = PluginInventory.create(54, "&8&m--&1 Kit Preview &8&m--");
		for (int i : borders) {
			inv.getInventory().setItem(i, BORDER);
		}
		ArrayList<ItemHolder> list = new ArrayList<>();
		for (ItemStack a : k.getItems()) {
			list.add(new ItemHolder() {
				
				@Override
				public void accept(InventoryClickEvent t) {
				}

				@Override
				public ItemStack getItem() {
					return a;
				}
			});
		}
		Paginator<ItemHolder> items = new Paginator<>(list,contents.length);
		inv.getInventory().setItem(48, PluginInventory.create(XMaterial.BOOK, "&7Kit: &b&l"+k.getName()));
		inv.getInventory().setItem(49, PluginInventory.create(XMaterial.CHEST, "&a&lSELECT KIT", "&7Click here to select this kit"));
		inv.getInventory().setItem(50, PluginInventory.create(XMaterial.CHEST_MINECART, "&a&lGO BACK", "&7Click here to go back to the kit selector"));
		inv.addConsumer(49, e->{
			selector.accept(k);
			p.closeInventory();
		});
		inv.addConsumer(50, e->{
			back.run();
		});
		List<ItemHolder> it = items.getPage(page);
		for (int i = 0; i < contents.length && i < it.size(); i++) {
			inv.getInventory().setItem(contents[i], it.get(i).getItem());
			inv.addConsumer(contents[i], it.get(i));
		}
		if (items.isValidPage(page+1)) {
			inv.getInventory().setItem(51, NEXT);
			inv.addConsumer(51, e->{
				view(p,k,page+1,selector,back);
			});
		}
		if (items.isValidPage(page-1)) {
			inv.getInventory().setItem(47, PREV);
			inv.addConsumer(47, e->{
				view(p,k,page-1,selector,back);
			});
		}
		p.openInventory(inv.getInventory());
	}
	public void select(Player p,ArenaHandler h,Consumer<Kit> selected,int page) {
		PluginInventory inv = PluginInventory.create(54, "&8&m--&1 Kit Selector &8&m--");
		for (int i : borders) {
			inv.getInventory().setItem(i, BORDER);
		}
		ArrayList<ItemHolder> list = new ArrayList<>();
		for (Kit a : kits) {
			if (!a.getAllowedArenas().contains(h.getGame().getName())) continue;
			list.add(new ItemHolder() {
				
				@Override
				public void accept(InventoryClickEvent t) {
					if (t.isRightClick()) {
						view(p,a,0,selected,()->{
							select(p,h,selected,page);
						});
						return;
					}
					selected.accept(a);
					p.closeInventory();
				}

				@Override
				public ItemStack getItem() {
					return PluginInventory.create(XMaterial.ENDER_CHEST, "&e"+a.getName(), "&7Left Click to select this kit","&7Right Click to view it");
				}
			});
		}
		Paginator<ItemHolder> items = new Paginator<>(list,contents.length);
		List<ItemHolder> it = items.getPage(page);
		for (int i = 0; i < contents.length && i < it.size(); i++) {
			inv.getInventory().setItem(contents[i], it.get(i).getItem());
			inv.addConsumer(contents[i], it.get(i));
		}
		if (items.isValidPage(page+1)) {
			inv.getInventory().setItem(51, NEXT);
			inv.addConsumer(51, e->{
				select(p,h,selected,page+1);
			});
		}
		if (items.isValidPage(page-1)) {
			inv.getInventory().setItem(47, PREV);
			inv.addConsumer(47, e->{
				select(p,h,selected,page-1);
			});
		}
		p.openInventory(inv.getInventory());
	}
	
	public void edit(Player p,Kit k) {
		PluginInventory inv = PluginInventory.create(InventoryType.HOPPER, StrUtil.color("&8&m--&1 Kit Editor &8&m--"));
		inv.getInventory().setItem(0, PluginInventory.create(XMaterial.BOOK,"&8[ &a&lINFORMATION&8 ]",
				"&7Kit Name: &b"+k.getName(),
				"&7Kit Permission: &b"+(k.getPermission() == null ? "&cnone" : k.getPermission()),
				"&7Allow Loadouts: &a"+(k.isAllowLoadouts() ? "Yes" : "&cNo"),
				"&7Allowed Arenas: &b"+(Util.toString(k.getAllowedArenas())),
				"&7Total Items: &b"+k.getItems().size()
				));
		inv.getInventory().setItem(1, PluginInventory.create(XMaterial.BUCKET, "&8[ &a&lTOGGLE ALLOW LOADOUTS&8 ]", "&7Current State: &a"+(k.isAllowLoadouts() ? "Yes" : "&cNo")));
		inv.addConsumer(1, e->{
			k.setAllowLoadouts(!k.isAllowLoadouts());
			p.sendMessage(prefix+"Saving...");
			save();
			p.sendMessage(prefix+"Saved!");
			edit(p,k);
		});
		inv.getInventory().setItem(2, PluginInventory.create(XMaterial.IRON_SWORD, "&8[ &a&lALLOWED ARENAS&8 ]", "&7Current Value: &b"+Util.toString(k.getAllowedArenas())));
		inv.addConsumer(2, e->{
			editArenas(p,k,0);
		});
		inv.getInventory().setItem(3, PluginInventory.create(XMaterial.CHEST, "&8[ &a&lITEMS&8 ]", "&7Current Total Items: &b"+k.getItems().size()));
		inv.addConsumer(3,e->{
			editItems(p,k);
		});
		inv.getInventory().setItem(4, PluginInventory.create(XMaterial.ARROW, "&8[ &c&lCLOSE&8 ]", "&7Close this window"));
		inv.addConsumer(4, e->{
			e.getWhoClicked().closeInventory();
		});
		p.openInventory(inv.getInventory());
	}
	
	public void editItems(Player p,Kit k) {
		PluginInventory inv = PluginInventory.create(54, "&8&m--&1 Kit Items &8&m--");
		inv.getInventory().setItem(45, PluginInventory.create(XMaterial.BEACON, "&a&lSAVE"));
		inv.setAllowInteract(true);
		for (ItemStack i : k.getItems()) {
			inv.getInventory().addItem(i);
		}
		inv.addConsumer(45, e->{
			e.setCancelled(true);;
			k.getItems().clear();
			for (int i = 0; i < 45; i++) {
				ItemStack item = inv.getInventory().getItem(i);
				if (item == null) continue;
				k.getItems().add(item);
			}
			p.sendMessage(prefix+"Saving...");
			save();
			p.sendMessage(prefix+"Saved!");
			edit(p,k);
		});
		for (int i = 46; i < 54; i++) {
			inv.getInventory().setItem(i, BARS);
			inv.addConsumer(i, e->{ 
				e.setCancelled(true);
			});
		}
		p.openInventory(inv.getInventory());
	}
	
	public void editArenas(Player p,Kit k,int page) {
		PluginInventory inv = PluginInventory.create(54, "&8&m--&1 Kit Arenas &8&m--");
		for (int i : borders) {
			inv.getInventory().setItem(i, BORDER);
		}
		ArrayList<ItemHolder> list = new ArrayList<>();
		for (String a : k.getAllowedArenas()) {
			list.add(new ItemHolder() {
				
				@Override
				public void accept(InventoryClickEvent t) {
					if (t.isRightClick()) {
						k.getAllowedArenas().remove(a);
						p.sendMessage(prefix+"Saving...");
						save();
						p.sendMessage(prefix+"Saved!");
						editArenas(p,k,page);
					}
				}

				@Override
				public ItemStack getItem() {
					return PluginInventory.create(XMaterial.IRON_CHESTPLATE, "&e"+a, "&7Right Click to remove","&7Status: &a"+(BedwarsRel.getInstance().getGameManager().getGame(a) == null ? "&cUnknown Arena" : "Available"));
				}
			});
		}
		list.add(new ItemHolder() {

			@Override
			public void accept(InventoryClickEvent t) {
				addArenas(p,k,0);
			}

			@Override
			public ItemStack getItem() {
				return PluginInventory.create(XMaterial.NETHER_STAR, "&a&lADD +", "&7Add an arena to the list");
			}
			
		});
		Paginator<ItemHolder> items = new Paginator<>(list,contents.length);
		List<ItemHolder> it = items.getPage(page);
		for (int i = 0; i < contents.length && i < it.size(); i++) {
			inv.getInventory().setItem(contents[i], it.get(i).getItem());
			inv.addConsumer(contents[i], it.get(i));
		}
		if (items.isValidPage(page+1)) {
			inv.getInventory().setItem(51, NEXT);
			inv.addConsumer(51, e->{
				editArenas(p,k,page+1);
			});
		}
		if (items.isValidPage(page-1)) {
			inv.getInventory().setItem(47, PREV);
			inv.addConsumer(47, e->{
				editArenas(p,k,page-1);
			});
		}
		p.openInventory(inv.getInventory());
	}
	
	public void addArenas(Player p,Kit k,int page) {
		PluginInventory inv = PluginInventory.create(54, "&8&m--&1 Add Arena &8&m--");
		for (int i : borders) {
			inv.getInventory().setItem(i, BORDER);
		}
		ArrayList<ItemHolder> list = new ArrayList<>();
		for (Game a : BedwarsRel.getInstance().getGameManager().getGames()) {
			list.add(new ItemHolder() {
				
				@Override
				public void accept(InventoryClickEvent t) {
					k.getAllowedArenas().add(a.getName());
					editArenas(p,k,0);
					p.sendMessage(prefix+"Saving...");
					save();
					p.sendMessage(prefix+"Saved!");
				}

				@Override
				public ItemStack getItem() {
					return PluginInventory.create(XMaterial.IRON_CHESTPLATE, "&e"+a.getName(), "&7Click to add");
				}
			});
		}
		Paginator<ItemHolder> items = new Paginator<>(list,contents.length);
		List<ItemHolder> it = items.getPage(page);
		for (int i = 0; i < contents.length && i < it.size(); i++) {
			inv.getInventory().setItem(contents[i], it.get(i).getItem());
			inv.addConsumer(contents[i], it.get(i));
		}
		if (items.isValidPage(page+1)) {
			inv.getInventory().setItem(51, NEXT);
			inv.addConsumer(51, e->{
				addArenas(p,k,page+1);
			});
		}
		if (items.isValidPage(page-1)) {
			inv.getInventory().setItem(47, PREV);
			inv.addConsumer(47, e->{
				addArenas(p,k,page-1);
			});
		}
		p.openInventory(inv.getInventory());
	}
	public static abstract class ItemHolder implements Consumer<InventoryClickEvent> {
		public abstract ItemStack getItem();
	}
	public synchronized void load() {
		try {
			File file = new File(BWG.get().getDataFolder(),"kits.dat");
			if (!file.exists()) return;
			BukkitObjectInputStream str = new BukkitObjectInputStream(new FileInputStream(file));
			Object obj = str.readObject();
			kits.clear();
			if (obj instanceof Set){
				for (Object o : (Set<?>)obj) {
					if (o instanceof Kit) {
						kits.add((Kit)o);
					}
				}
			}
			str.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public synchronized void save() {
		try {
			File file = new File(BWG.get().getDataFolder(),"kits.dat");
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			BukkitObjectOutputStream str = new BukkitObjectOutputStream(new FileOutputStream(file));
			str.writeObject(kits);
			str.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
