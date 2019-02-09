package thito.bedwarsrelgenerator.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesHandler;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer.Cost;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;
import thito.breadcore.utils.StrUtil;

public class TeamUpgradesHandler implements UpgradesHandler<Entity> {

	public static final Random RANDOM = new Random();
	private final ConfigurationSection section;
	private final TeamHandler team;
	private final boolean enabled;
	private final Paginator<Upgrades> upgd= new Paginator<>(new ArrayList<>(Upgrades.UPGRADES),7);
	public TeamUpgradesHandler(ConfigurationSection sec,TeamHandler h) {
		section = sec;
		team = h;
		enabled = section.getBoolean("enabled");
	}

	
	public void openShop(Player p,Upgrades up,int page,Entity npc) {
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
	
	public void buyUpgrades(Player p,Upgrades up,UpgradeLevelContainer level,int page,Entity npc) {
		Cost cost = level.getCost();
		if (p.getInventory().contains(cost.getType().parseMaterial(), cost.getAmount())) {
			int remainCost = cost.getAmount();
			ItemStack[] contents = p.getInventory().getContents();
			for (int i = 0; i < contents.length; i++) {
				ItemStack item = contents[i];
				if (item ==null||item.getType() != cost.getType().parseMaterial()) continue;
				if (remainCost <= 0) break; 
				if (item.getAmount() < remainCost) {
					remainCost -= item.getAmount();
					contents[i] = null;
				} else {
					ItemStack clu = item.clone();
					clu.setAmount(clu.getAmount()-remainCost);
					contents[i] = clu;
					break;
				}
			}
			p.getInventory().setContents(contents);
			up.levelUp(team);
			team.getArenaHandler().announce(team.getTeam(), "upgrades", p.getName(),up.getData().getNarrowName(),up.getLevel(team));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', StrUtil.format(section.getString("purchased"), up.getData().getName(),up.getLevel(team))));
			openShop(p,up,page,npc);
		} else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', section.getString("not-enough")));
		}
	}
}
