package thito.bedwarsrelgenerator.rejoin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerStorage;
import io.github.bedwarsrel.game.Team;

@Deprecated
public class RejoinData {

	private final String p;
	private final Team t;
	private final Game g;
	public RejoinData(String playerName,Team t,Game g) {
		p = playerName;
		this.t=t;
		this.g=g;
	}
	public String getPlayerName() {
		return p;
	}
	public Team getTeam() {
		return t;
	}
	public Game getGame() {
		return g;
	}
	public void rejoin(Player p) {
		BedwarsRel.getInstance().getGameManager().addGamePlayer(p, g);
		if (BedwarsRel.getInstance().statisticsEnabled()) {
			BedwarsRel.getInstance().getPlayerStatisticManager().getStatistic(p);
		}
		g.getPlayerDamages().put(p, null);
		g.addPlayerSettings(p);
		new BukkitRunnable() {

			public void run() {
				for (Player playerInGame : g.getPlayers()) {
					playerInGame.hidePlayer(p);
					p.hidePlayer(playerInGame);
				}
			}
		}.runTaskLater((Plugin) BedwarsRel.getInstance(), 5);
		PlayerStorage storage = g.addPlayerStorage(p);
		t.addPlayer(p);
		storage.store();
		storage.clean();
		if (!BedwarsRel.getInstance().isBungee()) {
			final Location location = g.getPlayerTeleportLocation(p);
			if (!p.getLocation().equals((Object) location)) {
				g.getPlayerSettings(p).setTeleporting(true);
				if (BedwarsRel.getInstance().isBungee()) {
					new BukkitRunnable() {

						public void run() {
							p.teleport(location);
						}
					}.runTaskLater((Plugin) BedwarsRel.getInstance(), 10);
				} else {
					p.teleport(location);
				}
			}
		}
		storage.loadLobbyInventory(g);
		new BukkitRunnable() {

			public void run() {
				g.setPlayerGameMode(p);
				g.setPlayerVisibility(p);
			}
		}.runTaskLater((Plugin) BedwarsRel.getInstance(), 15);
		BedwarsPlayerJoinedEvent joinEvent = new BedwarsPlayerJoinedEvent(g, t, p);
		BedwarsRel.getInstance().getServer().getPluginManager().callEvent(joinEvent);
//		g.updateScoreboard();
		g.updateSigns();
		
	}
	
}
