package thito.bedwarsrelgenerator.handlers;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.bedwarsrel.game.Game;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.breadcore.spigot.hologram.Hologram;
import thito.breadcore.spigot.hologram.TextLine;

public class BedHandler implements ArenaListener {

	private final ArenaHandler h;
	private final ConfigurationSection sec;
	private Hologram hologram;
	private TextLine line;
	private final TeamHandler hand;
	private boolean d;
	public BedHandler(ArenaHandler handler,TeamHandler t,Location loc,ConfigurationSection section) {
		h = handler;
		sec = section;
		hand = t;
		hologram = new Hologram(loc.clone().add(0.5,0,0.5), false);
		hologram.addComponent(line = new TextLine(ChatColor.translateAlternateColorCodes('&', section.getString("defend"))));
	}

	@Override
	public Game getGame() {
		return h.getGame();
	}
	public boolean isDestroyed() {
		return d;
	}
	public void setDestroyed(boolean destroyed) {
		if (destroyed && !d) {
			line.setName(ChatColor.translateAlternateColorCodes('&', sec.getString("destroyed")));
		} else if (!destroyed && d) {
			line.setName(ChatColor.translateAlternateColorCodes('&', sec.getString("defend")));
		}
		d = destroyed;
	}

	@Override
	public ArenaHandler getArenaHandler() {
		return h;
	}
	ArrayList<BukkitTask> task = new ArrayList<>();
	@Override
	public void arenaStart() {
		if (sec.getBoolean("enabled")) {
			hologram.spawn();
			setDestroyed(hand.getTeam().isDead(getGame()) || hand.getTeam().getHeadTarget().getType() == Material.AIR);
			task.add(new BukkitRunnable() {
				public void run() {
					if (hologram.getLocation().clone().add(0,1,0).getBlock().getType() != Material.AIR) {
						if (hologram.isSpawned()) {
							hologram.despawn();
						}
					} else {
						if (!hologram.isSpawned()) hologram.spawn();
					}
				}
			}.runTaskTimer(BWG.get(), 10L, 10L));
		}
	}

	@Override
	public void arenaStop() {
		hologram.despawn();
		task.forEach(a->{
			a.cancel();
		});
		task.clear();
	}
}
