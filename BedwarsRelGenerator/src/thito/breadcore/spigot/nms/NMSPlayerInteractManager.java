package thito.breadcore.spigot.nms;

import org.bukkit.World;

import thito.breadcore.utils.ObjectWrapper;
import thito.breadcore.utils.Util;

public class NMSPlayerInteractManager implements NMSObject {

	private final Object o;
	public NMSPlayerInteractManager(World w) {
		o = new ObjectWrapper(Util.nms("PlayerInteractManager"),new NMSWorld(w).getWrapped()).getInstance();
	}
	@Override
	public Object getWrapped() {
		return o;
	}
	
}
