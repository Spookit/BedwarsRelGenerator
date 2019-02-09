package thito.bedwarsrelgenerator.kits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

public class Kit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8222398377779785205L;
	private String name;
	private String permission;
	private Set<String> allowedArenas = new HashSet<>();
	private boolean allowLoadouts;
	private List<ItemStack> items = new ArrayList<>();
	public Kit(String name) {
		this.name = name;
	}
	
	public boolean isAllowLoadouts() {
		return allowLoadouts;
	}
	
	public void setAllowLoadouts(boolean o) {
		allowLoadouts = o;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public void setPermission(@Nullable String s) {
		permission = s;
	}
	
	public boolean hasPermission(Permissible p) {
		return getPermission() == null || p.hasPermission(getPermission());
	}
	
	public Set<String> getAllowedArenas() {
		return allowedArenas;
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Kit) {
			return ((Kit)o).getName().equals(getName());
		}
		return o == this;
	}
	
}
