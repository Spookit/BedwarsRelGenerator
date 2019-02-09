package thito.bedwarsrelgenerator.containers;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import thito.breadcore.utils.ScoreMap;

public class PlayerStatsContainer {
	
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{[^(\\}|\\{)]+\\}");

	public static final String KILL_ID = "kill";
	public static final String BEDBREAK_ID = "bedbreak";
	public static final String FINALKILL_ID = "finalkill";
	public static final String KILLSTREAK_ID = "killstreak";
	public static final String DEAD_ID = "dead";
	
	public static final String DATA_LASTACTION = "action_before";
	public static final String ACTION_KILL = "kill";
	public static final String ACTION_DEAD = "dead";
	
	private HashMap<String,ScoreMap<String>> data = new HashMap<>();
	private HashMap<String,HashMap<String,String>> other = new HashMap<>();
	
	public String get(String o,String k) {
		HashMap<String,String> str = other.get(o);
		if (str == null) return "";
		return str.getOrDefault(k, "");
	}
	
	public void reset(Player p,String id) {
		if (p == null) return;
		ScoreMap<String> dats = data.get(p.getName());
		if (dats == null) return;
		dats.remove(id);
	}
	
	public void set(String o,String k,String v) {
		HashMap<String,String> str = other.get(o);
		if (str == null) other.put(o, str = new HashMap<>());
		str.put(k, v);
	}
	
	public void clear() {
		data.clear();
	}
	
	public void add(Player p,String id) {
		if (p == null) return;
		ScoreMap<String> dats = data.get(p.getName());
		if (dats == null) data.put(p.getName(),dats=new ScoreMap<>());
		dats.increase(id);
	}
	public String replace(String p,String s) {
		ScoreMap<String> dats = data.get(p);
		if (dats == null) dats = new ScoreMap<>();
		Matcher m = PLACEHOLDER_PATTERN.matcher(s);
		while (m.find()) {
			String string = m.group().substring(2);
			string = string.substring(0,string.length()-1);
			String[] split = string.split("_");
			String placeholder = split[split.length-1];
			System.out.println(string);
			if (!string.startsWith("stats_")) continue;
			s = s.replace("${"+string+"}", dats.get(placeholder)+"");
		}
		return s;
	}
	public String replace(Player p,String s) {
		return replace(p.getName(),s);
	}
}
