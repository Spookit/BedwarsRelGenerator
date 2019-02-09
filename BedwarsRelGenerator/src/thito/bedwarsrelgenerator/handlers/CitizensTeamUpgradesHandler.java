package thito.bedwarsrelgenerator.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.sun.xml.internal.ws.util.StringUtils;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesHandler;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer.Cost;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;
import thito.breadcore.utils.StrUtil;

public class CitizensTeamUpgradesHandler implements UpgradesHandler<NPC> {

	public static final Random RANDOM = new Random();
	private final ConfigurationSection section;
	private final TeamHandler team;
	private final boolean enabled;
	private final Paginator<Upgrades> upgd= new Paginator<>(new ArrayList<>(Upgrades.UPGRADES),7);
	public CitizensTeamUpgradesHandler(ConfigurationSection sec,TeamHandler h) {
		section = sec;
		team = h;
		enabled = section.getBoolean("enabled");
	}
	public void changeNPC(Player p,NPC npc) {
		SkinnableEntity skinnableEntity= npc.getEntity() instanceof SkinnableEntity
				? (SkinnableEntity) npc.getEntity()
				: null;
		PluginInventory inv = PluginInventory.create(27, "&8&m--&1 NPC Settings &8&m--");
		int []contents = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,22,23,24,25,26};
		for (int c : contents) inv.getInventory().setItem(c, PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7"));
		int[] random = {11,12,13,14,15};
		for (int r : random) inv.getInventory().setItem(r, PluginInventory.create(XMaterial.fromString("STAINED_GLASS_PANE:"+RANDOM.nextInt(15)), "&7"));
		int[][] getter = {{11,12},{12,13},{13,14},{14,15}};
		if (skinnableEntity != null) {
			inv.getInventory().setItem(16, PluginInventory.create(XMaterial.PLAYER_HEAD, "&eChange Skin", 
					"&7Skin: &b"+skinnableEntity.getSkinName()
					));
			inv.addConsumer(16, e->{
				changeNPCSkin((Player)e.getWhoClicked(),skinnableEntity);
			});
		} else {
			inv.getInventory().setItem(16, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		inv.getInventory().setItem(10, PluginInventory.create(XMaterial.COW_SPAWN_EGG, "&eChange Type", "&7Type: &b"+npc.getEntity().getType().name()));
		inv.addConsumer(10, e->{
			changeNPCType((Player)e.getWhoClicked(),npc);
		});
		p.openInventory(inv.getInventory());
		new BukkitRunnable() {
			public void run() {
				InventoryView view = p.getOpenInventory();
				if (view ==null) cancel();
				Inventory ai = view.getTopInventory();
				if (ai == null) cancel();
				if (!(ai.getHolder() instanceof PluginInventory)) cancel();
				for (int[] g : getter) {
					inv.getInventory().setItem(g[0], inv.getInventory().getItem(g[1]));
				}
				inv.getInventory().setItem(15, PluginInventory.create(XMaterial.fromString("STAINED_GLASS_PANE:"+RANDOM.nextInt(15)), "&7"));
			}
		}.runTaskTimer(BWG.get(),2L,2L);
	}
	public void changeNPCType(Player p,NPC npc) {
		int size = 0;
		Set<EntityType> types = new HashSet<>();
		for (String s : section.getStringList("npc-types")) {
			try {
				types.add(EntityType.valueOf(s));
			} catch (Exception e) {
			}
		}
		for (int i = 0; i <types.size(); i+=9) size+=9;
		PluginInventory inv = PluginInventory.create(size, "&8&m--&1 Change NPC Type &8&m--");
		Iterator<EntityType> ty = types.iterator();
		for (int i = 0; i < size && ty.hasNext(); i++) {
			EntityType type = ty.next();
			ItemStack item = PluginInventory.create(XMaterial.EGG, "&a&l"+StringUtils.capitalize(type.name()));
			inv.getInventory().setItem(i, item);
			inv.addConsumer(i, e->{
				p.closeInventory();
				npc.setBukkitEntityType(type);
				p.sendMessage(ChatColor.YELLOW+"You changed this NPC type to "+type);
			});
		}
		p.openInventory(inv.getInventory());
	}
	
	public void changeNPCSkin(Player p,SkinnableEntity npc) {
		int size = 0;
		Set<String> types = new HashSet<>(section.getStringList("npc-skins"));
		for (int i = 0; i <types.size(); i+=9) size+=9;
		PluginInventory inv = PluginInventory.create(size, "&8&m--&1 Change NPC Skin &8&m--");
		Iterator<String> ty = types.iterator();
		for (int i = 0; i < size && ty.hasNext(); i++) {
			String type = ty.next();
			ItemStack item = PluginInventory.create(XMaterial.PLAYER_HEAD, "&a&l"+type);
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof SkullMeta) {
				((SkullMeta) meta).setOwner(type);
			}
			item.setItemMeta(meta);
			inv.getInventory().setItem(i, item);
			inv.addConsumer(i, e->{
				p.closeInventory();
				npc.setSkinName(type);
				p.sendMessage(ChatColor.YELLOW+"You changed this NPC skin to "+type);
			});
		}
		p.openInventory(inv.getInventory());
	}
	
	public void openShop(Player p,Upgrades up,int page,NPC npc) {
		if (!enabled) return;
		int size = up == null ? 18 : 36;
		PluginInventory inv = PluginInventory.create(size, section.getString("title"));
		if (upgd.isValidPage(page+1)) {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aNext Page &8]"));
			inv.addConsumer(0, e->{
				openShop((Player)e.getWhoClicked(),up,page+1,npc);
			});
		} else {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		if (upgd.isValidPage(page-1)) {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aPrevious Page &8]"));
			inv.addConsumer(8, e->{
				openShop((Player)e.getWhoClicked(),up,page-1,npc);
			});
		} else {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		List<Upgrades> upd = upgd.getPage(page);
		for (int i = 0; i < 8 && i < upd.size(); i++) {
			Upgrades d = upd.get(i);
			if (!d.getData().isEnabled()) continue;
			inv.getInventory().setItem(1+i, PluginInventory.create(d.getIcon(),"&d"+d.getData().getName()));
			inv.addConsumer(1+i, e->{
				openShop(p,d,page,npc);
			});
		}
		for (int i = 9; i < 18; i++) inv.getInventory().setItem(i, PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7"));
		inv.getInventory().setItem(13, PluginInventory.create(XMaterial.PLAYER_HEAD, "&a* Customize Shop NPC *","&7Click here to change the NPC skin"));
		inv.addConsumer(13, e->{
			changeNPC((Player)e.getWhoClicked(),npc);
		});
		if (up != null) {
			for (int i : new int[] {
					18, 26,
					27,28,29,30,31,32,33,34,35
			}) inv.getInventory().setItem(i, PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7"));
			for (int i : new int[] {
					20,21,22,23
			}) inv.getInventory().setItem(i, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
			UpgradeLevelContainer cons = up.getData().getLevels().get(up.getLevel(team));
			ItemStack current;
			if (cons != null) {
				current = PluginInventory.create(up.getIcon(), ChatColor.GREEN.toString()+ChatColor.BOLD+up.getData().getNarrowName()+" Status", 
						"&7Current Level: &b"+up.getLevel(team)+"/"+(up.getData().getLevels().size()-1),
						"&7Effects: &b"+Util.toString(cons.getEffects()),
						"&7Loadout: &b"+Util.toString(cons.getLoadouts())
						);
			} else {
				current = PluginInventory.create(XMaterial.IRON_BARS, "&7");
			}
			ItemStack next;
			UpgradeLevelContainer cons1 = up.getData().getLevels().get(up.getLevel(team) + 1);
			if (cons1 != null) {
				next = PluginInventory.create(up.getIcon(), ChatColor.YELLOW.toString()+up.getData().getNarrowName()+" &c[Next Level]", 
						"&7Level: &b"+(up.getLevel(team)+1)+"/"+(up.getData().getLevels().size()-1),
						"&7Effects: &b"+Util.toString(cons1.getEffects()),
						"&7Loadout: &b"+Util.toString(cons1.getLoadouts())
						);
			} else {
				next = PluginInventory.create(XMaterial.IRON_BARS, "&7");
			}
			ItemStack locked;
			UpgradeLevelContainer cons2 = up.getData().getLevels().get(up.getLevel(team) + 2);
			if (cons2 != null) {
				locked= PluginInventory.create(up.getIcon(), ChatColor.YELLOW.toString()+up.getData().getNarrowName()+" &c[LOCKED]", 
						"&7Level: &b"+(up.getLevel(team)+2)+"/"+(up.getData().getLevels().size()-1),
						"&7Effects: &b"+Util.toString(cons2.getEffects()),
						"&7Loadout: &b"+Util.toString(cons2.getLoadouts())
						);
			} else {
				locked= PluginInventory.create(XMaterial.IRON_BARS, "&7");
			}
			inv.getInventory().setItem(19, current);
			inv.getInventory().setItem(24, next);
			inv.getInventory().setItem(25, locked);
			ItemStack buy;
			if (cons1 != null) {
				buy = PluginInventory.create(XMaterial.EMERALD, "&aBuy Upgrades", 
						"&7Upgrade Level: "+(up.getLevel(team)+1),
						"&7Cost: "+cons1.getCost().getAmount()+"x "+cons1.getCost().getType().name());
				inv.addConsumer(22, e->{
					buyUpgrades((Player)e.getWhoClicked(),up,cons1,page,npc);
				});
			} else {
				buy = PluginInventory.create(XMaterial.IRON_BARS, "&7");
			}
			inv.getInventory().setItem(22, buy);
		}
		p.openInventory(inv.getInventory());
	}
	
	public void buyUpgrades(Player p,Upgrades up,UpgradeLevelContainer level,int page,NPC npc) {
		Cost cost = level.getCost();
		if (p.getInventory().contains(cost.getType().parseMaterial(), cost.getAmount())) {
			int remainCost = cost.getAmount();
			for (ItemStack item : p.getInventory().getContents()) {
				if (item ==null||item.getType() != cost.getType().parseMaterial()) continue;
				if (remainCost <= 0) break; 
				if (item.getAmount() <= remainCost) {
					remainCost -= item.getAmount();
					p.getInventory().removeItem(item);
				} else {
					ItemStack clu = item.clone();
					clu.setAmount(remainCost);
					p.getInventory().removeItem(clu);
				}
			};
			up.levelUp(team);
			team.getArenaHandler().announce(team.getTeam(), "upgrades", p.getName(),up.getData().getNarrowName(),up.getLevel(team));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', StrUtil.format(section.getString("purchased"), up.getData().getName(),up.getLevel(team))));
			openShop(p,up,page,npc);
		} else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', section.getString("not-enough")));
		}
	}
}
