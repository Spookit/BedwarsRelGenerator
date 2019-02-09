package thito.bedwarsrelgenerator.broadcaster;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import thito.bedwarsrelgenerator.BWG;

public interface Broadcaster {

	public static final Set<Broadcaster> SERVICE = new HashSet<Broadcaster>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Broadcaster b) {
			if (b == null) return false;
			boolean success = super.add(b);
			if (success) {
				if (b instanceof Listener) {
					Bukkit.getPluginManager().registerEvents((Listener)b, BWG.get());
				}
			}
			return success;
		}
		
		public boolean remove(Object o) {
			boolean success = super.remove(o);
			if (success) {
				if (o instanceof Listener) {
					HandlerList.unregisterAll((Listener)o);
				}
			}
			return success;
		}
		
		public boolean addAll(Collection<? extends Broadcaster> s) {
			boolean change = false;
			for (Broadcaster x : s) {
				if (add(x)) {
					change = true;
				}
			}
			return change;
		}
		
		public boolean removeAll(Collection<?> o) {
			boolean change = false;
			for (Object x : o) {
				if (remove(x)) {
					change = true;
				}
			}
			return change;
		}
		
	};
	
	public String getName();
	
	public static Broadcaster getService(String name) {
		for (Broadcaster b : SERVICE) {
			if (b.getName().equals(name)) {
				return b;
			}
		}
		return BWG.FALLBACK_BROADCASTER;
	}
	
	
	public void send(Player p,String msg);
	public default void send(Player p,String msg,String submsg) {
		send(p,msg);
	}
	public default void broadcast(String msg,List<Player> players) {
		for (Player p : players) {
			send(p,msg);
		}
	}
	public default void broadcast(String msg,String submsg,List<Player> players) {
		for (Player p : players) {
			send(p,msg,submsg);
		}
	}
	public void unsubscribe(Player p);
	
}
