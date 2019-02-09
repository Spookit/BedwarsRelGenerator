package thito.bedwarsrelgenerator.containers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class ScoreboardContainer {

	public boolean enabled;
	public boolean useold;
	public String title;
	public ScoreboardTeamContainer teamholder;
	public List<String> lines;
	public ScoreboardContainer(ConfigurationSection sec) {
		enabled = sec.getBoolean("enabled");
		useold = sec.getBoolean("use-old");
		title = sec.getString("title");
		if (sec.isConfigurationSection("team-holder")) {
			teamholder = new ScoreboardTeamContainer(sec.getConfigurationSection("team-holder"));
		}
		lines = sec.getStringList("lines");
	}
	
}
