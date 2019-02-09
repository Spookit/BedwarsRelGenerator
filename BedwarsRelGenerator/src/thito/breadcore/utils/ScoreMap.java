package thito.breadcore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class ScoreMap<K> extends HashMap<K,Integer> implements Iterable<Entry<K,Integer>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void increase(K key) {
		Integer n = remove(key);
		put(key,n+1);
	}
	
	public void add(K key,int increment) {
		put(key,remove(key)+increment);
	}
	public void subtract(K key,int decrement) {
		put(key,remove(key)-decrement);
	}
	
	public void decrease(K key) {
		Integer n = remove(key);
		put(key,n-1);
	}
	public Integer get(Object key) {
		Integer get = super.get(key);
		return get == null ? 0 : get;
	}
	public Integer remove(Object key) {
		Integer get = super.remove(key);
		return get == null ? 0 : get;
	}
	public Integer put(K key,Integer value) {
		if (value == null) value = 0;
		return super.put(key, value);
	}
	public Iterator<Entry<K,Integer>> iterator() {
		return new ArrayList<>(entrySet()).iterator();
	}
}
