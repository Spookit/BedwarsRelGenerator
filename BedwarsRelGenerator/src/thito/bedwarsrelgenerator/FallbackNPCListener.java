package thito.bedwarsrelgenerator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.villager.MerchantCategory;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.handlers.TeamHandler;
import thito.bedwarsrelgenerator.handlers.TeamUpgradesHandler;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;

public class FallbackNPCListener implements Listener {

	private final ItemStack SHOP= PluginInventory.create(XMaterial.CHEST, "&e&lSOLO UPGRADES", "&7This shop contains items you need in this game.");
	private final ItemStack TEAM = PluginInventory.create(XMaterial.ENDER_CHEST, "&e&lTEAM UPGRADES", "&7This shop contains item upgrades that affect to all team members.");
	private final ItemStack BORDER = PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7");
	@EventHandler
	public void a(BedwarsOpenShopEvent e) {
		Game game = e.getGame();
		CommandSender sender = e.getPlayer();
		if (!(sender instanceof Player)) return;
		Player player = (Player)sender;
		e.setCancelled(true);
		PluginInventory inv = PluginInventory.create(InventoryType.HOPPER, "&1Shop");
		inv.getInventory().setItem(0, SHOP);
		inv.addConsumer(0, ev->{
			if (game.getPlayerSettings(player).useOldShop()) {
				MerchantCategory.openCategorySelection((Player) player, (Game) game);
			} else {
				NewItemShop itemShop = game.getNewItemShop(player);
				if (itemShop == null) {
					itemShop = game.openNewItemShop(player);
				}
				itemShop.setCurrentCategory(null);
				itemShop.openCategoryInventory(player);
			}
		});
		for (int i = 1; i < 4; i ++) {
			inv.getInventory().setItem(i, BORDER);
		}
		inv.getInventory().setItem(4, TEAM);
		inv.addConsumer(4, ev->{
			final ArenaHandler handler = BWG.get(game);
			if (handler== null) return;
			TeamHandler th = handler.getByPlayer(player);
			if (th == null || !(th.getUpgradesHandler() instanceof TeamUpgradesHandler)) return;
			((TeamUpgradesHandler)th.getUpgradesHandler()).openShop(player, null, 0,e.getEntity());
		});
		player.openInventory(inv.getInventory());
	}
}
