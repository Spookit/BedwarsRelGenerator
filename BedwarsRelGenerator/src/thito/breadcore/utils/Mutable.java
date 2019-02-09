package thito.breadcore.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Mutable<T> implements Supplier<T>, Consumer<T> {

	private T type;

	public Mutable() {
	}

	public Mutable(T t) {
		type = t;
	}

	@Override
	public void accept(T t) {
		type = t;
	}

	@Override
	public T get() {
		return type;
	}
}
