package thito.bedwarsrelgenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.spigot.inventory.XMaterial;
import thito.breadcore.utils.Paginator;

public abstract class CategorizedGUI<C,V> {

	private static final int[] NON_CATEGORIZED = {9,10,11,12,13,14,15,16,17};
	private static final int[] CATEGORIZED = {9,10,11,12,13,14,15,16,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};
	private static final int[] CATEGORY_CONTENTS = {1,2,3,4,5,6,7};
	private static final int[] VALUE_CONTENTS = {19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
	private final ItemStack BARS = PluginInventory.create(XMaterial.IRON_BARS, "&7");
	private final ItemStack BORDER = PluginInventory.create(XMaterial.BLACK_STAINED_GLASS_PANE, "&7");
	private final ItemStack NEXT = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lNEXT&8 ]");
	private final ItemStack PREV = PluginInventory.create(XMaterial.SLIME_BALL, "&8[ &a&lPREVIOUS&8 ]");
	private final Map<C,? extends Collection<V>> m;
	private final String title;
	public CategorizedGUI(Map<C,? extends Collection<V>> map,String title) {
		m = map;
		this.title = title;
	}
	
	public void open(Player p,C category,int categoryPage,int valuePage) {
		Paginator<C> cats = new Paginator<>(new ArrayList<>(m.keySet()), CATEGORY_CONTENTS.length,1);
		PluginInventory inv = PluginInventory.create(category == null ? 18 : 54, title);
		for (int i : category == null ? NON_CATEGORIZED : CATEGORIZED) {
			inv.getInventory().setItem(i, BORDER);
		}
		if (cats.isValidPage(categoryPage-1)) {
			inv.getInventory().setItem(0, PREV);
			inv.addConsumer(0, e->{
				open(p,category,categoryPage-1,valuePage);
			});
		} else inv.getInventory().setItem(0, BARS);
		if (cats.isValidPage(categoryPage+1)) {
			inv.getInventory().setItem(8, NEXT);
			inv.addConsumer(8, e->{
				open(p,category,categoryPage+1,valuePage);
			});
		} else inv.getInventory().setItem(8, BARS);
		List<C> cates = cats.getPage(categoryPage);
		for (int i = 0; i < CATEGORY_CONTENTS.length && i < cates.size();i ++) {
			int slot = CATEGORY_CONTENTS[i];
			C cate = cates.get(i);
			inv.getInventory().setItem(slot, itemCategory(cate));
			inv.addConsumer(slot, e->{
				open(p,cate,categoryPage,0);
				onSelectCategory(p,e,cate);
			});
		}
		if (category != null) {
			Paginator<V> values = new Paginator<>(new ArrayList<>(m.get(category)),VALUE_CONTENTS.length);
			if (values.isValidPage(valuePage-1)) {
				inv.getInventory().setItem(47, PREV);
				inv.addConsumer(47, e->{
					open(p,category,categoryPage,valuePage-1);
				});
			}
			if (values.isValidPage(valuePage+1)) {
				inv.getInventory().setItem(51, NEXT);
				inv.addConsumer(51, e->{
					open(p,category,categoryPage,valuePage+1);
				});
			}
			List<V> vals = values.getPage(valuePage);
			for (int i = 0; i < VALUE_CONTENTS.length && i < vals.size(); i++) {
				int slot = VALUE_CONTENTS[i];
				V val = vals.get(i);
				inv.getInventory().setItem(slot, itemValue(val));
				inv.addConsumer(slot, e->{
					onSelect((Player)e.getWhoClicked(),e,category,val);
				});
			}	
		}
		p.openInventory(inv.getInventory());
	}
	public abstract void onSelectCategory(Player p,InventoryClickEvent e,C category);
	public abstract ItemStack itemCategory(C category);
	public abstract ItemStack itemValue(V value);
	public abstract void onSelect(Player p,InventoryClickEvent e, C category,V value);
	
}
