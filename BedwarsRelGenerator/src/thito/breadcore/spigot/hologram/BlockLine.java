package thito.breadcore.spigot.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class BlockLine implements HologramComponent {

	private ItemStack head;
	public BlockLine(ItemStack head) {
		this.head = head;
	}
	ArmorStand entity;
	@Override
	public void spawn(Location loc) {
		entity = HologramComponent.create(null, loc);
		setHead(head);
	}
	
	public void setHead(ItemStack item) {
		this.head = item;
		if (getEntity() != null) getEntity().setHelmet(head);
	}
	
	public ItemStack getHead() {
		return head;
	}
	
	@Override
	public ArmorStand getEntity() {
		return entity;
	}

	@Override
	public double getHeight() {
		return 0.89;
	}

	@Override
	public void despawn() {
		if (entity != null) entity.remove();
	}

}
