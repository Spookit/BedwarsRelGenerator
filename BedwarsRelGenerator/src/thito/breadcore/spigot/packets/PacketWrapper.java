package thito.breadcore.spigot.packets;

import org.bukkit.entity.Player;

import thito.breadcore.utils.ScriptUtil;

public interface PacketWrapper {

	public Object getPacketInstance();
	public default void send(Player p) {
		ScriptUtil.run("arg0.getHandle().playerConnection.sendPacket(arg1)", p,getPacketInstance());
	}
	
}
