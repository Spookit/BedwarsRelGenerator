package thito.bedwarsrelgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;

public class BedwarsSelector implements CommandExecutor {

	private static final Random RANDOM = new Random();
	public static void main(String[]args) {
		Map<String,Integer> something = new HashMap<>();
		int s = something.get("something");
		System.out.println(s);
	}
	public static void open(Player p,int page) {
		if (BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p) != null && !p.hasPermission("bedwarsrelgenerator.admin")) {
			p.sendMessage(ChatColor.RED+"You cannot do this while in a bedwars game");
			return;
		}
		Paginator<Integer> categories = new Paginator<>(new ArrayList<>(byCategory().keySet()),7);
		int size = 18;
		PluginInventory inv = PluginInventory.create(size, BWG.get().getConfig().getString("game-selector.title-category"));
		if (categories.isValidPage(page+1)) {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aNext Page &8]"));
			inv.addConsumer(0, e->{
				open((Player)e.getWhoClicked(),page+1);
			});
		} else {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		if (categories.isValidPage(page-1)) {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aPrevious Page &8]"));
			inv.addConsumer(8, e->{
				open((Player)e.getWhoClicked(),page-1);
			});
		} else {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		List<Integer> upd = categories.getPage(page);
		for (int i = 0; i < 8 && i < upd.size(); i++) {
			Integer d = upd.get(i);
			inv.getInventory().setItem(1+i, getItem(d,byCategory().get(d).size()));
			inv.addConsumer(1+i, e->{
				openCategory((Player)e.getWhoClicked(),0,d);
			});
		}
		for (int i = 9; i < 18; i++) inv.getInventory().setItem(i, PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7"));
		inv.getInventory().setItem(13, PluginInventory.create(XMaterial.PLAYER_HEAD, "&a* Random Join *","&7Click here to join random arena!"));
		inv.addConsumer(13, e->{
			List<Game> games = BedwarsRel.getInstance().getGameManager().getGames();
			if (!games.isEmpty()) {
				join((Player)e.getWhoClicked(),games.get(RANDOM.nextInt(games.size())));
			}
		});
		p.openInventory(inv.getInventory());
	}
	
	public static void join(Player p,Game g) {
		g.playerJoins(p);
	}
	
	public static void openCategory(Player p,int page,int category) {
		if (BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p) != null && !p.hasPermission("bedwarsrelgenerator.admin")) {
			p.sendMessage(ChatColor.RED+"You cannot do this while in a bedwars game");
			return;
		}
		Paginator<Game> categories = new Paginator<>(byCategory().get(category),7);
		int size = 18;
		PluginInventory inv = PluginInventory.create(size, BWG.get().getConfig().getString("game-selector.title-game").replace("<category>", category+" vs "+category));
		if (categories.isValidPage(page+1)) {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aNext Page &8]"));
			inv.addConsumer(0, e->{
				openCategory((Player)e.getWhoClicked(),page+1,category);
			});
		} else {
			inv.getInventory().setItem(0, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		if (categories.isValidPage(page-1)) {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &aPrevious Page &8]"));
			inv.addConsumer(8, e->{
				openCategory((Player)e.getWhoClicked(),page-1,category);
			});
		} else {
			inv.getInventory().setItem(8, PluginInventory.create(XMaterial.IRON_BARS, "&7"));
		}
		List<Game> upd = categories.getPage(page);
		for (int i = 0; i < 8 && i < upd.size(); i++) {
			Game d = upd.get(i);
			inv.getInventory().setItem(1+i, getItem(d.getState(),d));
			inv.addConsumer(1+i, e->{
				join((Player)e.getWhoClicked(),d);
			});
		}
		for (int i = 9; i < 18; i++) inv.getInventory().setItem(i, PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7"));
		inv.getInventory().setItem(13, PluginInventory.create(XMaterial.PLAYER_HEAD, "&a* Random Join *","&7Click here to join random arena!"));
		inv.addConsumer(13, e->{
			List<Game> games = byCategory().get(category);
			if (!games.isEmpty()) {
				join((Player)e.getWhoClicked(),games.get(RANDOM.nextInt(games.size())));
			}
		});
		p.openInventory(inv.getInventory());
		new BukkitRunnable() {
			public void run() {
				if (p.getOpenInventory() == null || 
						p.getOpenInventory().getTopInventory() == null ||
						!(inv.equals(p.getOpenInventory().getTopInventory().getHolder()))) {
					cancel();
					return;
				}
				for (int i = 0; i < 8 && i < upd.size(); i++) {
					Game d = upd.get(i);
					inv.getInventory().setItem(1+i, getItem(d.getState(),d));
					inv.addConsumer(1+i, e->{
						join((Player)e.getWhoClicked(),d);
					});
				}
				p.updateInventory();
			}
		}.runTaskTimer(BWG.get(), 20L, 20L);
	}
	
	public static Map<Integer,List<Game>> byCategory() {
		Map<Integer,List<Game>> games = new HashMap<>();
		for (Game g : BedwarsRel.getInstance().getGameManager().getGames()) {
			if (!g.getTeams().isEmpty()) {
				int cat = new ArrayList<>(g.getTeams().values()).get(0).getMaxPlayers();
				List<Game> game = games.get(cat);
				if (game == null) {
					games.put(cat, game = new ArrayList<>());
				}
				game.add(g);
			}
		}
		return games;
	}
	public static String perm() {
		return BWG.get().getConfig().getString("game-selector.permission");
	}
	
	public static ItemStack getItem(int category,int size) {
		ConfigurationSection sec = BWG.get().getConfig().getConfigurationSection("game-selector.category-item");
		if (sec == null) return XMaterial.AIR.parseItem();
		ItemStack item = XMaterial.fromString(sec.getString("type")).parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', sec.getString("display").replace("<category>", category+" vs "+category)));
		List<String> lore = sec.getStringList("lore");
		if (lore == null) lore = new ArrayList<>();
		lore.replaceAll(a->{
			a = a.replace("<games>", size+"");
			a = ChatColor.translateAlternateColorCodes('&', a);
			return a;
		});
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItem(GameState st,Game g) {
		ConfigurationSection sec = BWG.get().getConfig().getConfigurationSection("game-selector.game-item-"+st.name().toLowerCase());
		if (sec == null) return XMaterial.AIR.parseItem();
		ItemStack item = XMaterial.fromString(sec.getString("type")).parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', sec.getString("display",g.getName()).replace("<name>", g.getName())));
		List<String> lore = sec.getStringList("lore");
		if (lore == null) lore = new ArrayList<>();
		lore.replaceAll(a->{
			a = a.replace("<players>", g.getPlayers().size()+"");
			a = a.replace("<max>", g.getMaxPlayers()+"");
			a = a.replace("<remains>", (g.getMinPlayers()-g.getPlayers().size())+"");
			a = a.replace("<timeleft>", f(g));
			a = a.replace("<time>", f(g));
			a = ChatColor.translateAlternateColorCodes('&', a);
			return a;
		});
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static String f(Game g) {
		int min = 0;
		int sec = 0;
		String minStr = "";
		String secStr = "";
		min = (int) Math.floor(g.getTimeLeft() / 60);
		sec = g.getTimeLeft() % 60;
		minStr = min < 10 ? "0" + String.valueOf(min) : String.valueOf(min);
		secStr = sec < 10 ? "0" + String.valueOf(sec) : String.valueOf(sec);
		return minStr + ":" + secStr;
	}

	@Override
	public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
		if (var1 instanceof Player) {
			open((Player)var1,0);
			return true;
		}
		var1.sendMessage(ChatColor.RED+"You must be a player to do this!");
		return true;
	}
	
}
