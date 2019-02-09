package thito.bedwarsrelgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.TeamColor;
import net.md_5.bungee.api.ChatColor;
import thito.bedwarsrelgenerator.containers.PlayerStatsContainer;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.handlers.TeamHandler;
import thito.bedwarsrelgenerator.tracker.TrackerHandler;
import thito.breadcore.spigot.nbt.ItemStackTag;
import thito.breadcore.utils.ArrayUtil;
import thito.breadcore.utils.ScoreMap;
import thito.breadcore.utils.StrUtil;

public class BWGListener implements Listener {

	private RegisteredListener reg;
	public BWGListener() {
		/* Reject the original Bedwars Player Listener */
		RegisteredListener[] list = PlayerDeathEvent.getHandlerList().getRegisteredListeners();
		for (RegisteredListener l : list) {
			if (l.getPlugin().equals(BedwarsRel.getInstance())) {
				PlayerDeathEvent.getHandlerList().unregister(l);
				reg = l;
				break;
			}
		}
	}
	public void injectBackTheOldOne() {
		if (reg != null) {
			PlayerDeathEvent.getHandlerList().register(reg);
			reg = null;
		}
	}
	
	@EventHandler
	public void clck(InventoryClickEvent e) {
//		org.bukkit.plugin.java.PluginClassLoader
		if (!(e.getWhoClicked() instanceof Player)) return;
		final ItemStack i = e.getCurrentItem();
		Player p = (Player)e.getWhoClicked();
		Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
		if (g == null) return;
		ArenaHandler h = BWG.get(g);
		if (h == null) return;
		if (h.getByPlayer(p) == null) return;
		if (i != null) {
			final ItemStackTag tag = new ItemStackTag(i);
			if (tag.getNBT().getString("kit_selector").equals("dummy")) {
				e.setCancelled(true);
				BWG.getKits().select(p, h, a->{
					h.selectKit(p, a);
				}, 0);
			}
		}
	}
	
	@EventHandler
	public void inter(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
		if (g == null) return;
		ArenaHandler h = BWG.get(g);
		if (h == null) return;
		if (h.getByPlayer(p) == null) return;
		final ItemStack i = e.getItem();
		if (i != null) {
			final ItemStackTag tag = new ItemStackTag(i);
			if (tag.getNBT().getString("kit_selector").equals("dummy")) {
				e.setCancelled(true);
				BWG.getKits().select(p, h, a->{
					h.selectKit(p, a);
				}, 0);
			}
		}
	}
	
