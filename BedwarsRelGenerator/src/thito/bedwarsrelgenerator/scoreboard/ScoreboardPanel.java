package thito.bedwarsrelgenerator.scoreboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.base.Splitter;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;

public class ScoreboardPanel {

	private static final int LOBBY = 0;
	private static final int GAME = 1;
	private static final int SUSPEND = 2;
	private final ArrayList<ScoreboardFrame> frames = new ArrayList<>();
	private final ArrayList<ScoreboardFrame> lobbies = new ArrayList<>();
	private final ArrayList<ScoreboardFrame> suspends = new ArrayList<>();
	private final Map<String,Scoreboard> scoreboards = new HashMap<>();
	private int frameType = 0;
	private String teamDestroyed;
	private String teamAlive;
	private String teamEliminated;
	private String date;
	private String isYou;
	private long refresh;
	private long frameRefresh;
	private int frame;
	private int lobby;
	private int suspend;
	private boolean enabled;
	
	public Scoreboard getScoreboard(Player p) {
		Scoreboard sb = scoreboards.get(p.getName());
		if (sb == null) {
			scoreboards.put(p.getName(), sb = Bukkit.getScoreboardManager().getNewScoreboard());
		}
		return sb;
	}
	
	public ScoreboardPanel(ConfigurationSection sec) {
		if (!sec.getBoolean("enabled")) {
			enabled = false;
			return;
		} else enabled = true;
		teamDestroyed = sec.getString("strings.team-destroyed");
		teamAlive = sec.getString("strings.team-alive");
		teamEliminated = sec.getString("strings.team-eliminated");
		date = sec.getString("strings.date");
		refresh = sec.getLong("refresh-ticks");
		frameRefresh = sec.getLong("frame-refresh-ticks");
		isYou = sec.getString("strings.is-you");
		BedwarsRel.getInstance().getConfig().set("scoreboard.format-bed-destroyed", "");
		BedwarsRel.getInstance().getConfig().set("scoreboard.format-bed-alive", "");
		BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.content", new ArrayList<>());
		for (Map<?,?> o : sec.getMapList("frames.lobby")) {
			Object title = o.get("title");
			Object lines = o.get("lines");
			if (title instanceof String && lines instanceof List) {
				ArrayList<String> l = new ArrayList<>();
				for (Object line : (List<?>)lines) {
					if (line instanceof String) {
						l.add((String)line);
					}
				}
				lobbies.add(new ScoreboardFrame((String)title, l));
			}
		}
		for (Map<?,?> o : sec.getMapList("frames.suspend")) {
			Object title = o.get("title");
			Object lines = o.get("lines");
			if (title instanceof String && lines instanceof List) {
				ArrayList<String> l = new ArrayList<>();
				for (Object line : (List<?>)lines) {
					if (line instanceof String) {
						l.add((String)line);
					}
				}
				suspends.add(new ScoreboardFrame((String)title, l));
			}
		}
		for (Map<?,?> o : sec.getMapList("frames.game")) {
			Object title = o.get("title");
			Object lines = o.get("lines");
			if (title instanceof String && lines instanceof List) {
				ArrayList<String> l = new ArrayList<>();
				for (Object line : (List<?>)lines) {
					if (line instanceof String) {
						l.add((String)line);
					}
				}
				frames.add(new ScoreboardFrame((String)title, l));
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private BukkitTask task;
	private BukkitTask task2;
	public void start() {
		if (!enabled) return;
		task = new BukkitRunnable() {
			public void run() {
				updateScoreboard();
			}
		}.runTaskTimer(BWG.get(), 0, refresh);
		task2 = new BukkitRunnable() {
			public void run() {
				if (frame < frames.size()) {
					frame++;
				} else frame = 0;
				if (lobby < lobbies.size()) {
					lobby++;
				} else lobby = 0;
				if (suspend < suspends.size()) {
					suspend++;
				} else suspend = 0;
			}
		}.runTaskTimerAsynchronously(BWG.get(), frameRefresh, frameRefresh);
	}
	
	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (task2 != null) {
			task2.cancel();
			task2 = null;
		}
		for (ArenaHandler h : BWG.HANDLERS) {
			Scoreboard db = h.getGame().getScoreboard();
			db.clearSlot(DisplaySlot.SIDEBAR);
			for (org.bukkit.scoreboard.Team t : db.getTeams()) {
				if (t.getName().startsWith("sbpt")) {
					t.unregister();
				}
			}
		}
	}
	
	public String getDate() {
		SimpleDateFormat format = new SimpleDateFormat(date);
		return format.format(new Date());
	}
	
	public ScoreboardFrame gameFrame() {
		if (!frames.isEmpty()) {
			if (frame >= frames.size()) frame = 0;
			return frames.get(frame);
		}
		return null;
	}
	
	public ScoreboardFrame suspendFrame() {
		if (!suspends.isEmpty()) {
			if (suspend >= suspends.size()) suspend = 0;
			return suspends.get(suspend);
		}
		return null;
	}
	
	public ScoreboardFrame lobbyFrame() {
		if (!lobbies.isEmpty()) {
			if (lobby >= lobbies.size()) lobby = 0;
			return lobbies.get(lobby);
		}
		return null;
	}
	
	public void updateScoreboard() {
		if (!enabled) return;
		for (ArenaHandler h : BWG.HANDLERS) {
			final Game g = h.getGame();
//			sb.clearSlot(DisplaySlot.SIDEBAR);
			Map<String,String> placeholders = new HashMap<>();
			placeholders.put("game", g.getName());
			placeholders.put("date", getDate());
			placeholders.put("players", g.getPlayerAmount()+"");
			placeholders.put("maxplayers", g.getMaxPlayers()+"");
			placeholders.put("minplayers", g.getMinPlayers()+"");
			placeholders.put("remainplayers",(g.getMinPlayers()-g.getPlayerAmount())+"");
			boolean shouldReset = false;
			ScoreboardFrame frame;
			if (h.isSuspended()) {
				if (frameType == GAME) {
					shouldReset = true;
				}
				frame = suspendFrame();
				frameType = SUSPEND;
			} else if (g.getState() == GameState.RUNNING) {
				if (frameType == SUSPEND) {
					shouldReset = true;
				}
				frame = gameFrame();
				frameType = GAME;
			} else {
				frame = lobbyFrame();
				frameType = LOBBY;
			}
			shouldReset = !shouldReset;
			if (frame == null) continue;
			for (Player p : g.getPlayers()) {
				Scoreboard sb = g.getScoreboard();
				Objective obj = sb.getObjective(g.getState() == GameState.RUNNING ? "display" : "lobby");
				if (obj == null) {
					obj = sb.registerNewObjective(g.getState() == GameState.RUNNING ? "display" : "lobby","dummy");
				}
				sb.resetScores("");
				obj.setDisplayName(ScoreboardFrame.replaceAll(frame.getTitle(), placeholders, null));
				BedwarsRel.getInstance().getConfig().set("scoreboard.format-title", obj.getDisplayName());
				BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.title", obj.getDisplayName());
				Map<String,Iterator<String>> remains = new HashMap<>();
				ArrayList<String> teams = new ArrayList<>();
				for (Team t : g.getTeams().values()) {
					String data;
					if (t.isDead(g) && t.getPlayers().isEmpty()) {
						data = teamEliminated;
					} else if (t.isDead(g)) {
						data = teamDestroyed;
					} else {
						data = teamAlive;
					}
					teams.add(data
							.replace("${team}", t.getChatColor()+t.getName())
							.replace("${teamname}", t.getName())
							.replace("${teamcolor}", t.getChatColor()+"")
							.replace("${teamplayers}", t.getPlayers().size()+"")
							.replace("${teammaxplayers}", t.getMaxPlayers()+"")
							.replace("${teamprefix}", t.getName().isEmpty() ? "?" : t.getName().substring(0, 1).toUpperCase())
							.replace("${is_you}", t.getPlayers().contains(p) ? isYou : ""));
				}
				remains.put("teams", teams.iterator());
				placeholders.put("name", p.getName());
				placeholders.put("health", p.getHealth()+"");
				placeholders.put("displayname", p.getDisplayName());
				Team t = g.getPlayerTeam(p);
				if (t != null) {
					placeholders.put("team", t.getChatColor()+t.getName());
					placeholders.put("teamname", t.getName());
					placeholders.put("teamcolor", t.getChatColor()+"");
					placeholders.put("teamplayers", t.getPlayers().size()+"");
					placeholders.put("teammaxplayers", t.getMaxPlayers()+"");
					placeholders.put("teamprefix", t.getName().isEmpty() ? "?" : t.getName().substring(0, 1).toUpperCase());
					ArrayList<String> names = new ArrayList<>();
					for (Player member : t.getPlayers()) {
						names.add(member.getName());
					}
					remains.put("teamplayer", names.iterator());
				}
				h.additionalPlaceholders(p, placeholders);
				int index = 15;
				int teamId = 0;
				for (String s : frame.lines(placeholders, remains)) {
					s = h.getStats().replace(p, s);
					String middle = ScoreboardFrame.hideName("a"+(teamId));
					org.bukkit.scoreboard.Team team = sb.getTeam("bwg"+(teamId));
					if (team == null) {
						team = sb.registerNewTeam("bwg"+(teamId));
						team.addEntry(middle);
					} 
					Iterator<String> split = split(s);
					String pre;
					team.setPrefix(pre=split.next());
					if (s.length() > 16) {
						if (pre.endsWith(ChatColor.COLOR_CHAR+"")) {
							team.setPrefix(pre.substring(0,pre.length()-1));
						}
						String suffix = split.next();
						suffix = ChatColor.getLastColors(pre)+(pre.endsWith(ChatColor.COLOR_CHAR+"") ? ChatColor.COLOR_CHAR+suffix : suffix);
						team.setSuffix(suffix.substring(0,Math.min(16, suffix.length())));
					} else {
						team.setSuffix("");
					}
					Score score = obj.getScore(middle.substring(0,Math.min(16, middle.length())));
					score.setScore(index--);
					teamId++;
				}
				for (org.bukkit.scoreboard.Team team : sb.getTeams()) {
					if (!team.getName().startsWith("bwg")) continue;
					boolean fine = false;
					for (int i = 0; i < teamId; i++) {
						if (team.getName().equals("bwg"+i)) {
							fine = true;
							break;
						}
					}
					if (!fine) {
						for (String s : new HashSet<>(team.getEntries())) {
							sb.resetScores(s);
							team.removeEntry(s);
						}
						team.unregister();
					}
				}
				sb.resetScores("");
				p.setScoreboard(sb);
			}
		}
	}
	public static void main(String[]args) {
		Iterator<String> s = split("somethingsolong very very long just trust me that it is very long");
		while (s.hasNext()) {
			System.out.println(s.next().length());
		}
		System.out.println(ScoreboardFrame.hideName("something"));
	}
	public static Iterator<String> split(String s) {
		return Splitter.fixedLength(16).split(s).iterator();
	}
}
