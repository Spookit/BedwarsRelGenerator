package thito.bedwarsrelgenerator.tracker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.CategorizedGUI;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.handlers.TeamHandler;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;

public class TrackingGUI implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void interact(PlayerInteractEvent e) {
		if (e.getAction() != Action.PHYSICAL) {
			final ItemStack item = e.getItem();
			final Player p = e.getPlayer();
			final Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
			if (g == null) return;
			ArenaHandler a = BWG.get(g);
			if (a == null) return;
			if (item != null && item.getType() == Material.COMPASS) {
				openGUI(p,a);
				e.setCancelled(true);
			}
		}
	}
	
	public void openGUI(Player p,ArenaHandler handler) {
		Map<TeamHolder,List<PlayerHolder>> teams = new LinkedHashMap<>();
		teams.put(new TeamHolder(null), new ArrayList<>());
		for (TeamHandler h : handler.getTeams()) {
			ArrayList<PlayerHolder> holders = new ArrayList<>();
			for (Player pl : h.getSubscribers()) {
				holders.add(new PlayerHolder(pl));
			}
			teams.put(new TeamHolder(h), holders);
		}
		CategorizedGUI<TeamHolder,PlayerHolder> gui = new CategorizedGUI<TrackingGUI.TeamHolder, TrackingGUI.PlayerHolder>(teams,"&8&m--&1 Tracker &8&m--") {
			
			@Override
			public void onSelect(Player p, InventoryClickEvent e, TeamHolder category, PlayerHolder value) {
				TrackerHandler h = handler.getTrack(p);
				if (h != null) {
					h.setTracking(value.p);
					p.sendMessage(ChatColor.GREEN+"You are now tracking: "+category.t.getTeam().getChatColor()+ChatColor.BOLD.toString()+category.t.getTeam().getName()+ChatColor.WHITE+" "+value.p.getName());
					p.closeInventory();
				}
			}
			
			@Override
			public ItemStack itemValue(PlayerHolder value) {
				return value.getItem();
			}
			
			@Override
			public ItemStack itemCategory(TeamHolder category) {
				return category.getItem();
			}

			@Override
			public void onSelectCategory(Player p, InventoryClickEvent e, TeamHolder category) {
				TrackerHandler h = handler.getTrack(p);
				if (h != null && category.t == null) {
					Player tr = h.getTracking();
					TeamHandler team = h.getArenaHandler().getByPlayer(tr);
					if (tr == null || team == null) {
						p.sendMessage(ChatColor.RED+"You are not tracking anyone!");
						p.closeInventory();
						return;
					}
					p.sendMessage(ChatColor.YELLOW+"You are no longer tracking: "+team.getTeam().getChatColor()+ChatColor.BOLD.toString()+team.getTeam().getName()+" "+ChatColor.WHITE+tr.getName());
					h.setTracking(null);
					p.closeInventory();
				}
			}
		};
		gui.open(p, null, 0, 0);
	};
	
	public static class TeamHolder {
		final TeamHandler t;
		public TeamHolder(TeamHandler t) {
			this.t = t;
		}
		public ItemStack getItem() {
			if (t == null) {
				return PluginInventory.create(XMaterial.PLAYER_HEAD, "&c&lRESET TRACKER", "&7Reset the tracking data");
			}
			List<String> lore = new ArrayList<>();
			for (Player p : t.getSubscribers()) {
				lore.add("&7- "+p.getName());
			}
			return PluginInventory.create(XMaterial.fromString(t.getTeam().getColor().getDyeColor()+"_WOOL"), t.getTeam().getChatColor()+"&l"+t.getTeam().getName(),lore);
		}
	}
	
	public static class PlayerHolder {
		final Player p;
		public PlayerHolder(Player p) {
			this.p = p;
		}
		public ItemStack getItem() {
			return PluginInventory.create(XMaterial.PLAYER_HEAD, "&e&l"+p.getName());
		}
	}
	
}
