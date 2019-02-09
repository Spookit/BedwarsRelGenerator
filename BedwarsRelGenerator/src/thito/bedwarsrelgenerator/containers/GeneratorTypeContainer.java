package thito.bedwarsrelgenerator.containers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import thito.breadcore.spigot.inventory.XMaterial;

public class GeneratorTypeContainer {

	private final String typeName;
	private final XMaterial typeBlock;
	private final String typePrefix;
	private final int interval;
	private final Map<Integer,GeneratorLevelContainer> levels = new LinkedHashMap<>();
	public GeneratorTypeContainer(ConfigurationSection sec,ConfigurationSection levelSection) {
		interval = sec.getInt("interval");
		typeName =sec.getString("type-name");
		typeBlock = XMaterial.fromString(sec.getString("type-block"));
		typePrefix = sec.getString("type-prefix");
		for (String s : sec.getStringList("levels")) {
			GeneratorLevelContainer level = new GeneratorLevelContainer(levelSection.getConfigurationSection(s));
			levels.put(level.getLevel(), level);
		}
	}
	public int getInterval() {
		return interval;
	}
	public GeneratorLevelContainer getNextLevel(int currentLevel) {
		GeneratorLevelContainer cont = null;
		for (GeneratorLevelContainer con : levels.values()) {
			if (currentLevel <= con.getLevel()) {
				if (cont != null) {
					if (cont.getLevel() > con.getLevel()) {
						cont = con;
					}
				} else cont = con;
			}
		}
		return cont;
	}
	public int maxLevel() {
		int max = 0;
		for (GeneratorLevelContainer con : levels.values()) max = Math.max(con.getLevel(), max);
		return max;
	}
	public Map<Integer,GeneratorLevelContainer> getLevels() {
		return levels;
	}
	public String getTypeName() {
		return typeName;
	}
	public XMaterial getTypeBlock() {
		return typeBlock;
	}
	public String getTypePrefix() {
		return typePrefix;
	}
}
