package thito.bedwarsrelgenerator.broadcaster;

import org.bukkit.entity.Player;

import thito.breadcore.spigot.packets.SimplePacketWrapper;

public class TitleBroadcaster implements Broadcaster {

	@Override
	public void send(Player p, String msg) {
		SimplePacketWrapper.sendTitle(p, msg, "", 0, 70, 40);
	}
	
	public void send(Player p,String msg,String submsg) {
		SimplePacketWrapper.sendTitle(p, msg, submsg, 0, 70, 40);
	}

	@Override
	public void unsubscribe(Player p) {
		p.resetTitle();
	}

	@Override
	public String getName() {
		return "Title";
	}

}
