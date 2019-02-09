package thito.breadcore.spigot.nms;

import org.bukkit.inventory.ItemStack;

import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.Util;

public class NMSItemStack implements NMSObject {

	private final Object nms;
	public NMSItemStack(ItemStack item) {
		nms = ScriptUtil.run("arg0.asNMSCopy(arg1)", Util.craft("inventory.CraftItemStack"),item);
	}
	@Override
	public Object getWrapped() {
		return nms;
	}

}
