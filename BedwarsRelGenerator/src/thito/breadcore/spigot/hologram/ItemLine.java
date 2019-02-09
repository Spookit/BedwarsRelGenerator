package thito.breadcore.spigot.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class ItemLine implements HologramComponent {

	private ItemStack item;
	private Item i;
	public ItemLine(ItemStack item) {
		this.item = item;
	}
	@Override
	public void spawn(Location loc) {
		i = loc.getWorld().dropItem(loc, item);
		//this thing to prevent item being picked
		i.setPickupDelay(99999999);
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
		if (getEntity() != null) getEntity().setItemStack(item);
	}
	
	public ItemStack getItem() {
		return item;
	}

	@Override
	public Item getEntity() {
		return i;
	}

	@Override
	public double getHeight() {
		return 0.25;
	}
	@Override
	public void despawn() {
		if (i != null) i.remove();
	}

}
