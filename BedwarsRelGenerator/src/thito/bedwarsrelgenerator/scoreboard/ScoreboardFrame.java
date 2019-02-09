package thito.bedwarsrelgenerator.scoreboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class ScoreboardFrame {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{[^(\\}|\\{)]+\\}");
	public static final Random RANDOM = new Random();
	public static String hideName(String s) {
		String builder = new String();
		for (char c : s.toCharArray()) {
			builder+=ChatColor.COLOR_CHAR+""+c;
		}
		return builder;
	}
	public static String randomName() {
		String builder = new String();
		for (int i = 0; i < 6;i ++) {
			builder+=ChatColor.COLOR_CHAR+""+RANDOM.nextInt(10);
		}
		return builder;
	}
	private final List<String> l;
	private final String t;
	public ScoreboardFrame(String title,List<String> lines) {
		l = lines;
		t = title;
	}
	public String getTitle() {
		return t;
	}
	public ArrayList<String> lines(Map<String,String> map,Map<String,Iterator<String>> remains) {
		ArrayList<String> form = new ArrayList<>();
		for (String s : l) {
			String xx = replaceAll(s,map,remains);
			if (xx == null) continue;
			form.add(xx);
		}
		return form;
	}
	
	public static String replaceAll(String s,Map<String,String> maps,Map<String,Iterator<String>> remains) {
		Matcher match = PLACEHOLDER_PATTERN.matcher(s);
		while (match.find()) {
			String found = match.group();
			String placeholder = found;
			found = found.substring(2, found.length()-1);
			if (maps.containsKey(found)) {
				s = s.replace(placeholder, maps.get(found));
			} else if (remains != null && remains.containsKey(found)) {
				Iterator<String> sx = remains.get(found);
				if (sx.hasNext()) {
					s = s.replace(placeholder, sx.next());
				} else {
					return null;
				}
			}
		}
		if (s != null) {
			s = ChatColor.translateAlternateColorCodes('&', s);
		}
		return s;
	}
}
