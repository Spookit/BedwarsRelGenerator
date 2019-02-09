package thito.bedwarsrelgenerator;

import java.util.LinkedHashSet;
import java.util.Set;

import thito.bedwarsrelgenerator.containers.UpgradeContainer;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.breadcore.spigot.inventory.XMaterial;

public interface Upgrades {

	public static final UpgradeLevelContainer DEFAULT = new UpgradeLevelContainer();
	public static final Set<Upgrades> UPGRADES = new LinkedHashSet<>();
	public XMaterial getIcon();
	public UpgradeContainer getData();
	public void subscribe(UpgradesSubscriber subs);
	public void unsubscribe(UpgradesSubscriber subs);
	public void levelUp(UpgradesSubscriber subs);
	public int getLevel(UpgradesSubscriber subs);
	public void tick();
	
}
