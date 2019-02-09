package thito.bedwarsrelgenerator.containers;

import org.bukkit.configuration.ConfigurationSection;

public class GeneratorLevelContainer {

	private final String name;
	private final int level;
	private final long countdown;
	private final long decreasement;
	public GeneratorLevelContainer(ConfigurationSection sec) {
		name = sec.getString("name");
		level = sec.getInt("level");
		countdown = sec.getLong("upgrade-countdown");
		decreasement = sec.getLong("seconds-decreasement");
	}
	public String getName() {
		return name;
	}
	public int getLevel() {
		return level;
	}
	public long getUpgradeCountdown() {
		return countdown;
	}
	public long getSecondsDecreasement() {
		return decreasement;
	}
}
