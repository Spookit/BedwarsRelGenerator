package thito.bedwarsrelgenerator.containers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import thito.breadcore.spigot.inventory.XMaterial;

public class UpgradeContainer {

	private final boolean enabled;
	private final String name;
	private final String narrow;
	private final Map<Integer,UpgradeLevelContainer> levels = new LinkedHashMap<>();
	private final XMaterial icon;
	private final ConfigurationSection s;
	public UpgradeContainer(ConfigurationSection sec) {
		enabled = sec.getBoolean("enabled");
		s = sec;
		name = sec.getString("name");
		narrow = sec.getString("narrow-name");
		for (String s : sec.getConfigurationSection("levels").getKeys(false)) {
			UpgradeLevelContainer con = new UpgradeLevelContainer(sec.getConfigurationSection("levels."+s));
			levels.put(con.getLevel(), con);
		}
		icon = XMaterial.fromString(sec.getString("icon"));
	}
	public ConfigurationSection getConfig() {
		return s;
	}
	public XMaterial getIcon() {
		return icon;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public String getName() {
		return name;
	}
	public String getNarrowName() {
		return narrow;
	}
	public Map<Integer,UpgradeLevelContainer> getLevels() {
		return levels;
	}
}
