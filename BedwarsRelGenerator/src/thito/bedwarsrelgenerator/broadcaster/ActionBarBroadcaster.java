package thito.bedwarsrelgenerator.broadcaster;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarBroadcaster implements Broadcaster {

	@Override
	public void send(Player p, String msg) {
		try {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
		} catch (Throwable t) {
		}
	}

	@Override
	public void unsubscribe(Player p) {
	}

	@Override
	public String getName() {
		return "ActionBar";
	}

}
