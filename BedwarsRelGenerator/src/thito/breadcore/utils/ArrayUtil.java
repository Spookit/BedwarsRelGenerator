package thito.breadcore.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Predicate;

public class ArrayUtil {

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> T[] combine(T[]... arrays) {
		if (arrays.length == 0) {
			Object o = Array.newInstance(arrays.getClass().getComponentType(), 0);
			return (T[])o;
		}
		T[] first = arrays[0];
		int newLength = 0;
		for (T[] t : arrays) {
			newLength+=t.length;
		}
		T[] newArray = Arrays.copyOf(first, newLength);
		int index = first.length;
		for (int i = 1; i < arrays.length; i++) {
			T[] oldArray = arrays[i];
			for (int i2 = 0; i2 < oldArray.length; i2++) {
				newArray[index++] = oldArray[i2];
			}
		}
		return newArray;
	}
	
	public static <T,E extends T>void a(T[] a) {
		
	}
	
	@SafeVarargs
	public static <T> T get(Predicate<T> acceptor,T... array) {
		for (T t : array) if (acceptor.test(t)) return t;
		return null;
	}
	
}
