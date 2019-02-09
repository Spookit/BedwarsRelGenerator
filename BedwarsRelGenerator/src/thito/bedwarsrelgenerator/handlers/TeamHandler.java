package thito.bedwarsrelgenerator.handlers;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.DeathPlayerBag;
import thito.bedwarsrelgenerator.Upgrades;
import thito.bedwarsrelgenerator.UpgradesHandler;
import thito.bedwarsrelgenerator.UpgradesSubscriber;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.UpgradeLevelContainer;
import thito.bedwarsrelgenerator.kits.Kit;

public class TeamHandler implements UpgradesSubscriber,ArenaListener {

	/*
	 * Runtime Objects
	 */
	private final Game game;
	private final ArenaHandler arena;
	private final Team t;
	private final BedHandler bed;
	private final UpgradesHandler<?> handler;
	private final Location spawn;
	public TeamHandler(Game g,Team team,ArenaHandler a) {
		spawn = team.getSpawnLocation().clone();
		game = g;
		arena = a;
		t = team;
		if (BWG.useCitizens()) {
			handler = new CitizensTeamUpgradesHandler(BWG.get().getConfig().getConfigurationSection("team-upgrades-handler"),this);
		} else {
			handler = new TeamUpgradesHandler(BWG.get().getConfig().getConfigurationSection("team-upgrades-handler"),this);
		}
		bed = new BedHandler(a,this, team.getTargetHeadBlock(),BWG.get().getConfig().getConfigurationSection("bed-handler"));
	}
	
	public void fakeLocation(boolean restore,int y) {
		if (!restore) {
			t.setSpawnLocation(spawn.clone().add(0,y,0));
		} else {
			t.setSpawnLocation(spawn.clone());
		}
	}
	
	public UpgradesHandler<?> getUpgradesHandler() {
		return handler;
	}
	
	public Team getTeam() {
		return t;
	}
	public BedHandler getBed() {
		return bed;
	}
	public void arenaStart() {
		bed.arenaStart();
		for (Upgrades u : Upgrades.UPGRADES) u.subscribe(this);
	}
	public void removeLoadouts(Upgrades u) {
		for (Player p : getSubscribers()) {
			removeLoadouts(p,u);
		}
	}
	public void removeLoadouts(Player p,Upgrades u) {
		for (ItemStack i : p.getInventory()) {
			if (i != null && Util.isLoadout(i,u.getClass().getSimpleName())) {
				p.getInventory().removeItem(i);
			}
		}
	}
	public Kit defaultLoadouts(Player p) {
		Kit k = getArenaHandler().selected.get(p.getName());
		return k;
	}
	public void giveLoadouts(Player p) {
		Kit items = defaultLoadouts(p);
		if (items != null) {
			for (ItemStack i : items.getItems()) {
				Util.modify(i, this);
				Util.placeNeatfully(p, i);
			}
			if (!items.isAllowLoadouts()) return;
		}
		for (Upgrades u : Upgrades.UPGRADES) {
			UpgradeLevelContainer con = u.getData().getLevels().get(u.getLevel(this));
			if (con != null) {
				con.getLoadouts().forEach(a->{
					ItemStack i = a.parseItem();
					Util.modify(i, this);
					Util.setLoadout(i, u.getClass().getSimpleName());
					Util.replaceAndPlaceLoadout(p, i, u.getClass().getSimpleName());
				});
			}
		}
	}
	public void giveLoadouts(Upgrades u) {
		for (Player p : getSubscribers()) {
			UpgradeLevelContainer con = u.getData().getLevels().get(u.getLevel(this));
			if (con != null) {
				con.getLoadouts().forEach(a->{
					ItemStack i = a.parseItem();
					Util.modify(i, this);
					Util.setLoadout(i, u.getClass().getSimpleName());
					Util.replaceAndPlaceLoadout(p, i, u.getClass().getSimpleName());
				});
			}
		}
	}
	public void giveLoadouts() {
		for (Player p : getSubscribers()) {
			giveLoadouts(p);
		}
	}
	@Override
	public Collection<Player> getSubscribers() {
		return t.getPlayers();
	}
	@Override
	public Game getGame() {
		return game;
	}
	@Override
	public void arenaStop() {
		bed.arenaStop();
		for (Player p : getSubscribers()) {
			DeathPlayerBag.PLAYERS.remove(p);
			DeathPlayerBag.INVINCIBLE.remove(p);
		}
		for (Upgrades u : Upgrades.UPGRADES) {
			u.unsubscribe(this);
		}
	}
	@Override
	public ArenaHandler getArenaHandler() {
		return arena;
	}
}
