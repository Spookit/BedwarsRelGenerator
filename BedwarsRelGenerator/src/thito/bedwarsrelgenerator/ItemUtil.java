package thito.bedwarsrelgenerator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

	public static int withdraw(Player p,Material m,int amount) {
		ItemStack[] c = p.getInventory().getContents();
		for (int i = 0; i < c.length; i++) {
			ItemStack it = c[i];
			if (it != null && it.getType() == m) {
				if (amount <= 0) break;
				if (it.getAmount() <= amount) {
					amount-=it.getAmount();
					c[i] = null;
				} else {
					ItemStack cl = it.clone();
					cl.setAmount(cl.getAmount()-amount);
					c[i] = cl;
					break;
				}
			}
		}
		p.getInventory().setContents(c);
		return amount;
	}
	
}
