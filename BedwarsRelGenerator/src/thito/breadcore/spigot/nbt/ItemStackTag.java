package thito.breadcore.spigot.nbt;

import org.bukkit.inventory.ItemStack;

import thito.breadcore.spigot.nms.NMSItemStack;
import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.Util;

public class ItemStackTag implements NBTContainer<ItemStack> {

	private NBTTagCompound nbt;
	private final Object nmsItem;
	public ItemStackTag(ItemStack item) {
		nmsItem = new NMSItemStack(item).getWrapped();
		nbt = nmsItem == null ? new NBTTagCompound() : new NBTTagCompound(ScriptUtil.run("arg0.getTag()",nmsItem));
		if (nbt == null || nbt.asNBTBase() == null) {
			nbt = new NBTTagCompound();
			if (nmsItem != null) ScriptUtil.run("arg0.setTag(arg1)", nmsItem,nbt.asNBTBase());
		}
	}
	@Override
	public NBTTagCompound getNBT() {
		return nbt;
	}
	
	public void apply(ItemStack item) {
		ItemStack copy = (ItemStack)ScriptUtil.run("arg0.asBukkitCopy(arg1)", Util.craft("inventory.CraftItemStack"),nmsItem);
		item.setItemMeta(copy.getItemMeta());
	}
	
}
