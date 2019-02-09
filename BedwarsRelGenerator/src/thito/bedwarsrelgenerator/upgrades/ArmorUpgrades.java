package thito.bedwarsrelgenerator.upgrades;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesSubscriber;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.UpgradeContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer.Effect;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.ArrayUtil;
import thito.breadcore.utils.ScoreMap;

public class ArmorUpgrades implements Upgrades {

	private ScoreMap<UpgradesSubscriber> subscriber = new ScoreMap<>();
	private final UpgradeContainer upgrades;
	public ArmorUpgrades(ConfigurationSection sec) {
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
		subs.removeLoadouts(this);
		subscriber.increase(subs);
		subs.giveLoadouts(this);
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
				Enchantment ench = e.asEnchantment();
				if (ench != null) {
					for (Player p : new HashSet<>(a.getSubscribers())) {
						for (ItemStack item : ArrayUtil.combine(p.getInventory().getContents(),p.getInventory().getArmorContents())) {
							if (item != null) {
								if (Util.isArmor(item.getType()) && (level.getLoadouts().isEmpty() || Util.isLoadout(item, "ArmorUpgrades"))) {
									item.addUnsafeEnchantment(ench, e.getLevel());
								}
							}
						}
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
