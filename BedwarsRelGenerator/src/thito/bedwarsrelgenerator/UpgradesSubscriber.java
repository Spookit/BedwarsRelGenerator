package thito.bedwarsrelgenerator;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;

public interface UpgradesSubscriber {

	public Collection<Player> getSubscribers();
	public Team getTeam();
	public Game getGame();
	public ArenaHandler getArenaHandler();
	public void removeLoadouts(Upgrades u);
	public void giveLoadouts(Upgrades u);

}
