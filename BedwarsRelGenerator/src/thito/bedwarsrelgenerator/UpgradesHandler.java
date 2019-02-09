package thito.bedwarsrelgenerator;

import org.bukkit.entity.Player;

public interface UpgradesHandler<T> {
	public void openShop(Player p,Upgrades up,int page,T npc);
}
