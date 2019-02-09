package thito.bedwarsrelgenerator;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import thito.bedwarsrelgenerator.broadcaster.ActionBarBroadcaster;
import thito.bedwarsrelgenerator.broadcaster.BossBarBroadcaster;
import thito.bedwarsrelgenerator.broadcaster.BossBarBroadcasterPrimitive;
import thito.bedwarsrelgenerator.broadcaster.Broadcaster;
import thito.bedwarsrelgenerator.broadcaster.TitleBroadcaster;
import thito.bedwarsrelgenerator.containers.TrackerContainer;
import thito.bedwarsrelgenerator.handlers.ArenaHandler;
import thito.bedwarsrelgenerator.kits.KitManager;
import thito.bedwarsrelgenerator.scoreboard.ScoreboardPanel;
import thito.bedwarsrelgenerator.tracker.TrackingGUI;
import thito.bedwarsrelgenerator.trails.ProjectileTrail;
import thito.bedwarsrelgenerator.upgrades.ArmorUpgrades;
import thito.bedwarsrelgenerator.upgrades.BeaconUpgrades;
import thito.bedwarsrelgenerator.upgrades.BedTrapUpgrades;
import thito.bedwarsrelgenerator.upgrades.SwordUpgrades;
import thito.bedwarsrelgenerator.upgrades.ToolUpgrades;
import thito.breadcore.spigot.hologram.Hologram;
import thito.breadcore.spigot.inventory.PluginInventory;
import thito.breadcore.utils.Util.PacketVersion;

public final class BWG extends JavaPlugin {

