package thito.breadcore.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ArraySet<K> extends ArrayList<K> implements Set<K> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArraySet() {
		super();
	}
	public ArraySet(Collection<? extends K> col) { 
		super(col);
	}
	public ArraySet(int capacity) {
		super(capacity);
	}
}
