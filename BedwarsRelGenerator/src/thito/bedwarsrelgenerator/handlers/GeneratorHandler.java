package thito.bedwarsrelgenerator.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.CleanUpCrash;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.GeneratorLevelContainer;
import thito.bedwarsrelgenerator.containers.GeneratorTypeContainer;
import thito.breadcore.spigot.hologram.BlockLine;
import thito.breadcore.spigot.hologram.Hologram;
import thito.breadcore.spigot.hologram.HologramComponent;
import thito.breadcore.spigot.hologram.TextLine;

public class GeneratorHandler implements ArenaListener {

	private final Game game;
	private final ArenaHandler h;
	private final Hologram holo;
	private final boolean enabled;
	private int level = 1;
	private BukkitTask task;
	private BukkitTask task2;
	private final GeneratorTypeContainer cont;
	private final ConfigurationSection section;
	private BlockLine block;
	private List<ItemStack> drops;
	private ResourceSpawner spawner;
	public GeneratorHandler(ConfigurationSection config,ResourceSpawner spawner,ArenaHandler handler,GeneratorTypeContainer container) {
		game = spawner.getGame();
		section = config;
		this.spawner = spawner;
		cont = container;
		h = handler;
		enabled = config.getBoolean("enabled");
		holo = new Hologram(spawner.getLocation().clone().add(0,1,0), true);
		if (enabled) {
			GeneratorLevelContainer level = container.getLevels().get(this.level);
			List<String> cc = getNext() == null ? section.getStringList("lines-maxed") : section.getStringList("lines");
			Collections.reverse(cc);
			for (String line : cc) {
				line = line
						.replace("<typeprefix>", container.getTypePrefix())
						.replace("<type>", container.getTypeName())
						.replace("<level>", level == null ? "??":level.getName())
						.replace("<upgradecountdown>", level == null ? "-- : --" : container.maxLevel() > this.level ? Util.formatSeconds(level.getUpgradeCountdown()) : "-- : --")
						.replace("<countdown>",container.getInterval()+"");
				holo.addComponent(new TextLine(ChatColor.translateAlternateColorCodes('&',line)));
			}
			holo.addComponent(block=new BlockLine(container.getTypeBlock().parseItem()));
		};
		oldInterval = spawner.getInterval();
	}
	
	public ConfigurationSection getConfig() {
		return section;
	}
	
	public ResourceSpawner getSpawner() {
		return spawner;
	}
	
	public GeneratorTypeContainer getData() {
		return cont;
	}

	@Override
	public Game getGame() {
		return game;
	}
	public GeneratorLevelContainer getCurrent() {
		return cont.getLevels().get(this.level);
	}
	public GeneratorLevelContainer getNext() {
		return cont.getLevels().get(this.level+1);
	}
	public void refreshHolo(int countdown,long upgradeCountdown) {
		if (holo == null) return;
		countdownLeft = countdown;
		upgradeCountdownLeft = upgradeCountdown;
		List<String> lines;
		if (getNext() == null) {
			lines = section.getStringList("lines-maxed");
			maxLevel = true;
		} else {
			lines = section.getStringList("lines");
			maxLevel = false;
		}
		Collections.reverse(lines);
		List<HologramComponent> comps = holo.getComponents();
		int index = 0;
		for (HologramComponent c : comps) {
			if (c instanceof TextLine && index < lines.size()) {
				((TextLine) c).setName(ChatColor.translateAlternateColorCodes('&',lines.get(index)
						.replace("<typeprefix>", cont.getTypePrefix())
						.replace("<type>", cont.getTypeName())
						.replace("<level>", getCurrent() == null ? "??":getCurrent().getName())
						.replace("<upgradecountdown>", upgradeCountdown >= 0 ? Util.formatSeconds(upgradeCountdown) : "-- : --")
						.replace("<countdown>",countdown+"")
						));
				index++;
			}
		}
	}
	int countdownLeft = 0;
	long upgradeCountdownLeft = 0;
	boolean maxLevel = false;
	int oldInterval;
	public void arenaPreStart() {
		spawner.setInterval(-1000);
	}
	@Override
	public void arenaStart() {
		if (holo != null) holo.spawn();
		maxLevel = true;
		drops = spawner.getResources() == null ? new ArrayList<>() : new ArrayList<>(spawner.getResources());
		task = new BukkitRunnable() {
			int max = cont.getInterval();
			int countdown = cont.getInterval();
			long upgradeCountdown = 0;
			{
				upgradeCountdown = getCurrent() == null ? 0 : getCurrent().getUpgradeCountdown();
			}
			public void run() {
				if (getArenaHandler().isSuspended()) {
					refreshHolo(0,0);
					return;
				}
				countdown--;
				upgradeCountdown--;
				int planedCountdown = 0;
				long planedUpgradeCountdown = -1;
				if (upgradeCountdown <= 0 && getNext() != null) {
					level++;
					if (getCurrent() != null) {
						upgradeCountdown = planedUpgradeCountdown = getCurrent().getUpgradeCountdown();
						this.max -= getCurrent().getSecondsDecreasement();
						countdown = planedCountdown = Math.min(max, countdown);
					}
				} else if (getNext() != null) {
					planedUpgradeCountdown = upgradeCountdown;
				}
				if (countdown <= 0) {
					World world = holo.getLocation().getWorld();
					Location loc = spawner.getLocation();
					Block block = loc.getBlock();
					BlockState state = block.getState();
					if (BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true) && state instanceof Chest) {
						Inventory inv = ((Chest) state).getInventory();
						for (ItemStack drop : drops) {
							inv.addItem(drop);
						}
					} else {
						for (ItemStack drop : drops) {
							Item itemEn = world.dropItem(holo.getLocation(), drop);
							CleanUpCrash.claimEntity(itemEn);
						}
					}
					planedCountdown = countdown = max;
				} else {
					planedCountdown = countdown;
				}
				refreshHolo(planedCountdown,planedUpgradeCountdown);
			}
		}.runTaskTimer(BWG.get(), 20L, 20L);
		task2 = new BukkitRunnable() {
			double y = 0;
			double increment = 0.02;
			boolean add = false;
			boolean revert = false;
			int delay = 0;
			public void run() {
				EulerAngle angle = new EulerAngle(0, y, 0);
				if (angle.getY() < 6) {
					y += add?increment:-increment;
				} else {
					y = 0;
				}
				if (revert) {
					if (increment <= 0.2) {
						if (delay >= 20) {
							delay = 0;
							revert = false;
							add = !add;
						} else delay++;
					} else {
						increment -= (increment / 100) * Math.sqrt(increment * 100);
					}
				} else {
					if (increment >= 0.35) {
						if (delay >= 20) {
							delay = 0;
							revert = true;
						} else delay++;
					} else {
						increment += (increment / 100) * Math.sqrt(increment * 100);
					}
				}
				if (block != null) block.getEntity().setHeadPose(angle);
			}
		}.runTaskTimerAsynchronously(BWG.get(), 1L, 1L);
	}

	@Override
	public void arenaStop() {
		if (task != null) task.cancel();
		if (task2 != null) task2.cancel();
		holo.despawn();
		spawner.setInterval(oldInterval);
	}

	@Override
	public ArenaHandler getArenaHandler() {
		return h;
	}
}
