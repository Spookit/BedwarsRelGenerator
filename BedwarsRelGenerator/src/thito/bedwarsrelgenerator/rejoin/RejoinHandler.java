package thito.bedwarsrelgenerator.rejoin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import thito.bedwarsrelgenerator.BWG;
import thito.breadcore.utils.Chat;
import thito.breadcore.utils.StrUtil;

@Deprecated
public class RejoinHandler implements CommandExecutor,Listener {

	/***
rejoin:
  enabled: true
  center-message: true
  message:
  - "&7&m-----------------------------------------------------"
  - "&a&lBEDWARS GAME REJOIN"
  - "&eYou have been disconnected when game ${game} is running."
  - "&eYou can join back the game with command &b/rejoin"
  - "&7&m-----------------------------------------------------"
  no-game: "&cGame unavailable/has been ended!"
  # placeholder 
  # name - The player name
  # team - The player team
  # teamname - The player teamname
  # teamprefix - The player teamprefix
  # teamcolor - The player team color
  announcement: "&a&lREJOIN &8> &e${name} has been joined the game"
	 */
	private final Map<String,RejoinData> datas = new HashMap<>();
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return false;
	}
	
	@EventHandler
	public void end(BedwarsGameEndEvent e) {
		for (Entry<String,RejoinData> rd : new HashSet<>(datas.entrySet())) {
			if (rd.getValue().getGame() == e.getGame()) {
				datas.remove(rd.getKey());
			}
		}
	}
	
	@EventHandler
	public void leave(BedwarsPlayerLeaveEvent e) {
		RejoinData data = new RejoinData(e.getPlayer().getName(), e.getTeam(), e.getGame());
		datas.put(e.getPlayer().getName(), data);
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		if (datas.containsKey(e.getPlayer().getName())) {
			for (String s : getMessage()) {
				if (isCenterMessage()) {
					for (String c : Chat.centerMultilines(s)) {
						e.getPlayer().sendMessage(c);
					}
				} else e.getPlayer().sendMessage(StrUtil.color(s));
			}
		}
	}
	
	public boolean isEnabled() {
		return BWG.get().getConfig().getBoolean("rejoin.enabled");
	}
	
	public boolean isCenterMessage() {
		return BWG.get().getConfig().getBoolean("rejoin.center-message");
	}
	
	public List<String> getMessage() {
		return BWG.get().getConfig().getStringList("rejoin.message");
	}
	
	public String noGame() {
		return BWG.get().getConfig().getString("rejoin.no-game");
	}
	
	public RejoinData get(Player p) {
		return datas.get(p.getName());
	}

}
