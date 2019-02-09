package thito.breadcore.spigot.nms;

import org.bukkit.World;

import thito.breadcore.utils.ScriptUtil;

public class NMSWorld implements NMSObject {

	private final Object nms;
	public NMSWorld(World world) {
		nms = ScriptUtil.run("arg0.getHandle()", world);
	}
	
	@Override
	public Object getWrapped() {
		return nms;
	}

}
