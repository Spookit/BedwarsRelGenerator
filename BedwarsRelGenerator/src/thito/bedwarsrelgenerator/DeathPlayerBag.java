package thito.bedwarsrelgenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.game.PlayerStorage;
import thito.breadcore.utils.Mutable;

public class DeathPlayerBag implements Listener {

	public static final Set<Player> INVINCIBLE = new HashSet<>();
	public static final Set<Player> PLAYERS = new HashSet<Player>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Map<Player,PlayerStorage> settings = new HashMap<>();
		public boolean add(Player p) {
			boolean success = super.add(p);
			if (success) {
				for (PotionEffect e : p.getActivePotionEffects()) {
					p.removePotionEffect(e.getType());
				}
				PlayerStorage set = new PlayerStorage(p);
				set.store();
				set.clean();
				settings.put(p, set);
				p.setAllowFlight(true);
				p.setFlying(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 10, false, false), true);
			}
			return success;
		}
		public boolean remove(Object p) {
			boolean success = super.remove(p);
			if (success && p instanceof Player) {
				((Player)p).setAllowFlight(false);
				PlayerStorage storage = settings.remove(p);
				((Player)p).setFlying(false);
				((Player)p).removePotionEffect(PotionEffectType.INVISIBILITY);
				if (storage != null) {
					storage.restore();
					settings.remove(p);
				}
			}
			return success;
		}
	};
	public static final List<String> EXEMPT = Arrays.asList(
			"PlayerMoveEvent","PlayerTeleportEvent",
			"AsyncPlayerChatEvent","PlayerChatEvent",
			"PlayerChatTabCompleteEvent", "PlayerCommandPreprocessEvent",
			"PlayerCommandSendEvent","PlayerJoinEvent",
			"PlayerKickEvent","PlayerLocaleChangeEvent",
			"PlayerLoginEvent","PlayerQuitEvent",
			"PlayerChangedMainHandEvent", "PlayerItemHeldEvent"
			);
	public DeathPlayerBag() {
		for (HandlerList l : HandlerList.getHandlerLists()) {
			Mutable<RegisteredListener> trustMe = new Mutable<>();
			trustMe.accept(new RegisteredListener(this, new EventExecutor() {
				
				@Override
				public void execute(Listener var1, Event var2) throws EventException {
					if (var2 instanceof PlayerEvent) {
						if (EXEMPT.contains(var2.getEventName()) || !(var2 instanceof Cancellable)) {
							RegisteredListener list = trustMe.get();
							if (list != null) l.unregister(list);
							return;
						}
						e((PlayerEvent)var2);
					}
				}
			}, EventPriority.HIGHEST, BWG.get(), true));
			l.register(trustMe.get());
		}
	}
	
	@EventHandler 
	public void a(EntityDamageEvent e) {
		e(e.getEntity(),e,false);
	}
	
	public void e(Entity p,Cancellable e,boolean haveDone) {
		if (!haveDone && e instanceof EntityDamageByEntityEvent) {
			e(((EntityDamageByEntityEvent)e).getDamager(),e,true);
		}
		if (PLAYERS.contains(p) || INVINCIBLE.contains(p)) e.setCancelled(true);
	}
	public void e(PlayerEvent c) {
		if (c instanceof Cancellable) {
			if (PLAYERS.contains(c.getPlayer()) || INVINCIBLE.contains(c.getPlayer())) {
				((Cancellable) c).setCancelled(true);
			}
		}
	}
}
