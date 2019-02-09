package thito.bedwarsrelgenerator.broadcaster;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import thito.bedwarsrelgenerator.BWG;
import thito.breadcore.spigot.nms.NMSEntityPlayer;
import thito.breadcore.spigot.nms.NMSWorld;
import thito.breadcore.utils.ObjectWrapper;
import thito.breadcore.utils.Util;

public class BossBarBroadcasterPrimitive implements Listener,Broadcaster {

	private final Map<String,Bar> main = new HashMap<>();
	private final Map<String,Bar> secondary = new HashMap<>();
	
	@EventHandler
	public void a(PlayerMoveEvent e) {
		new BukkitRunnable() {
			public void run() {
				Bar m = main.get(e.getPlayer().getName());
				Bar s = secondary.get(e.getPlayer().getName());
				if (m != null && m.pending) {
					m.pending = true;
					m.send();
					m.pending = false;
				}
				if (s != null && s.pending) {
					s.pending = true;
					s.send();
					s.pending = false;
				}
			}
		}.runTaskAsynchronously(BWG.get());
	}
	
	@EventHandler 
	public void join(PlayerJoinEvent e) {
		new BukkitRunnable() {
			public void run() {
				Bar m = main.get(e.getPlayer().getName());
				Bar s = secondary.get(e.getPlayer().getName());
				if (m != null && m.pending) {
					m.pending = true;
					m.setPlayer(e.getPlayer());
					m.send();
					m.pending = false;
				}
				if (s != null && s.pending) {
					s.pending = true;
					s.setPlayer(e.getPlayer());
					s.send();
					s.pending = false;
				}
			}
		}.runTaskAsynchronously(BWG.get());
	}
	
	public static class Bar {
		private static Class<?> clazz = Util.nms("EntityEnderDragon");
		private static Class<?> spawnPacket = Util.nms("PacketPlayOutSpawnEntityLiving");
		private static Class<?> movePacket = Util.nms("PacketPlayOutEntityTeleport");
		private static Class<?> metadataPacket = Util.nms("PacketPlayOutEntityMetadata");
		private static Class<?> destroyPacket = Util.nms("PacketPlayOutEntityDestroy");
		private ObjectWrapper entity;
		private Player p;
		private NMSEntityPlayer player;
		private boolean spawned = false;
		boolean pending = false;
		private boolean visible = false;
		public Bar(Player p) {
			try {
				this.p = p;
				player = new NMSEntityPlayer(p);
				Constructor<?> cons = clazz.getDeclaredConstructor(Util.nms("World"));
				entity = new ObjectWrapper();
				entity.setInstance(cons.newInstance(new NMSWorld(p.getWorld()).getWrapped()));
				Location loc = p.getLocation();
				entity.invokeMethod("setLocation", loc.getX() - 30D,loc.getY() - 100D, loc.getZ(),0F,0F);
				entity.invokeMethod("setInvisible", true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public Bar setTitle(String title) {
			entity.invokeMethod("setCustomName", title);
			if (visible) {
				if (spawned) {
					spawned = false;
					player.invoke("arg0.playerConnection.sendPacket(new arg1(arg2,arg2.getDataWatcher(),true))", metadataPacket,entity.getInstance());
				}
			}
			return this;
		}
		public Player getPlayer() {
			return p;
		}
		public void setPlayer(Player p) {
			if (p.equals(this.p)) return;
			this.p = p;
			player = new NMSEntityPlayer(p);
		}
		public void setVisible(boolean v) {
			visible = v;
			send();
		}
		void send() {
			if (!visible) {
				if (spawned) {
					spawned = false;
					player.invoke("arg0.playerConnection.sendPacket(new arg1(arg2.getId()))", destroyPacket,entity.getInstance());
				}
				return;
			}
			if (!spawned) {
				spawned = true;
				ObjectWrapper packet = new ObjectWrapper(spawnPacket, entity.getInstance());
				player.invoke("arg0.playerConnection.sendPacket(arg1)", packet.getInstance());
				return;
			}
			Location loc = p.getLocation();
			entity.invokeMethod("setLocation", loc.getX() - 30D,loc.getY() - 100D, loc.getZ(),0F,0F);
			ObjectWrapper packet = new ObjectWrapper(movePacket,entity.getInstance());
			player.invoke("arg0.playerConnection.sendPacket(arg1)", packet.getInstance());
		}
	}

	@Override
	public void send(Player p, String msg) {
		Bar bar = main.get(p.getName());
		if (bar == null) {
			main.put(p.getName(), bar = new Bar(p));
			bar.setTitle(msg);
			bar.setVisible(msg != null);
			return;
		}
		bar.setTitle(msg);
		bar.setVisible(msg != null);
	}
	
	public void send(Player p,String msg,String submsg) {
		Bar bar = main.get(p.getName());
		if (bar == null) {
			main.put(p.getName(), bar = new Bar(p));
			bar.setTitle(msg);
			bar.setVisible(msg != null);
			return;
		}
		bar.setTitle(msg);
		bar.setVisible(msg != null);
		Bar subbar = secondary.get(p.getName());
		if (subbar == null) {
			secondary.put(p.getName(), subbar = new Bar(p));
			subbar.setTitle(submsg);
			subbar.setVisible(submsg != null);
			return;
		}
		subbar.setVisible(submsg != null);
		subbar.setTitle(submsg);
	}

	@Override
	public void unsubscribe(Player p) {
		Bar bar = main.get(p.getName());
		if (bar != null) bar.setVisible(false);
		Bar sub = secondary.get(p.getName());
		if (sub != null) sub.setVisible(false);
	}

	@Override
	public String getName() {
		return "BossBar";
	}
}
