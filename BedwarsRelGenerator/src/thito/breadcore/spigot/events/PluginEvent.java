package thito.breadcore.spigot.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PluginEvent extends Event {
	public PluginEvent() {
		
	}
	public PluginEvent(boolean async) {
		super(async);
	}
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	public static <E extends PluginEvent> E callEvent(E e) {
		if (e.isAsynchronous()) {
			new Thread(new Runnable() {
				public void run() {
					Bukkit.getPluginManager().callEvent(e);
				}
			},"EVENT:"+e.getClass().getName()).start();
		} else Bukkit.getPluginManager().callEvent(e);
		return e;
	}
}
