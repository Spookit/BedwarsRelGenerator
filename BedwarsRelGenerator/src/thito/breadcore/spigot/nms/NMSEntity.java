package thito.breadcore.spigot.nms;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.StrUtil;
import thito.breadcore.utils.Util;

public class NMSEntity implements NMSObject {

	private final Object nms;
	public NMSEntity(World world,EntityType type) {
		nms = ScriptUtil.run("new arg0(arg1)", Util.nms("Entity"+StrUtil.capitalizeEnum(type)),new NMSWorld(world).getWrapped());
	}
	
	public NMSEntity(Entity e) {
		nms = ScriptUtil.run("arg0.getHandle()", e);
	}
	
	@Override
	public Object getWrapped() {
		return nms;
	}

}
