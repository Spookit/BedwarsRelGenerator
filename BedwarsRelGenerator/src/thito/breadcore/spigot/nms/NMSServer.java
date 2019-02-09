package thito.breadcore.spigot.nms;

import org.bukkit.Bukkit;

import thito.breadcore.utils.ScriptUtil;

public class NMSServer implements NMSObject {

	private final Object o;
	public NMSServer() {
		o = ScriptUtil.run("arg0.getServer()", Bukkit.getServer());
	}
	@Override
	public Object getWrapped() {
		return o;
	}
	
}
