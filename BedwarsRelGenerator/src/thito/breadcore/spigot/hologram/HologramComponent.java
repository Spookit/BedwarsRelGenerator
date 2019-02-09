package thito.breadcore.spigot.hologram;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import thito.breadcore.utils.Util;

public interface HologramComponent {

	public void despawn();
	public void spawn(Location loc);
	public Entity getEntity();
	public double getHeight();
	public static ArmorStand create(String name,Location loc) {
		return create(name,loc,false);
	}
	public static ArmorStand create(String name,Location loc,boolean usenms) {
		if (usenms) {
			try {
				Class<?> nmsClass = Util.nms("EntityArmorStand");
				Constructor<?> nmsCons = nmsClass.getConstructor(Util.nms("World"),double.class,double.class,double.class);
				Object wo = loc.getWorld().getClass().getMethod("getHandle").invoke(loc.getWorld());
				Object nms = nmsCons.newInstance(wo,loc.getX(),loc.getY(),loc.getZ());
				nms.getClass().getMethod("setInvisible",boolean.class).invoke(nms,true);
				wo.getClass().getMethod("addEntity", Util.nms("Entity")).invoke(wo, nms);
				ArmorStand as = (ArmorStand)nms.getClass().getMethod("getBukkitEntity").invoke(nms);
				if (name != null) {
					as.setCustomName(name);
					as.setCustomNameVisible(true);
				}
				as.setGravity(false);
				return as;
			} catch (Exception e) {
			}
		}
		ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setVisible(false);
		if (name != null) {
			as.setCustomName(name);
			as.setCustomNameVisible(true);
		}
		as.setGravity(false);
		return as;
	}
}
