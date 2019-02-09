package thito.bedwarsrelgenerator.trails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;
import thito.breadcore.utils.StrUtil;

public class ProjectileTrail extends BukkitRunnable implements Listener, CommandExecutor {

	private Map<String,Effect> cache;
	private final Map<Effect.Type,List<Effect>> category = new HashMap<>();
	private final ItemStack BARS = PluginInventory.create(XMaterial.IRON_BARS, "&7");
	private final ItemStack BORDER = PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7");
	private final ItemStack VISUAL = PluginInventory.create(XMaterial.ENDER_EYE, "&a&lVISUAL", "&7Visualized effects","&7This effect is also make sounds");
	private final ItemStack SOUND = PluginInventory.create(XMaterial.DROPPER, "&a&lSOUND", "&7Invisible effects","&7but does make sounds");
	private final ItemStack PARTICLE = PluginInventory.create(XMaterial.PRISMARINE_SHARD, "&a&lPARTICLES", "&7Shows particles, silently");
	private final ItemStack NEXT = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lNEXT&8 ]");
	private final ItemStack PREV = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lPREVIOUS&8 ]");
	private final int[] borders = {0,1,2,3,4,5,6,7,8,
									9,10,   16,17,
								   18,19,20,21,22,23,24,25,26,
								   27,                     35,
								   36,                     44,
								   45,46,47,48,49,50,51,52,53};
	private final int[] contents = {28,29,30,31,32,33,34,37,38,39,40,41,42,43};
	
	{
		for (Effect eff : Effect.values()) {
			Effect.Type type = eff.getType();
			List<Effect> effects = category.get(type);
			if (effects == null) {
				category.put(type, effects = new ArrayList<>());
			}
			effects.add(eff);
		}
	}
	
	public ProjectileTrail() {
		File f = new File(BWG.get().getDataFolder(),"projectiledatas.dat");
		if (f.exists()) {
			try {
				ObjectInputStream str = new ObjectInputStream(new FileInputStream(f));
				Object o = str.readObject();
				if (o instanceof Map) {
					cache = (Map<String,Effect>)o;
				}
				str.close();
			} catch (Exception e) {
				cache = new HashMap<>();
			}
		} else cache = new HashMap<>();
	}
	
	public void open(Player p,Effect.Type category,int page) {
		if (!p.hasPermission("bedwarsrelgenerator.use.trails")) {
			p.sendMessage(ChatColor.RED+"You don't have permission to use ProjectileTrails!");
			return;
		}
		if (category == null) {
			PluginInventory inv = PluginInventory.create(InventoryType.HOPPER, StrUtil.color("&8&m--&1 Trails - Category &8&m--"));
			inv.getInventory().setItem(0, BARS);
			inv.getInventory().setItem(1, VISUAL);
			inv.getInventory().setItem(4, BARS);
			inv.addConsumer(1, a->{
				open(p,Effect.Type.VISUAL,0);
			});
			try {
				inv.getInventory().setItem(2, SOUND);
				inv.addConsumer(2, a->{
					open(p,Effect.Type.SOUND,0 );
				});
				inv.getInventory().setItem(3, PARTICLE);
				inv.addConsumer(3, a->{
					open(p,Effect.Type.valueOf("PARTICLE"),0);
				});
			} catch (Exception e) {
				inv.getInventory().setItem(2, BORDER);
				inv.getInventory().setItem(3, SOUND);
				inv.addConsumer(3, a->{
					open(p,Effect.Type.SOUND,0 );
				});
			}
			p.openInventory(inv.getInventory());
		} else {
			List<Effect> effects = this.category.get(category);
			if (effects == null) {
				open(p,null,0);
				return;
			}
			PluginInventory inv = PluginInventory.create(54, StrUtil.color("&8&m--&1 Trails - "+category.name()+" &8&m--"));
			for (int b : borders) {
				inv.getInventory().setItem(b, BORDER);
			}
			inv.getInventory().setItem(11, BARS);
			inv.getInventory().setItem(12, VISUAL);
			inv.getInventory().setItem(15, BARS);
			inv.addConsumer(12, a->{
				open(p,Effect.Type.VISUAL,0);
			});
			try {
				inv.getInventory().setItem(13, SOUND);
				inv.addConsumer(13, a->{
					open(p,Effect.Type.SOUND,0 );
				});
				inv.getInventory().setItem(14, PARTICLE);
				inv.addConsumer(14, a->{
					open(p,Effect.Type.valueOf("PARTICLE"),0);
				});
			} catch (Exception e) {
				inv.getInventory().setItem(13, BORDER);
				inv.getInventory().setItem(14, SOUND);
				inv.addConsumer(15, a->{
					open(p,Effect.Type.SOUND,0 );
				});
			}
			Paginator<Effect>effs = new Paginator<>(effects,contents.length);
			List<Effect> eff = effs.getPage(page);
			for (int i = 0; i < contents.length && i < eff.size(); i++) {
				final int index = i;
				inv.getInventory().setItem(contents[i], PluginInventory.create(XMaterial.POTION, "&a&l"+StrUtil.capitalizeEnum(eff.get(i)),"&7Click here to set your projectile trails"));
				inv.addConsumer(contents[i], e->{
					cache.put(p.getName(), eff.get(index));
					p.sendMessage(ChatColor.GREEN+"You selected "+StrUtil.capitalizeEnum(eff.get(index))+" trail effect.");
					open(p,category,page);
				});
			}
			if (effs.isValidPage(page-1)) {
				inv.getInventory().setItem(47, PREV);
				inv.addConsumer(47, e->{
					open(p,category,page-1);
				});
			}
			if (effs.isValidPage(page+1)) {
				inv.getInventory().setItem(51, NEXT);
				inv.addConsumer(51, e->{
					open(p,category,page+1);
				});
			}
			inv.getInventory().setItem(49, PluginInventory.create(XMaterial.ENDER_EYE, "&7Current: &b"+(cache.get(p.getName()) != null ? StrUtil.capitalizeEnum(cache.get(p.getName())) : "&cNone"),"&7","&c* Right Click to remove current trail effect"));
			inv.addConsumer(49, e->{
				if (e.isRightClick()) {
					cache.remove(p.getName());
					open(p,category,page);
				}
			});
			p.openInventory(inv.getInventory());
		}
	}
	
	@EventHandler
	public void onPress(PlayerInteractEvent e) { 
		if (e.getItem() != null) {
			Player pl = e.getPlayer();
			Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(pl);
			if (game == null) return;
			ArenaHandler handler = BWG.get(game);
			if (handler == null) return;
			if (handler.getByPlayer(pl) == null) return;
			if (!isProjectile(e.getItem().getType())) return;
			switch (e.getAction()) {
			case LEFT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				open(pl,null,0);
			default:
			}
		}
	}
	
	public boolean isProjectile(Material item) {
		try {
			String type = item.name();
			if (type.startsWith("LEGACY_")) {
				type = type.substring(7);
			}
			type = fixCapitalize(type);
			final Class<?> bukkitClass = Class.forName("org.bukkit.entity." + type);
			return Projectile.class.isAssignableFrom(bukkitClass);
		} catch (final Exception e) {
		}
		return false;
	}
	
	public String fixCapitalize(String n) {
		String b = new String();
		for (final String s : n.split("_")) {
			if (s.isEmpty()) {
				continue;
			}
			if (s.length() > 1) {
				b += s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
			} else {
				b += s.toUpperCase();
			}
		}
		return b;
	}
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent e) {
		final Projectile pro = e.getEntity();
		final ProjectileSource sh = pro.getShooter();
		if (sh instanceof Player) {
			Player pl = (Player)sh;
			Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(pl);
			if (game == null) return;
			ArenaHandler handler = BWG.get(game);
			if (handler == null) return;
			if (handler.getByPlayer(pl) == null) return;
			final Effect eff = cache.get(pl.getName());
			if (eff == null) return;
			new BukkitRunnable() {
				public void run() {
					if (pro.isOnGround()) {
						cancel();
						return;
					}
					Location loc = pro.getLocation();
					spawnEffect(eff,loc);
				}
			}.runTaskTimerAsynchronously(BWG.get(), 1L, 1L);
		}
	}
	
	
	public void spawnEffect(Effect e,Location loc) {
 		loc.getWorld().playEffect(loc, e, 100);
	}

	@Override
	public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
		if (var1 instanceof Player) {
			open((Player)var1,null,0);
			return true;
		}
		var1.sendMessage(StrUtil.color("&cYou must be a player to do this!"));
		return true;
	}

	@Override
	public void run() {
		try {
			File file = new File(BWG.get().getDataFolder(),"projectiledatas.dat");
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(file));
			str.writeObject(cache);
			str.close();
		} catch (Exception e) {
		}
	}
}
