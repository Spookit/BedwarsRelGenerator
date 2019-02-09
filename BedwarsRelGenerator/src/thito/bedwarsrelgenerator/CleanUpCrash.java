package thito.bedwarsrelgenerator;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import thito.breadcore.spigot.nbt.EntityTag;

public class CleanUpCrash {

	public static final UUID UUID = UUID();
	public static void claimEntity(Entity e) {
		try {
			EntityTag tag = new EntityTag(e);
			tag.setCustomData("antiCrash", "dummy", UUID);
			tag.apply(e);
		} catch (Exception ex) {
		}
	}
	
	private static UUID UUID() {
		return java.util.UUID.randomUUID();
	}
	
	public static boolean isEntityClaimed(Entity e) {
		try {
			return new EntityTag(e).hasCustomData("antiCrash");
		} catch (Exception ex) {
		}
		return false;
	}
	
	public static void cleanUp() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (isEntityClaimed(e)) {
					e.remove();
				}
			}
		}
	}
	
}
