package thito.bedwarsrelgenerator.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.ArenaListener;
import thito.bedwarsrelgenerator.BWG;
import thito.bedwarsrelgenerator.BedwarsSelector;
import thito.bedwarsrelgenerator.Util;
import thito.bedwarsrelgenerator.containers.GeneratorLevelContainer;
import thito.bedwarsrelgenerator.containers.GeneratorTypeContainer;
import thito.bedwarsrelgenerator.containers.MessageContainer;
import thito.bedwarsrelgenerator.containers.PlayerStatsContainer;
import thito.bedwarsrelgenerator.containers.TitleContainer;
import thito.bedwarsrelgenerator.kits.Kit;
import thito.bedwarsrelgenerator.tracker.TrackerHandler;
import thito.breadcore.spigot.nbt.ItemStackTag;
import thito.breadcore.utils.StrUtil;

public class ArenaHandler implements ArenaListener {

	private final Game game;
	private final Map<String,TitleContainer> titles = new HashMap<>();
	private final Map<String,MessageContainer> messages = new HashMap<>();
	private final GameCageHandler cages = new GameCageHandler(this);
	private final ConfigurationSection config;
	/*
	 * Runtime Handlers
	 * a list of objects that could never stay forever
	 */
	private final Set<TeamHandler> teams = new HashSet<>();
	private final Set<GeneratorHandler> generators = new HashSet<>();
	final Map<String,Kit> selected = new HashMap<>();
	final Map<String,TrackerHandler> trackers = new HashMap<>();
	private final PlayerStatsContainer stats = new PlayerStatsContainer();
	private final List<String> deathDrops;
	private final int maxDeathDrops;
	private final int respawnDelay;
	private boolean suspend = false;
	public ArenaHandler(Game g,ConfigurationSection sec) {
		game = g;
		config = sec;
		respawnDelay = sec.getInt("respawn-delay");
		maxDeathDrops = sec.getInt("max-death-drops");
		deathDrops = sec.getStringList("death-item-drops");
		for (String t : sec.getConfigurationSection("titles").getKeys(false)) {
			titles.put(t, new TitleContainer(sec.getConfigurationSection("titles."+t)));
		}
		for (String m : sec.getConfigurationSection("announcements").getKeys(false)) {
			messages.put(m, new MessageContainer(sec.getConfigurationSection("announcements."+m)));
		}
		for (ResourceSpawner spawner : g.getResourceSpawners()) {
			ConfigurationSection av = BWG.get().getConfig().getConfigurationSection("generator.types."+spawner.getName());
			if (av != null)
				generators.add(new GeneratorHandler(BWG.get().getConfig().getConfigurationSection("generator"), spawner, this, new GeneratorTypeContainer(av, BWG.get().getConfig().getConfigurationSection("generator.levels"))));
		}
	}
	public PlayerStatsContainer getStats() {
		return stats;
	}
	public Map<String,String> additionalPlaceholders(Player p,Map<String,String> map) {
		for (GeneratorHandler h : generators) {
			map.put("upgrade_time_"+h.getSpawner().getName(), h.maxLevel ? h.getConfig().getString("messages.level-max") : Util.formatSeconds(h.upgradeCountdownLeft));
			map.put("countdown_"+h.getSpawner().getName(), Util.formatSeconds(h.countdownLeft));
			map.put("generator_level_"+h.getSpawner().getName(), h.getCurrent() == null ? "Unknown" : h.getCurrent().getName());
			GeneratorLevelContainer con = h.getNext();
			map.put("generator_nextlevel_"+h.getSpawner().getName(), con == null ? BWG.get().getConfig().getString("generator.messages.maxed") : con.getName());
		}
		map.put("time_left", BedwarsSelector.f(getGame()));
		Kit k = selected.get(p.getName());
		map.put("selected_kit", k == null ? "&cNone" : k.getName());
		return map;
	}
	public void selectKit(Player p,Kit k) {
		if (getByPlayer(p) == null) return;
		selected.put(p.getName(), k);
		p.sendMessage(ChatColor.GREEN+"You selected "+k.getName()+" kit.");
	}
	public void removeKitSelectors() {
		for (Player p : getGame().getPlayers()) {
			for (ItemStack i : p.getInventory().getContents()) {
				if ( i == null ) continue;
				final ItemStackTag tag = new ItemStackTag(i);
				if (tag.getNBT().getString("kit_selector").equals("dummy")) {
					p.getInventory().removeItem(i);
				}
			}	
		}
	}
	public Set<TeamHandler> getTeams() {
		return teams;
	}
	public void setSuspended(boolean suspended) {
		suspend = suspended;
	}
	public boolean isSuspended() {
		return suspend;
	}
	public int getRespawnDelay() {
		return respawnDelay;
	}
	public int getMaxDeathDrops() {
		return maxDeathDrops;
	}
	
