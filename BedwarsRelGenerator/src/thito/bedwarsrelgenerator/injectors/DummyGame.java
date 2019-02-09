package thito.bedwarsrelgenerator.injectors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.containers.ScoreboardContainer;

@Deprecated @SuppressWarnings("unused")
public class DummyGame extends Game {
/*
 * 
 * Due to the BedwarsRel Scoreboard is enough.
 * This injector is just a trash.
game-scoreboard:
  on-game:
    enabled: true
    # Use default scoreboard from BedwarsRel
    use-old: false
    title: "&e&lBEDWARS"
    team-holder:
      alive: "&a✔ &7- &f<team>"
      eliminated: "&c✘ &7- &f<team>"
      bed-lost: "&e✘ &7- &f<team>"
    # Max Lines - 16
    lines:
    - "&7"
    - "Map: &b<game>"
    - "Time Left: &b<timeleft>"
    - "&7"
    - "<team>"
    - "&7"
    - "spigotmc.org"
  on-lobby:
    enabled: true
    # Use default scoreboard from BedwarsRel
    use-old: false
    title: "&e&lBEDWARS"
    lines:
    - "&7"
    - "&fMap: &b<game>"
    - "&fPlayers:"
    - "&b<players>/<maxplayers>"
    - "&7"
    - "Need <remains> more"
    - "players to start!"
    - "&7"
    - "spigotmc.org" 
 */
	private Game originalGame;
	private ScoreboardContainer game = new ScoreboardContainer(BWG.get().getConfig().getConfigurationSection("game-scoreboard.on-game"));
	private ScoreboardContainer lobby = new ScoreboardContainer(BWG.get().getConfig().getConfigurationSection("game-scoreboard.on-lobby"));
	public DummyGame(Game g) {
		super(g.getName());
		copyField(g);
		this.addRunningTask(new BukkitRunnable() {
			public void run() {
				
			}
		}.runTaskTimerAsynchronously(BWG.get(),2L,2L));
	}
	public void onReload() {
		this.stop();
	}
	Game getOriginal() {
		return originalGame;
	}
	private void copyField(Game g) {
		originalGame = g;
		for (Field f : g.getClass().getDeclaredFields()) {
			if (Modifier.isPrivate(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				try {
					Field thiz = getClass().getDeclaredField(f.getName());
					thiz.set(this, f.get(g));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateScoreboard() {
		//EMPTY
	}
	public void updateGameScoreboard() {
		Scoreboard scoreboard = getScoreboard();
		Objective obj = scoreboard.getObjective("display");
		if (obj == null) {
			obj = scoreboard.registerNewObjective("display", "dummy");
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> lines = game.lines;
		int maxValues = 0;
		for (Team t : getTeams().values()) {
			maxValues = Math.max(t.getMaxPlayers(), maxValues);
		}
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		for (int i = 0 ;i < lines.size();i ++) {
			Score score = obj.getScore(lines.get(i));
			score.setScore(maxValues);
		}
	}
	public void updateSB() {
//		if (getState()== GameState.WAITING
//				&& BedwarsRel.getInstance().getBooleanConfig("lobby-scoreboard.enabled", true)) {
//			updateLobbySB();
//			return;
//		}
//		Scoreboard scoreboard = getScoreboard();
//		Objective obj = scoreboard.getObjective("display");
//		if (obj == null) {
//			obj = scoreboard.registerNewObjective("display", "dummy");
//		}
//		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//		obj.setDisplayName(formatScoreboardTitle());
//		for (Team t : getTeams().values()) {
//			scoreboard.resetScores(this.formatScoreboardTeam(t, false));
//			scoreboard.resetScores(this.formatScoreboardTeam(t, true));
//			boolean teamDead = t.isDead(this) && this.getState() == GameState.RUNNING;
//			Score score = obj.getScore(this.formatScoreboardTeam(t, teamDead));
//			score.setScore(t.getPlayers().size());
//		}
//		for (Player player : getPlayers()) {
//			player.setScoreboard(scoreboard);
//		}
	}
	public String getString(String s,String def) {
		return null;
	}
	private String formatScoreboardTeam(Team team, String format) {
		if (team == null) {
			return "";
		}
		format = format.replace("$team$", (Object) team.getChatColor() + team.getName());
		return ChatColor.translateAlternateColorCodes((char) '&', (String) format);
	}
	private String formatScoreboardTitle() {
		String format = getString("scoreboard.format-title", "&e$region$&f - $time$");
		format = format.replace("$region$", this.getRegion().getName());
		format = format.replace("$game$", getName());
		format = format.replace("$time$", getFormattedTimeLeft());
		return ChatColor.translateAlternateColorCodes((char) '&', (String) format);
	}
	private String getFormattedTimeLeft() {
		int min = 0;
		int sec = 0;
		String minStr = "";
		String secStr = "";
		min = (int) Math.floor(getTimeLeft()/ 60);
		sec = getTimeLeft() % 60;
		minStr = min < 10 ? "0" + String.valueOf(min) : String.valueOf(min);
		secStr = sec < 10 ? "0" + String.valueOf(sec) : String.valueOf(sec);
		return minStr + ":" + secStr;
	}
	private String formatLobbyScoreboardString(String str) {
		String finalStr = str;
		finalStr = finalStr.replace("$regionname$", getRegion().getName());
		finalStr = finalStr.replace("$gamename$", getName());
		finalStr = finalStr.replace("$players$", String.valueOf(this.getPlayerAmount()));
		finalStr = finalStr.replace("$maxplayers$", String.valueOf(this.getMaxPlayers()));
		return ChatColor.translateAlternateColorCodes((char) '&', (String) finalStr);
	}
	public void updateLobbySB() {
		getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		Objective obj = getScoreboard().getObjective("lobby");
		if (obj != null) {
			obj.unregister();
		}
		obj = getScoreboard().registerNewObjective("lobby", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(formatLobbyScoreboardString(
				BedwarsRel.getInstance().getStringConfig("lobby-scoreboard.title", "&eBEDWARS")));
		List<String> rows = BedwarsRel.getInstance().getConfig().getStringList("lobby-scoreboard.content");
		int rowMax = rows.size();
		if (rows == null || rows.isEmpty()) {
			return;
		}
		Iterator<String> iterator = rows.iterator();
		while (iterator.hasNext()) {
			String row = (String) iterator.next();
			if (row.trim().equals("")) {
				for (int i = 0; i <= rowMax; ++i) {
					row = row + " ";
				}
			}
			Score score = obj.getScore(this.formatLobbyScoreboardString(row));
			score.setScore(rowMax);
			--rowMax;
		}
		for (Player player : this.getPlayers()) {
			player.setScoreboard(getScoreboard());
		}
	}
}
