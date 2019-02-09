package thito.breadcore.utils;

public final class EnumConstant {

	private final String n;
	private final Class<?> t;
	public EnumConstant(String name,Class<?> type) {
		n = name;
		t = type;
		try {
			get();
		} catch (Exception e) {
		}
	}
	public String name() {
		return n;
	}
	public Class<?> getDeclaringClass() {
		return t;
	}
	public String toString() {
		return name();
	}
	private Enum<?> cache;
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> Enum<T> get() {
		if (cache != null) return (Enum<T>)cache;
		cache = Enum.valueOf((Class<T>)t, n);
		return (Enum<T>)cache;
	}
}
