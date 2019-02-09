package thito.bedwarsrelgenerator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.handlers.CitizensTeamUpgradesHandler;
import thito.bedwarsrelgenerator.handlers.TeamHandler;

public class CitizensListener implements Listener {
	@EventHandler
	public void npcClick(NPCLeftClickEvent e) {
		click(e);
	}
	
	@EventHandler
	public void npcClick(NPCRightClickEvent e) {
		click(e);
	}
	
	public void click(NPCClickEvent e) {
		final Player player = e.getClicker();
		final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		final ArenaHandler handler = BWG.get(game);
		if (handler== null) return;
		TeamHandler th = handler.getByPlayer(player);
		if (th == null) return;
		((CitizensTeamUpgradesHandler)th.getUpgradesHandler()).openShop(player, null, 0,e.getNPC());
	}
}
