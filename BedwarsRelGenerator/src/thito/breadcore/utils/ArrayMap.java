package thito.breadcore.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ArrayMap<K,V> implements Map<K,V> {

	private final ArrayList<ArrayEntry> entries;
	public ArrayMap() {
		entries = new ArrayList<>();
	}
	public ArrayMap(int capacity) {
		entries = new ArrayList<>(capacity);
	}
	
	public ArrayMap(Map<? extends K,? extends V> map) {
		entries = new ArrayList<>();
		putAll(map);
	}
	
	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return indexOfKey(key) >= 0;
	}
	
	public boolean contains(K key, V value) {
		for (ArrayEntry e : entries) {
			if ((e.getKey() == null && key == null) || (e.getKey().equals(key))) {
				if ((e.getValue() == null && value == null) || (e.getValue().equals(value))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return indexOfValue(value) >= 0;
	}

	@Override
	public V get(Object key) {
		int index = indexOfKey(key);
		if (index >= 0) {
			return entries.get(index).getValue();
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		V get = get(key);
		entries.add(new ArrayEntry(key,value));
		return get;
	}

	@Override
	public V remove(Object key) {
		int index = indexOfKey(key);
		if (index >= 0) {
			return entries.remove(index).getValue();
		}
		return null;
	}
	
	public int indexOfKey(Object key) {
		return new ArrayList<>(keySet()).indexOf(key);
	}
	
	public int lastIndexOfKey(Object key) {
		return new ArrayList<>(keySet()).lastIndexOf(key);
	}
	
	public int indexOfValue(Object value) {
		return new ArrayList<>(values()).indexOf(value);
	}
	
	public int lastIndexOfValue(Object value) {
		return new ArrayList<>(values()).lastIndexOf(value);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K,? extends V> e : m.entrySet()) {
			put(e.getKey(),e.getValue());
		}
	}

	@Override
	public void clear() {
		entries.clear();
	}

	@Override
	public Set<K> keySet() {
		Set<K> keys = new ArraySet<>();
		entries.forEach(a->{
			keys.add(a.getKey());
		});
		return keys;
	}

	@Override
	public Collection<V> values() {
		ArrayList<V> values = new ArrayList<>();
		entries.forEach(a->{
			values.add(a.getValue());
		});
		return values;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new ArraySet<>(entries);
	}
	class ArrayEntry implements Entry<K,V> {

		private final K key;
		private V value;
		public ArrayEntry(K key,V val) {
			this.key = key;
			value = val;
		}
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
		
	}
}
