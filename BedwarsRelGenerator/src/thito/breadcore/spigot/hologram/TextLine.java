package thito.breadcore.spigot.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class TextLine implements HologramComponent {

	private String name;
	public TextLine(String name) {
		this.name = name;
	}
	ArmorStand entity;
	@Override
	public void spawn(Location loc) {
		entity = HologramComponent.create(name,loc);
		entity.setSmall(true);
		setName(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (entity != null) {
			entity.setCustomName(name);
			entity.setCustomNameVisible(name != null && !name.isEmpty());
		}
	}
	@Override
	public ArmorStand getEntity() {
		return entity;
	}

	@Override
	public double getHeight() {
		return 0.23;
	}
	@Override
	public void despawn() {
		if (entity != null) entity.remove();
	}

}
