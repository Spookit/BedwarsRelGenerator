package thito.bedwarsrelgenerator.upgrades;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesSubscriber;
import thito.bedwarsrelgenerator.containers.UpgradeContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer.Effect;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.ScoreMap;

public class BeaconUpgrades implements Upgrades {

	private ScoreMap<UpgradesSubscriber> subscriber = new ScoreMap<>();
	private final UpgradeContainer upgrades;
	public BeaconUpgrades(ConfigurationSection sec) {
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

	@Override
	public void tick() {
		if (!upgrades.isEnabled()) return;
		new HashMap<>(subscriber).forEach((a,b)->{
			UpgradeLevelContainer level = upgrades.getLevels().getOrDefault(b, Upgrades.DEFAULT);
			for (Effect e : level.getEffects()) {
				PotionEffectType eff = e.asPotionEffectType();
				if (eff != null) {
					for (Player p : new HashSet<>(a.getSubscribers())) {
						p.addPotionEffect(new PotionEffect(eff, 400, e.getLevel()), true);
					}
				}
			}
		});
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
