package thito.bedwarsrelgenerator.containers;

import org.bukkit.configuration.ConfigurationSection;

public class ScoreboardTeamContainer {

	public String alive;
	public String eliminated;
	public String bedlost;
	public ScoreboardTeamContainer(ConfigurationSection sec) {
		alive = sec.getString("alive");
		eliminated = sec.getString("eliminated");
		bedlost = sec.getString("bed-lost");
	}
}
