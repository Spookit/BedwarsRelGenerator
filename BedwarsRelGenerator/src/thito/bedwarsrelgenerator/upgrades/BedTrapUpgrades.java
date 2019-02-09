package thito.bedwarsrelgenerator.upgrades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Team;
import thito.bedwarsrelgenerator.DeathPlayerBag;
import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesSubscriber;
import thito.bedwarsrelgenerator.containers.UpgradeContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer.Effect;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.ScoreMap;
import thito.breadcore.utils.Util;

public class BedTrapUpgrades implements Upgrades {
	private ScoreMap<UpgradesSubscriber> subscriber = new ScoreMap<>();
	private final UpgradeContainer upgrades;
	public BedTrapUpgrades(ConfigurationSection sec) {
		upgrades = new UpgradeContainer(sec);
	}
	@Override
	public void subscribe(UpgradesSubscriber subs) {
		subscriber.put(subs, 0);
	}

	@Override
	public void unsubscribe(UpgradesSubscriber subs) {
		subscriber.remove(subs);
	}

	@Override
	public void levelUp(UpgradesSubscriber subs) {
		subscriber.increase(subs);
	}

	@Override
	public int getLevel(UpgradesSubscriber subs) {
		return subscriber.get(subs);
	}

	public Collection<Entity> getNearbyEntities8(Location location, double rad) {
		ArrayList<Entity> bukkitEntityList = new ArrayList<>();
		for (Entity e : location.getWorld().getEntities()) {
			if (e.getLocation().distance(location) <= rad) {
				bukkitEntityList.add(e);
			}
		}
		return bukkitEntityList;
	}
	
	public Collection<Entity> getNearbyEntities(Location location, double rad) {
		return location.getWorld().getNearbyEntities(location, rad, rad, rad);
	}
	
	@Override
	public void tick() {
		if (!upgrades.isEnabled()) return;
		try {
			new HashMap<>(subscriber).forEach((a,b)->{
				UpgradeLevelContainer level = upgrades.getLevels().getOrDefault(b, Upgrades.DEFAULT);
				if (level.getConfig() == null) return;
				Team t = a.getTeam();
				boolean triggered = false;
				Collection<Entity> iterable = Util.getVersionNumber() > 8 ? getNearbyEntities(t.getTargetHeadBlock(), level.getConfig().getDouble("radius")) : getNearbyEntities8(t.getTargetHeadBlock(), level.getConfig().getDouble("radius")); 
				for (Entity e :  iterable) {
					if (e instanceof Player) {
						Player px = (Player)e;
						if (DeathPlayerBag.PLAYERS.contains(px)) continue;
						if (DeathPlayerBag.INVINCIBLE.contains(px)) continue;
						if (BedwarsRel.getInstance().getGameManager().getGameOfPlayer(px) == a.getGame()) {
							if (a.getGame().getPlayerTeam(px) != null && a.getGame().getPlayerTeam(px) != t) {
								for (Effect ef : level.getEffects()) {
									PotionEffectType eff = ef.asPotionEffectType();
									if (eff != null) {
										px.addPotionEffect(new PotionEffect(eff, 400, ef.getLevel()), true);
										triggered = true;
									}
								}
							}
						}
					}
				}
				if (triggered) {
					for (Player p : t.getPlayers()) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', upgrades.getConfig().getString("triggered-message")));
						a.getArenaHandler().sendTitle("bed-trap-triggered",p);
					}
					
					subscriber.decrease(a);
				}
			});
		} catch (Exception e) {
		}
	}
	@Override
	public XMaterial getIcon() {
		return upgrades.getIcon();
	}
	@Override
	public UpgradeContainer getData() {
		return upgrades;
	}
}