	@EventHandler
	public void event(BedwarsResourceSpawnEvent e) {
		ArenaHandler handler = BWG.get(e.getGame());
		if (handler != null) {
			if (handler.isSuspended()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void event(BedwarsPlayerLeaveEvent e) {
		DeathPlayerBag.INVINCIBLE.remove(e.getPlayer());
		DeathPlayerBag.PLAYERS.remove(e.getPlayer());
	}
	
	
	@EventHandler
	public void arenaStartEvent(BedwarsGameStartedEvent e) {
		ArenaHandler handler = BWG.get(e.getGame());
		if (handler != null) {
			handler.arenaStart();
		}
	}
	
	@EventHandler
	public void arenaPreStartEvent(BedwarsGameStartEvent e) {
		ArenaHandler handler = BWG.get(e.getGame());
		if (handler != null) {
			handler.arenaPreStart();
		}
	}
	
	@EventHandler
	public void arenaStopEvent(BedwarsGameEndEvent e) {
		ArenaHandler handler = BWG.get(e.getGame());
		if (handler != null) {
			handler.arenaStop();
		}
	}
	
	
	
	@EventHandler
	public void dropItem(PlayerDropItemEvent e) {
		if (Util.isLoadout(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED+"You can't drop loadout item!");
		}
	}
	
	
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	public void playerDamage(EntityDamageEvent e) {
		if (!(e.getEntity()instanceof Player)) return;
		final Player player = (Player)e.getEntity();
		final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		final ArenaHandler handler = BWG.get(game);
		if (handler== null) return;
		if (game.getState() != GameState.RUNNING || handler.isSuspended()) return;
		double health = player.getHealth();
		if (health - e.getFinalDamage() <= 0 || e.getCause() == DamageCause.VOID) {
			player.setLastDamageCause(e);
			e.setCancelled(true);
			PlayerDeathEvent ev;
			playerDeathEvent(ev=new PlayerDeathEvent(player, new ArrayList<>(), 0, 0, 0, 0, null));
			if (!ev.getKeepInventory()) {
				player.getInventory().clear();
				player.getInventory().setArmorContents(new ItemStack[4]);
			}
		}
	}
	
	@EventHandler
	public void bedwarsLeave(BedwarsPlayerLeaveEvent e) {
		Game game = e.getGame();
		ArenaHandler handler = BWG.get(game);
		if (handler== null) return;
		TrackerHandler tracker=handler.getTrack(e.getPlayer());
		if (tracker ==null) return;
		tracker.arenaStop();
	}
	
	@EventHandler
	public void a(FoodLevelChangeEvent e) {
		final HumanEntity ent = e.getEntity();
		if (ent instanceof Player) {
			final Player player = (Player)ent;
			final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
			if (game == null) {
				return;
			}
			e.setCancelled(true);
			e.setFoodLevel(19);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void playerDeathEvent(PlayerDeathEvent e) {
		final Player player = e.getEntity();
		final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		final ArenaHandler handler = BWG.get(game);
		if (handler== null) return;
		TeamHandler th = handler.getByPlayer(player);
		if (th == null) return;
		e.getEntity().setHealth(e.getEntity().getMaxHealth());
		e.setDeathMessage(null);
		e.setDroppedExp(0);
		Player killer = player.getKiller();
		if (game.getState() == GameState.RUNNING) {
			if (killer == null) {
				killer = game.getPlayerDamager(player);
			}
			game.getCycle().onPlayerDies(player, killer);
			player.teleport(th.getTeam().getSpawnLocation().clone().add(0,30,0));
			DeathPlayerBag.PLAYERS.add(player);
			handler.sendTitle("died", player);
			ItemStack items[] = ArrayUtil.combine(e.getEntity().getInventory().getContents(),e.getEntity().getInventory().getArmorContents());
			e.setKeepInventory(false);
			e.getDrops().clear();
			ScoreMap<String> left = new ScoreMap<>();
			for (String s : handler.getDeathDrops()) left.put(s, handler.getMaxDeathDrops());
			if (killer != null) {
				Map<Material,List<ItemStack>> got = new HashMap<>();
				for (ItemStack item : items) {
					if (item == null) continue;
					if (handler.getDeathDrops().contains(item.getType().name())) {
						int remain = left.getOrDefault(item.getType().name(),0);
						if (remain > 0) {
							//item - the item
							// remain - left remain chances
							if (item.getAmount() < remain) {
								ItemStack cl = item.clone();
								cl.setAmount(cl.getAmount()-remain);
								left.subtract(item.getType().name(), cl.getAmount());
								List<ItemStack> list = got.get(item.getType());
								if (list == null) got.put(item.getType(), list = new ArrayList<>());
								list.add(cl);
							} else {
								left.remove(item.getType().name());
								List<ItemStack> list = got.get(item.getType());
								if (list == null) got.put(item.getType(), list = new ArrayList<>());
								ItemStack cl = item.clone();
								cl.setAmount(remain);
								list.add(cl);
							}
						}
					}
				}
				for (Entry<Material,List<ItemStack>> en : got.entrySet()) {
					int amount = 0;
					for (ItemStack item : en.getValue()) {
						amount+=item.getAmount();
					}
					killer.getInventory().addItem(en.getValue().toArray(new ItemStack[en.getValue().size()]));
					killer.sendMessage(StrUtil.color(handler.getDeathItemMessage(amount,en.getKey().name(),player.getName())));
				}
				handler.getStats().add(killer, PlayerStatsContainer.KILL_ID);
				if (handler.getStats().get(killer.getName(), PlayerStatsContainer.DATA_LASTACTION).equals(PlayerStatsContainer.ACTION_KILL)) {
					handler.getStats().add(killer, PlayerStatsContainer.KILLSTREAK_ID);
				}
				handler.getStats().set(killer.getName(), PlayerStatsContainer.DATA_LASTACTION, PlayerStatsContainer.ACTION_KILL);
			}
			handler.getStats().add(player, PlayerStatsContainer.DEAD_ID);
			handler.getStats().set(player.getName(), PlayerStatsContainer.DATA_LASTACTION, PlayerStatsContainer.ACTION_DEAD);
			handler.getStats().reset(player, PlayerStatsContainer.KILLSTREAK_ID);
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			if (th.getTeam().isDead(game)) {
				handler.getStats().add(player, PlayerStatsContainer.FINALKILL_ID);
				return;
			}
			new BukkitRunnable() {
				int remain = handler.getRespawnDelay();
				public void run() {
					if (game.getState() != GameState.RUNNING || game.getCycle().isEndGameRunning()) {
						cancel();
						return;
					}
					if (remain > 0) {
						handler.sendTitle("respawn-count", player, remain);
						remain--;
					} else {
						handler.sendTitle("respawned", player);
						DeathPlayerBag.PLAYERS.remove(player);
						PlayerRespawnEvent respawn = new PlayerRespawnEvent(player, th.getTeam().getSpawnLocation(), false);
						handler.getGame().getCycle().onPlayerRespawn(respawn, player);
						player.teleport(respawn.getRespawnLocation());
						th.giveLoadouts(player);
						cancel();
					}
				}
			}.runTaskTimer(BWG.get(), 20L*5, 20L);
		}
	}
	
	@EventHandler
	public void bedDestroyEvent(BedwarsTargetBlockDestroyedEvent e) {
		ArenaHandler handler = BWG.get(e.getGame());
		if (handler != null) {
			handler.getStats().add(e.getPlayer(), PlayerStatsContainer.BEDBREAK_ID);
			TeamHandler th = handler.get(e.getTeam());
			if (th != null) {
				th.getBed().setDestroyed(true);
				handler.sendTitleToTeam("bed-destroy", th.getSubscribers());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void placeEvent(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		BlockState state = b.getState();
		MaterialData data = state.getData();		
		if (data instanceof Colorable) {
			recolor(p,(Colorable)data);
			state.setData(data);
			state.update(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void pickup(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		BlockState state = b.getState();
		MaterialData data = state.getData();		
		if (data instanceof Colorable) {
			recolor(p,(Colorable)data);
			state.setData(data);
			state.update(true);
		}
	}
	
	public void recolor(Player p,Colorable c) {
		Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
		if (g == null) return;
		ArenaHandler handler = BWG.get(g);
		if (handler == null) return;
		TeamHandler team = handler.getByPlayer(p);
		if (team == null) return;
		TeamColor color = team.getTeam().getColor();
		c.setColor(color.getDyeColor());
		
	}
}
