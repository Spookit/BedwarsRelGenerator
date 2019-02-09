package thito.bedwarsrelgenerator.broadcaster;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BossBarBroadcaster implements Broadcaster, Listener {

	private final Map<String,BossBar> main = new HashMap<>();
	private final Map<String,BossBar> secondary = new HashMap<>();
	@Override
	public void send(Player p, String msg) {
		send(p,msg,main);
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		main.remove(e.getPlayer().getName());
		secondary.remove(e.getPlayer().getName());
	}
	
	public void send(Player p,String msg,String sub) {
		send(p,msg,main);
		send(p,msg,secondary);
	}
	
	public void send(Player p,String msg,Map<String,BossBar> map) {
		BossBar bar = map.get(p.getName());
		if (bar == null) {
			map.put(p.getName(), bar = Bukkit.createBossBar(msg, BarColor.RED, BarStyle.SOLID));
			bar.addPlayer(p);
			bar.setVisible(true);
			return;
		}
		bar.setTitle(msg);
		bar.setVisible(true);
	}

	@Override
	public void unsubscribe(Player p) {
		BossBar bar = main.get(p.getName());
		if (bar != null) bar.setVisible(false);
		BossBar sub = secondary.get(p.getName());
		if (sub != null) sub.setVisible(false);
	}

	@Override
	public String getName() {
		return "BossBar";
	}

}
