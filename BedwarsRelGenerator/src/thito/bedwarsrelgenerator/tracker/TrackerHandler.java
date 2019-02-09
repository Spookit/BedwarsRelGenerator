package thito.bedwarsrelgenerator.tracker;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.bedwarsrel.game.Game;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.TrackerContainer;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.handlers.TeamHandler;
import thito.bedwarsrelgenerator.scoreboard.ScoreboardFrame;

public class TrackerHandler implements ArenaListener {

	private final TrackerContainer cont = BWG.get().getTrackerContainer();
	private final Player p;
	private final ArenaHandler a;
	private final TeamHandler t;
	private Player tracked;
	private BukkitTask main;
	private long duration = cont.trackingDuration;
	private BukkitTask dura;
	private boolean unsub = false;
	public TrackerHandler(Player p,ArenaHandler arena,TeamHandler team) {
		this.p = p;
		a = arena;
		t = team;
	}
	
	public void setTracking(Player p) {
		unsub = false;
		duration = cont.trackingDuration;
		tracked = p;
	}
	
	public Player getTracking() {
		return tracked;
	}
	
	public TeamHandler getTeamHandler() {
		return t;
	}
	
	public Game getGame() {
		return a.getGame();
	}
	
	public ArenaHandler getArenaHandler() {
		return a;
	}
	
	public long getTimeLeft() {
		return duration;
	}
	
	public Player getPlayer() {
		return p;
	}
	public boolean isActive() {
		return !unsub;
	}

	@Override
	public void arenaStart() {
		if (!cont.enabled) return;
		dura = new BukkitRunnable() {
			public void run() {
				if (duration > 0) {
					duration--;
				}
			}
		}.runTaskTimerAsynchronously(BWG.get(),20L,20L);
		main = new BukkitRunnable() {
			public void run() {
				if (getArenaHandler().isSuspended()) return;
				Player tr = getTracking();
				if (cont.broadcasterBedMode != cont.broadcasterTrackMode) {
					String msg = null;
					if (tr != null){
						if (duration <= 0) {
							msg = cont.bedMode;
							setTracking(null);
							duration = cont.trackingDuration;
						} else {
							msg = cont.trackingMode;
						}
					}
					if (msg != null && (!cont.trackMustHoldCompass || (getPlayer().getItemInHand()!=null && getPlayer().getItemInHand().getType() == Material.COMPASS))) {
						msg = ScoreboardFrame.replaceAll(msg, placeholders(new HashMap<>()), null);
						unsub = false;
						(tr == null ? cont.broadcasterBedMode : cont.broadcasterTrackMode).send(getPlayer(), msg);
					} else {
						if (!unsub) {
							unsub = true;
							cont.broadcasterTrackMode.unsubscribe(p);
						}
					}
					if ((!cont.bedMustHoldCompass || (getPlayer().getItemInHand()!=null && getPlayer().getItemInHand().getType() == Material.COMPASS)) && 
							cont.broadcasterBedMode != cont.broadcasterTrackMode) {
						cont.broadcasterBedMode.send(p, ScoreboardFrame.replaceAll(cont.bedMode, placeholders(new HashMap<>()), null));
					} else {
						cont.broadcasterBedMode.unsubscribe(p);
					}
				} else {
					String msg;
					if (tr == null) {
						if (!cont.bedMustHoldCompass || (getPlayer().getItemInHand()!=null && getPlayer().getItemInHand().getType() == Material.COMPASS)) {
							msg = cont.bedMode;
							msg = ScoreboardFrame.replaceAll(msg, placeholders(new HashMap<>()), null);
							cont.broadcasterBedMode.send(getPlayer(), msg);
						} else cont.broadcasterBedMode.unsubscribe(getPlayer());
					} else {
						if (duration <= 0) {
							msg = cont.bedMode;
							setTracking(null);
							duration = cont.trackingDuration;
						} else {
							msg = cont.trackingMode;
						}
						if (!cont.trackMustHoldCompass || (getPlayer().getItemInHand()!=null && getPlayer().getItemInHand().getType() == Material.COMPASS)) {
							msg = ScoreboardFrame.replaceAll(msg, placeholders(new HashMap<>()), null);
							unsub = false;
							cont.broadcasterTrackMode.send(getPlayer(), msg);
						} else {
							if (!unsub && cont.bedMustHoldCompass) {
								unsub = true;
								cont.broadcasterTrackMode.unsubscribe(p);
							} else {
								msg = cont.bedMode;
								msg = ScoreboardFrame.replaceAll(msg, placeholders(new HashMap<>()), null);
								cont.broadcasterBedMode.send(getPlayer(), msg);
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(BWG.get(), cont.rate, cont.rate);
	}
	
	String ts(double d) {
		String x = d+"";
		if (x.contains(".")) {
			String ac = x.split("\\.", 2)[0];
			String bc = x.split("\\.",2)[1];
			return ac + '.' + (bc.length() > 2 ? bc.substring(0, 2) : bc);
		}
		return x;
	}
	
	public Map<String,String> placeholders(Map<String,String> map) {
		Player tr = getTracking();
		if (!map.containsKey("teamcolor")) map.put("teamcolor", getTeamHandler().getTeam().getChatColor()+"");
		if (!map.containsKey("teamname")) map.put("teamname", getTeamHandler().getTeam().getName()+"");
		if (!map.containsKey("teamprefix")) map.put("teamprefix", getTeamHandler().getTeam().getName().isEmpty() ? "":getTeamHandler().getTeam().getName().substring(0, 1).toUpperCase());
		if (!map.containsKey("team")) map.put("team", getTeamHandler().getTeam().getChatColor()+getTeamHandler().getTeam().getName());
		if (!map.containsKey("name")) map.put("name", getPlayer().getName());
		map.put("tracking_timeleft", Util.formatSeconds(getTimeLeft()));
		if (getTeamHandler().getTeam().getTargetFeetBlock().getWorld() == getPlayer().getLocation().getWorld()) {
			map.put("bed_distance", ts(getTeamHandler().getTeam().getTargetFeetBlock().distance(getPlayer().getLocation())));
		} else {
			map.put("bed_distance", "??");
		}
		map.put("tracked", tr == null ? "None" : tr.getName());
		if (tr == null? getTeamHandler().getTeam().getTargetFeetBlock().getWorld() == getPlayer().getLocation().getWorld() : tr.getLocation().getWorld() == getTeamHandler().getTeam().getTargetFeetBlock().getWorld()) {
			map.put("tracked_distance", tr == null ? ts(getTeamHandler().getTeam().getTargetHeadBlock().distance(getPlayer().getLocation())): ts(tr.getLocation().distance(getPlayer().getLocation())));
		} else {
			map.put("tracked_distance", "??");
		}
		TeamHandler t = tr == null ? getTeamHandler(): a.getByPlayer(tr);
		map.put("tracked_teamcolor", t == null ? "" : t.getTeam().getChatColor()+"");
		map.put("tracked_teamname", t == null ? "??" : t.getTeam().getName());
		map.put("tracked_team", t== null ? "??" : t.getTeam().getChatColor()+t.getTeam().getName());
		map.put("tracked_teamprefix", t==null ? "?" : t.getTeam().getName().isEmpty() ? "" : t.getTeam().getName().substring(0, 1).toUpperCase());
		return map;
	}

	@Override
	public void arenaStop() {
		if (main != null) {
			main.cancel();
			main = null;
		}
		if (dura != null) {
			dura.cancel();
			dura = null;
		}
		cont.broadcasterBedMode.unsubscribe(p);
		cont.broadcasterTrackMode.unsubscribe(p);
	}
	
}
