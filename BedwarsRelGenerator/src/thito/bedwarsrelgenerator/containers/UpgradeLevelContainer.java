package thito.bedwarsrelgenerator.containers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import thito.breadcore.spigot.inventory.XMaterial;

public class UpgradeLevelContainer {

	private final String name;
	private final int level;
	private final Set<Effect> effects = new HashSet<>();
	private final Set<XMaterial> loadouts = new HashSet<>();
	private final Cost cost;
	private final ConfigurationSection section;
	public UpgradeLevelContainer() {
		name = "Default";
		level = 0;
		section = null;
		cost = new Cost();
	}
	public UpgradeLevelContainer(ConfigurationSection sec) {
		section = sec;
		name = sec.getString("name");
		level = sec.getInt("level");
		if (sec.isList("effects")) for (String s : sec.getStringList("effects")) {
			effects.add(new Effect(s));
		}
		if (sec.isList("loadout")) for (String s : sec.getStringList("loadout")) {
			loadouts.add(XMaterial.fromString(s));
		}
		cost = new Cost(sec.getConfigurationSection("cost"));
	}
	
	public Set<XMaterial> getLoadouts() {
		return loadouts;
	}
	
	public String getName() {
		return name;
	}
	public int getLevel() {
		return level;
	}
	public Set<Effect> getEffects() {
		return effects;
	}
	public Cost getCost() {
		return cost;
	}
	
	public static class Effect {
		private int level;
		private String name;
		public Effect(String eff) {
			String[] a = eff.split(":",2);
			if (a.length >= 2) {
				try {
					level = Integer.parseInt(a[1]);
				} catch (Exception e) {
					level = 0;
				}
				name = a[0];
			} else {
				name = a[0];
			}
		}
		public Enchantment asEnchantment() {
			return Enchantment.getByName(getName());
		}
		public PotionEffectType asPotionEffectType() {
			return PotionEffectType.getByName(getName());
		}
		public String toString() {
			return name+":"+level;
		}
		public int getLevel() {
			return level;
		}
		public String getName() {
			return name;
		}
	}
	public static class Cost {
		private final XMaterial type;
		private final int amount;
		Cost() {
			type =XMaterial.AIR;
			amount = 0;
		}
		public Cost(ConfigurationSection sec){ 
			type = XMaterial.fromString(sec.getString("type"));
			amount = sec.getInt("amount");
		}
		public int getAmount() {
			return amount;
		}
		public XMaterial getType() {
			return type;
		}
	}
	public ConfigurationSection getConfig() {
		return section;
	}
}
