package thito.bedwarsrelgenerator.containers;

import org.bukkit.configuration.ConfigurationSection;

import thito.bedwarsrelgenerator.broadcaster.Broadcaster;

public class TrackerContainer {

	public boolean enabled;
	public Broadcaster broadcasterBedMode;
	public Broadcaster broadcasterTrackMode;
	public String bedMode;
	public String trackingMode;
	public int trackingDuration;
	public long rate;
	public boolean trackMustHoldCompass;
	public boolean bedMustHoldCompass;
	public TrackerContainer(ConfigurationSection sec) {
		enabled = sec.getBoolean("enabled");
		broadcasterBedMode = Broadcaster.getService(sec.getString("broadcaster-bed-mode"));
		broadcasterTrackMode = Broadcaster.getService(sec.getString("broadcaster-tracking-mode"));
		bedMode = sec.getString("bed-mode");
		trackingMode = sec.getString("tracking-mode");
		trackingDuration = sec.getInt("tracking-mode-duration");
		rate = sec.getLong("refresh-rate");
		trackMustHoldCompass = sec.getBoolean("tracking-must-hold-compass");
		bedMustHoldCompass = sec.getBoolean("bed-must-hold-compass");
	}
}
