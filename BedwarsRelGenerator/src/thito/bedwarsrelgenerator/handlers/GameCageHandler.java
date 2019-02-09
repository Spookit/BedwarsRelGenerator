package thito.bedwarsrelgenerator.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.DeathPlayerBag;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.spigot.nbt.ItemStackTag;

public class GameCageHandler implements ArenaListener {

	private ArenaHandler h;
	private int time;
	private Set<BlockState> states = new HashSet<>();
	private ItemStack kitSelector;
	private ConfigurationSection sec = BWG.get().getConfig().getConfigurationSection("game-cage");
	public GameCageHandler(ArenaHandler handler) {
		h = handler;
		time = BedwarsRel.getInstance().getMaxLength();
		
	}
	public ItemStack getSelector() {
		if (kitSelector == null) {
			kitSelector = PluginInventory.create(XMaterial.fromString(sec.getString("kit-item.type")),
					sec.getString("kit-item.display"),sec.getStringList("kit-item.lore"));
			ItemStackTag tag = new ItemStackTag(kitSelector);
			tag.getNBT().setString("kit_selector", "dummy");
			tag.apply(kitSelector);
		}
		return kitSelector;
	}
	public boolean isEnabled() {
		return sec.getBoolean("enabled");
	}
	public int getSuspendTime() {
		return sec.getInt("time");
	}
	public int getKitTime() {
		return sec.getInt("kit-time");
	}
	@Override
	public Game getGame() {
		return h.getGame();
	}
	@Override
	public ArenaHandler getArenaHandler() {
		return h;
	}
	public boolean allKits() {
		ArrayList<String> players = new ArrayList<>();
		for (Player p : getGame().getPlayers()) {
			players.add(p.getName());
		}
		return h.selected.keySet().containsAll(players);
	}
	@Override
	public void arenaStart() {
		if (!isEnabled()) return;
		getArenaHandler().setSuspended(true);
		boolean hasKit = BWG.getKits().hasKit(getArenaHandler());
		for (TeamHandler t : getArenaHandler().getTeams()) {
			t.fakeLocation(false, sec.getInt("distance"));
			createCage(t.getTeam().getSpawnLocation(),
					t.getTeam().getPlayers().size() > 1 ? 2 :2, // the width
					t.getTeam().getPlayers().size() > 1 ? 4 : 4, // the height
							XMaterial.fromString(sec.getString("cage")),XMaterial.fromString(sec.getString("center")));
			for (Player p : t.getTeam().getPlayers()) {
				p.teleport(t.getTeam().getSpawnLocation().clone().add(0.5,1,0.5));
				DeathPlayerBag.INVINCIBLE.add(p);
			}
			t.fakeLocation(true, 0);
		}
		if (hasKit) {
			for (Player p : getGame().getPlayers()) {
				p.getInventory().addItem(getSelector());
			}
		}
		new BukkitRunnable() {
			int remain = getSuspendTime();
			int kitTime = getKitTime();
			boolean skipKit = false;
			public void run() {
				if (getGame().getState() != GameState.RUNNING || getGame().getCycle().isEndGameRunning()) {
					cancel();
					return;
				}
				if (hasKit && !skipKit) {
					if (allKits()) {
						skipKit = true;
						return;
					}
					if (kitTime == 0) {
						skipKit = true;
						return;
					}
					h.sendTitleToAll("kit-timeout", kitTime);
					kitTime--;
					getGame().setTimeLeft(time);
					return;
				}
				if (remain <= 0) {
					h.sendTitleToAll("arena-start");
					destroyAll();
					new BukkitRunnable() {
						public void run() {
							if (getGame().getState() != GameState.RUNNING || getGame().getCycle().isEndGameRunning()) {
								cancel();
								return;
							}
							for (Player p : getGame().getPlayers()) {
								DeathPlayerBag.INVINCIBLE.remove(p);
							}
						}
					}.runTaskLaterAsynchronously(BWG.get(), sec.getInt("invincible-delay") * 20L);
					cancel();
					getArenaHandler().removeKitSelectors();
					for (TeamHandler a : h.getTeams()) {
						a.giveLoadouts();
					}
					h.setSuspended(false);
				} else if (remain <= 5) {
					h.sendTitleToAll("suspend-time-ready", remain);
					remain--;
				} else {
					h.sendTitleToAll("suspend-time",remain);
					remain--;
				}
				getGame().setTimeLeft(time);
			}
		}.runTaskTimer(BWG.get(), 20L, 20L);
	}
	@Override
	public void arenaStop() {
		h.setSuspended(false);
		for (Player p : getGame().getPlayers()) {
			DeathPlayerBag.INVINCIBLE.remove(p);
		}
		destroyAll();
	}
	
	public void createCage(Location loc,int size,int height,XMaterial m,XMaterial center) {
		Set<Location> duplicate = new HashSet<>();
		for (int x = loc.getBlockX()-size;x<=loc.getBlockX()+size;x++) {
			for (int y = loc.getBlockY();y<=loc.getBlockY()+height;y++) {
				for (int z = loc.getBlockZ()-size;z<=loc.getBlockZ()+size;z++) {
					if (
							(x == loc.getBlockX()+size || x == loc.getBlockX()-size )||
							(y == loc.getBlockY()          || y == loc.getBlockY()+height) ||
							(z == loc.getBlockZ()+size || z == loc.getBlockZ()-size)
							
						) {
						if (
								((x == loc.getBlockX()-size || x == loc.getBlockX() + size) && (y == loc.getBlockY() || y == height + loc.getBlockY())) ||
								((z == -size + loc.getBlockZ() || z == size + loc.getBlockZ()) && (x == -size + loc.getBlockX() || x == size + loc.getBlockX())) ||
								((y == loc.getBlockY() || y == height + loc.getBlockY()) && (z == -size + loc.getBlockZ() || z == size + loc.getBlockZ()))
							) {
							continue;
						}
						duplicate.add(new Location(loc.getWorld(),x,y,z));
					}
				}
			}
		}
		duplicate.forEach(locx->{
			states.add(locx.getBlock().getState());
			locx.getBlock().setType(m.parseMaterial());
		});
		loc.getBlock().setType(center.parseMaterial());
	}
	
	public void destroyAll() {
		new HashSet<>(states).forEach(a->{
			a.update(true);
		});
		states.clear();
	}
	
}
