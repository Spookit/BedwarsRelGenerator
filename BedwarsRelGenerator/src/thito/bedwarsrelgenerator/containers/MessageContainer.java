package thito.bedwarsrelgenerator.containers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;

public class MessageContainer {

	private final boolean enabled;
	private final String message;
	public MessageContainer(ConfigurationSection sec) {
		enabled = sec.getBoolean("enabled");
		message = sec.getString("message");
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getMessage() {
		return message;
	}
	public void broadcast(Team g,Object... format) {
		if (!isEnabled()) return;
		g.getPlayers().forEach(a->{
			String msg = getMessage();
			for (int i = 0; i < format.length; i++) {
				msg = msg.replace("{"+i+"}", String.valueOf(format[i]));
			}
			msg = ChatColor.translateAlternateColorCodes('&', msg);
			a.sendMessage(msg);
		});
	}
	public void broadcast(Game g,Object... format) {
		if (!isEnabled()) return;
		g.getPlayers().forEach(a->{
			String msg = getMessage();
			for (int i = 0; i < format.length; i++) {
				msg = msg.replace("{"+i+"}", String.valueOf(format[i]));
			}
			msg = ChatColor.translateAlternateColorCodes('&', msg);
			a.sendMessage(msg);
		});
	}
	
}