	public List<String> getDeathDrops() {
		return deathDrops;
	}
	
	public void sendTitle(String titleName,Player p,Object...format) {
		TitleContainer cont = titles.get(titleName);
		if (cont != null) cont.send(p,format);
	}
	public void sendTitleToAll(String titleName,Object...format) {
		TitleContainer cont = titles.get(titleName);
		if (cont != null) getGame().getPlayers().forEach(a->{
			cont.send(a,format);
		});
	}
	public void sendTitleToTeam(String titleName,Collection<Player> p,Object...format) {
		TitleContainer cont = titles.get(titleName);
		if (cont != null) p.forEach(a->{
			cont.send(a,format);
		});
	}
	
	public void announce(String messageName,Object...format) {
		MessageContainer cont = messages.get(messageName);
		if (cont != null) {
			cont.broadcast(getGame(),format);
		}
	}
	
	public void announce(Team team,String messageName,Object...format) {
		MessageContainer cont = messages.get(messageName);
		if (cont != null) {
			cont.broadcast(team,format);
		}
	}
	
	public String getDeathItemMessage(Object...args) {
		return StrUtil.format(config.getString("death-item-message"),args);
	}
	
	public TeamHandler getByPlayer(Player p) {
		for (TeamHandler h : teams) {
			if (h.getSubscribers().contains(p)) return h;
		}
		return null;
	}
	
	public TrackerHandler getTrack(Player p) {
		return trackers.get(p.getName());
	}
	
	public TeamHandler get(Team t) {
		for (TeamHandler h : teams) {
			if (h.getTeam() == t) return h;
		}
		return null;
	}
	
	public Game getGame() {
		return game;
	}
	public void arenaPreStart() {
		for (GeneratorHandler h : generators) {
			h.arenaPreStart();
		}
	}
	public void arenaStart() {
		for (Team t : getGame().getPlayingTeams()) {
			TeamHandler h= new TeamHandler(getGame(), t, getArenaHandler());
			h.arenaPreStart();
			teams.add(h);
			h.arenaStart();
			for (Player p : h.getSubscribers()) {
				TrackerHandler hn;
				trackers.put(p.getName(), hn=new TrackerHandler(p, getArenaHandler(), h));
				hn.arenaStart();
			}
		}
		cages.arenaStart();
		for (GeneratorHandler h : generators) {
			h.arenaStart();
		}
	}
	
	public void arenaStop() {
		cages.arenaStop();
		for (TeamHandler t : teams) {
			t.arenaStop();
		}
		for (GeneratorHandler h : generators) {
			h.arenaStop();
		}
		for (TrackerHandler h : trackers.values()) {
			h.arenaStop();
		}
		teams.clear();
		selected.clear();
		game.stop();
		game.setState(GameState.WAITING);
		game.setTimeLeft(BedwarsRel.getInstance().getMaxLength());
		trackers.clear();
	}

	public String toString() {
		return getGame().getName();
	}
	@Override
	public ArenaHandler getArenaHandler() {
		return this;
	}
	
}
