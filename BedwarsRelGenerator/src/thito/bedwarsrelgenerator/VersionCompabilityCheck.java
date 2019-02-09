package thito.bedwarsrelgenerator;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import thito.breadcore.utils.Util.PacketVersion;

public class VersionCompabilityCheck {

	private final PacketVersion[] vers;
	public VersionCompabilityCheck(PacketVersion... versions) {
		vers = versions;
	}
	
	public boolean isCompatible() {
		PacketVersion current = thito.breadcore.utils.Util.getPacketVersion();
		for (PacketVersion p : vers) {
			if (p == current) return true;
		}
		return false;
	}
	
	public String toString() {
		return Util.toString(vers);
	}
	
	public void check(Runnable disabler) {
		if (!isCompatible()) {
			for (String s : new String[] {
					
					"/ *************************************************",
					" *   BEDWARSRELGENERATOR (ERROR)",
					" * ----------------------------------------------",
					" * This plugin doesn't support this server version!",
					" * (Only support "+this+")",
					" * Please note that, the author won't respond to",
					" * your version support request! Just wait until",
					" * the next update (version support update) available",
					" ************************************************* /"
			}) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+s);
			}
			disabler.run();
		}
	}
	
}
