package thito.bedwarsrelgenerator.containers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.Util;
import thito.breadcore.spigot.packets.SimplePacketWrapper;

public class TitleContainer {

	private final boolean enabled;
	private final String title;
	private final String subtitle;
	private final List<String> titles;
	private final List<String> subtitles;
	private final int interval;
	public TitleContainer(ConfigurationSection sec) {
		enabled = sec.getBoolean("enabled");
		title = sec.getString("title");
		subtitle = sec.getString("subtitle");
		titles = sec.getStringList("titles");
		subtitles = sec.getStringList("subtitles");
		interval = sec.getInt("interval");
	}
	public boolean isEnabled() {
		return enabled;
	}
	public String getTitle() {
		return title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void send(Player p,Object...format) {
		send(p,0,40,30,format);
	}
	public void send(Player p,int fadeIn,int stay,int fadeOut,Object...format) {
		send(p,fadeIn,stay,fadeOut,false,format);
	}
	public void send(Player p,int fadeIn,int stay, int fadeOut, boolean force,Object...format) {
		if (!enabled && !force) return;
		if (title != null || subtitle != null) {
			SimplePacketWrapper.sendTitle(p, Util.format(title, format), Util.format(subtitle, format), fadeIn, stay, fadeOut);
		}
		if ((titles != null || subtitles != null) && interval > 0) {
				new BukkitRunnable() {
					int index = 0;
					public void run() {
						if (index >= Math.max(titles == null ? 0 : titles.size(), subtitles == null ? 0 : subtitles.size())) {
							cancel();
							return;
						}
						String tit = null;
						String subtit = null;
						if (titles != null && index < titles.size()) {
							tit = titles.get(index);
						}
						if (subtitles != null && index < subtitles.size()) {
							subtit = subtitles.get(index);
						}
						index++;
						SimplePacketWrapper.sendTitle(p, Util.format(tit, format), Util.format(subtit, format), fadeIn, stay, fadeOut);
					}
				}.runTaskTimerAsynchronously(BWG.get(),0, interval);
		}
	}
}