	public static BWG get() {
		return instance;
	}
	private static BWG instance;
	private static boolean useCitizens = true;
	public static Set<ArenaHandler> HANDLERS = new HashSet<>();
	private static final String VERSION_TYPE = "RELEASE";
	private static final KitManager kits = new KitManager();
	public static KitManager getKits() {
		return kits;
	}
	public static boolean useCitizens() {
		return useCitizens;
	}
	public static ArenaHandler get(Game g) {
		for (ArenaHandler h : HANDLERS) {
			if (h.getGame() == g) return h;
		}
		return null;
	}
	{
		instance = this;
	}
	private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&aBWG&8] &7");
	public void reloadConfig() {
		new HashSet<>(HANDLERS).forEach(a->{
			a.arenaStop();
		});
		HANDLERS.clear();
		if (panel != null) {
			panel.stop();
		}
		if (!new File(getDataFolder(),"config.yml").exists()) {
			saveDefaultConfig();
		}
		super.reloadConfig();
		trackerContainer = new TrackerContainer(getConfig().getConfigurationSection("tracker"));
		String oldVersion = getConfig().getString("config-version","legacy");
		if (!getConfig().isSet("config-version") || !getConfig().getString("config-version").equalsIgnoreCase(getDescription().getVersion()+"_"+VERSION_TYPE.toLowerCase())) {
			File config = new File(getDataFolder(),"config.yml");
			if (config.exists()) {
				config.renameTo(new File(getDataFolder(),"config-"+oldVersion+".yml"));
			}
			this.saveResource("config.yml", true);
			log("Configuration File has been resetted due to outdated version!");
			log("The old configuration file has been renamed to config-"+oldVersion+".yml");
		}
		panel = new ScoreboardPanel(getConfig().getConfigurationSection("scoreboard-panel"));
		panel.start();
		kits.load();
		for (Game g : BedwarsRel.getInstance().getGameManager().getGames()) {
			if (getConfig().getStringList("blacklisted-arenas").contains(g.getName())) continue;
			HANDLERS.add(new ArenaHandler(g, getConfig().getConfigurationSection("arena-handler")));
		}
		Upgrades.UPGRADES.removeIf(a->{
			try {
				JavaPlugin.getProvidingPlugin(a.getClass()).equals(this);
			} catch (Exception e) {
			}
			return a instanceof ArmorUpgrades || a instanceof ToolUpgrades
					|| a instanceof SwordUpgrades || a instanceof BeaconUpgrades
					|| a instanceof BedTrapUpgrades;
		});
		Upgrades.UPGRADES.addAll(Arrays.asList(
				new ArmorUpgrades(getConfig().getConfigurationSection("upgrades.armor")),
				new ToolUpgrades(getConfig().getConfigurationSection("upgrades.tool")),
				new SwordUpgrades(getConfig().getConfigurationSection("upgrades.sword")),
				new BeaconUpgrades(getConfig().getConfigurationSection("upgrades.beacon")),
				new BedTrapUpgrades(getConfig().getConfigurationSection("upgrades.bed-trap"))
				));
		startHandlers();
	}
	private void startHandlers() {
		new HashSet<>(HANDLERS).forEach(a->{
			if (a.getGame().getState() == GameState.RUNNING) a.arenaStart();
		});
	}
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[]args) {
		if (sender.hasPermission("bedwarsrelgenerator.admin")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					try {
						reloadConfig();
						sender.sendMessage(prefix+"Configuration reloaded!");
					} catch (Exception e) {
						e.printStackTrace();
						sender.sendMessage(prefix+"Failed to reload configuration! "+e);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("info")) {
					sender.sendMessage(prefix+"Injected Bedwars Game: "+Util.toString(HANDLERS));
					sender.sendMessage(prefix+"If your arena doesn't listed here. Please do /bwg reload");
					return true;
				}
			}
			sender.sendMessage(prefix+"BedwarsRelGenerator plugin by BlueObsidian/Thito Yalasatria Sunarya. Usage: /bwg <reload|info>");
			return true;
		}
		sender.sendMessage(prefix+"You don't have permission to do this");
		return true;
	}
	public void log(String s) {
		Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.RESET+s);
	}
	private BWGListener listener;
	private ScoreboardPanel panel;
	private TrackerContainer trackerContainer;
	private TrackingGUI trackingGUI;
	private VersionCompabilityCheck checker = new VersionCompabilityCheck(
			/*
			 * Supported versions
			 */
			PacketVersion.V1_8_R1,
			PacketVersion.V1_8_R2,
			PacketVersion.V1_8_R3,
			PacketVersion.V1_9_R1,
			PacketVersion.V1_9_R2,
			PacketVersion.V1_10_R1,
			PacketVersion.V1_11_R1,
			PacketVersion.V1_12_R1,
			PacketVersion.V1_13_R1,
			PacketVersion.V1_13_R2
	);
	public TrackerContainer getTrackerContainer() {
		if (trackerContainer == null) {
			trackerContainer = new TrackerContainer(getConfig().getConfigurationSection("tracker"));
		}
		return trackerContainer;
	}
	public static Broadcaster FALLBACK_BROADCASTER;
	public void onEnable() {
		checker.check(()->{
			setEnabled(false);
		});
		if (!isEnabled()) return;
		if (!getServer().getPluginManager().isPluginEnabled("BedwarsRel")) {
			log(ChatColor.RED+"Plugin 'BedwarsRel' is not installed on your server! Please install it before using this plugin!");
			log("Disabling plugin...");
			setEnabled(false);
			return;
		}
		if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
			log(ChatColor.YELLOW+"Plugin 'Citizens' is not installed on your server! Please install it before using this plugin! (IGNORED)");
			log(ChatColor.YELLOW+"Using fallback Team Upgrades handler!");
			useCitizens = false;
			getServer().getPluginManager().registerEvents(new FallbackNPCListener(), this);
		} else {
			getServer().getPluginManager().registerEvents(new CitizensListener(), this);
		}
		new Metrics(this);
		Broadcaster.SERVICE.addAll(Arrays.asList(FALLBACK_BROADCASTER = (thito.breadcore.utils.Util.getVersionNumber() <= 8 ? new BossBarBroadcasterPrimitive() : new BossBarBroadcaster()),
				new TitleBroadcaster()));
		if (thito.breadcore.utils.Util.getVersionNumber() >= 11) {
			Broadcaster.SERVICE.add(FALLBACK_BROADCASTER = new ActionBarBroadcaster());
		}
		reloadConfig();
		trackingGUI = new TrackingGUI();
		getServer().getPluginManager().registerEvents(trackingGUI, this);
		getServer().getPluginManager().registerEvents(listener = new BWGListener(), this);
		getServer().getPluginManager().registerEvents(new DeathPlayerBag(), this);
		getCommand("bedwarsselector").setExecutor(new BedwarsSelector());
		try {
			log("Cleaning up server crash entities...");
			CleanUpCrash.cleanUp();
			log("All undespawned entities during server crash has been cleaned!");
		} catch (Exception e) {
			log(ChatColor.RED+"Failed to remove entities");
			e.printStackTrace();
		}
		Hologram.registerListener(this);
		PluginInventory.registerListener(this);
		new BukkitRunnable() {
			public void run() {
				for (Upgrades u : Upgrades.UPGRADES) {
					u.tick();
				}
			}
		}.runTaskTimer(this, 10L, 10L);
		ProjectileTrail trails = new ProjectileTrail();
		getServer().getPluginManager().registerEvents(trails,this);
		getCommand("trail").setExecutor(trails);
		getCommand("bwgkit").setExecutor(kits);
		trails.runTaskTimerAsynchronously(this, 20L*240, 20L*240);
	}
	public void onDisable() {
		if (listener != null) listener.injectBackTheOldOne();
		for (ArenaHandler h : HANDLERS) {
			h.arenaStop();
		}
	}
}
