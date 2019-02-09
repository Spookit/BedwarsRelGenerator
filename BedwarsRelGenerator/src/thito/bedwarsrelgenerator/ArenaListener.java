package thito.bedwarsrelgenerator;

import java.util.HashSet;
import java.util.Set;

import io.github.bedwarsrel.game.Game;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;

public interface ArenaListener {

	public static Set<ArenaListener> LISTENERS = new HashSet<>();
	public Game getGame();
	public ArenaHandler getArenaHandler();
	public void arenaStart();
	public void arenaStop();
	public default void arenaPreStart() {}
	
}
