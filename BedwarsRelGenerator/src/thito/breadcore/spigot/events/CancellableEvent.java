package thito.breadcore.spigot.events;

import org.bukkit.event.Cancellable;

public abstract class CancellableEvent extends PluginEvent implements Cancellable {

	public CancellableEvent() {
		super();
	}
	public CancellableEvent(boolean async) {
		super(async);
	}
	boolean cancel = false;
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean paramBoolean) {
		cancel = paramBoolean;
	}
	
	public static <E extends CancellableEvent> boolean callEvent(E e) {
		PluginEvent.callEvent(e);
		return e.isCancelled();
	}

}
