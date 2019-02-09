package thito.breadcore.spigot.nms;

import org.bukkit.World;
import org.bukkit.entity.Player;

import thito.breadcore.spigot.objects.SpigotGameProfile;
import thito.breadcore.utils.ObjectWrapper;
import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.Util;

public class NMSEntityPlayer implements NMSObject {

	private final Object o;
	public NMSEntityPlayer(SpigotGameProfile profile,World w) {
		o = new ObjectWrapper(Util.nms("EntityPlayer"),
				new NMSServer().getWrapped(),
				new NMSWorld(w).getWrapped(),
				profile,
				new NMSPlayerInteractManager(w)).getInstance();
	}
	public NMSEntityPlayer(Player p) {
		o = ScriptUtil.run("arg0.getHandle()", p);
	}
	@Override
	public Object getWrapped() {
		return o;
	}
}
