package thito.breadcore.spigot.hologram;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import thito.bedwarsrelgenerator.CleanUpCrash;
import thito.breadcore.spigot.nbt.EntityTag;

public class Hologram {

	private final ArrayList<HologramComponent> components = new ArrayList<>();
	private Location loc;
	private final boolean up;
	public Hologram(Location loc,boolean up) {
		this.loc = loc;
		this.up = up;
	}
	public void teleport(Location loc) {
		this.loc = loc;
		double height = 0;
		List<HologramComponent> comp = new ArrayList<>(components);
//		if (up) Collections.reverse(comp);
		for (HologramComponent c : comp) {
			if (c.getEntity() != null) {
				c.getEntity().teleport(loc.clone().add(0, up ? height : -height, 0));
				height+=c.getHeight();
			}
		}
	}
	boolean spawned = false;
	public boolean isSpawned() {
		return spawned;
	}
	public void spawn() {
		if (spawned) return;
		spawned = true;
		double height = 0;
		List<HologramComponent> comp = new ArrayList<>(components);
//		if (up) Collections.reverse(comp);
		for (HologramComponent c : comp) {
			c.spawn(loc.clone().add(0, up ? height : -height, 0));
			EntityTag tag = new EntityTag(c.getEntity());
			tag.setCustomData("hologramObject", "true", UUID.randomUUID());
			tag.apply(c.getEntity());
			CleanUpCrash.claimEntity(c.getEntity());
			height+=c.getHeight();
		}
	}
	public void despawn() {
		if (!spawned) return;
		spawned = false;
		components.forEach(a->{
			a.despawn();
		});
	}
	public Location getLocation() {
		return loc;
	}
	public void addComponent(HologramComponent c) {
		if (c == null) return;
		if (up) {
			components.add(0,c);
		} else {
			components.add(c);
		}
		if (spawned) {
			despawn();
			spawn();
		}
	}
	public List<HologramComponent> getComponents() {
		List<HologramComponent> comp = new ArrayList<>(components);
//		if (up) Collections.reverse(comp);
		return comp;
	}
	public HologramComponent removeComponent(int index) {
		HologramComponent component = components.remove(index);
		if (component.getEntity() == null) return component;
		if (!component.getEntity().isDead()) {
			EntityTag tag = new EntityTag(component.getEntity());
			tag.setCustomData("hologramObject", "false", UUID.randomUUID());
			tag.apply(component.getEntity());
			component.getEntity().remove();
		}
		if (spawned) {
			despawn();
			spawn();
		}
		return component;
	}
	
	/*
	 * Static Listeners
	 */
	private static boolean REGISTERED = false;
	public static void registerListener(JavaPlugin core) {
		if (REGISTERED) return;
		REGISTERED = true;
		core.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void e(PlayerInteractEntityEvent e) {
				Entity en = e.getRightClicked();
				EntityTag tag = new EntityTag(en);
				if (tag.getCustomData("hologramObject").asBoolean()) {
					e.setCancelled(true);
				}
			}
			@EventHandler
			public void r(EntityDamageEvent e) {
				Entity en = e.getEntity();
				EntityTag tag = new EntityTag(en);
				if (tag.getCustomData("hologramObject").asBoolean()) {
					e.setCancelled(true);
				}
			}
			@EventHandler
			public void d(EntityDeathEvent e) {
				Entity en = e.getEntity();
				EntityTag tag = new EntityTag(en);
				if (tag.getCustomData("hologramObject").asBoolean()) {
					e.getEntity().setHealth(10);
				}
			}
			@EventHandler
			public void c(PlayerArmorStandManipulateEvent e) {
				Entity en = e.getRightClicked();
				EntityTag tag = new EntityTag(en);
				if (tag.getCustomData("hologramObject").asBoolean()) {
					e.setCancelled(true);
				}
			}
		}, core);
	}
}
