package thito.breadcore.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class JsonParser {
	// final Object root = new JohnParser();

	public static class Itself {
		Obj parent;

		Itself(Obj p) {
			parent = p;
		}

		@Override
		public String toString() {
			return "this";
		}
	}

	public static class Obj implements Iterable<Obj>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		static String handle(Object x) {
			if (x instanceof Itself) {
				return toJSON(x);
			}
			if (x instanceof String) {
				return "\"" + x.toString().replace("\"", "\\\"").replace("'", "\\'") + "\"";
			}
			return toJSON(x);
		}

		static String toJSON(Object o) {
			if (o == null) {
				return "";
			}
			String b = new String();
			if (o instanceof Itself) {
				return o.toString();
			}
			if (o instanceof Map) {
				for (final Entry<?, ?> x : ((Map<?, ?>) o).entrySet()) {
					if (x.getValue() != null) {
						final String to = handle(x.getValue());
						if (!to.isEmpty()) {
							b += ", \"" + x.getKey() + "\":" + to;
						}
					}
				}
				if (b.startsWith(", ")) {
					b = b.substring(2);
				}
				b = "{" + b + "}";
			} else if (o instanceof List) {
				for (final Object x : (List<?>) o) {
					if (x != null) {
						final String to = handle(x);
						if (!to.isEmpty()) {
							b += ", " + to;
						}
					}
				}
				if (b.startsWith(", ")) {
					b = b.substring(2);
				}
				b = "[" + b + "]";
			} else if (o.getClass().getName().startsWith("java.lang.")) {
				b += o.toString();
			} else {
				try {
					for (final Field f : o.getClass().getDeclaredFields()) {
						f.setAccessible(true);
						if (Modifier.isStatic(f.getModifiers())) {
							continue;
						}
						final Object val = f.get(o);
						final Object def = defaultValue(f.getType());
						if (def != null && def.equals(val)) {
							continue;
						}
						if (val != null) {
							if (val != o) {
								b += ", \"" + f.getName() + "\":" + handle(val);
							} else {
								b += ", \"" + f.getName() + "\":this";
							}
						}
					}
					if (b.startsWith(", ")) {
						b = b.substring(2);
					}
					b = "{" + b + "}";
				} catch (final Throwable t) {
				}
			}
			// b = new JSONParser().fromJson(b).toString();
			return b;
		}

		Object o;
		Object key;
		String path;
		Obj parent;

		Obj(Object o, Object k, String path, Obj p) {
			this.o = o;
			key = k;
			if (k == null) {
				key = new Object() {
					@Override
					public int hashCode() {
						return -1;
					}

					@Override
					public String toString() {
						return "undefined";
					}
				};
			}
			this.path = path;
			parent = p;
			if (o instanceof Itself) {
				((Itself) o).parent = parent;
			}
		}

		public Obj add(Object value) {
			return put(null, value);
		}

		public void clear() {
			if (o instanceof List) {
				((List<?>) o).clear();
			} else if (o instanceof Map) {
				((Map<?, ?>) o).clear();
			} else if (o instanceof String) {
				o = new String();
			} else {
				o = null;
			}
		}

		Map<Object, Object> convertListToMap() {
			final Map<Object, Object> x = new HashMap<>();
			List<?> y;
			if (o instanceof List) {
				y = (List<?>) o;
			} else if (o instanceof String) {
				final ArrayList<Character> c = new ArrayList<>();
				for (final char xx : ((String) o).toCharArray()) {
					c.add(xx);
				}
				y = c;
			} else {
				return x;
			}
			for (int i = 0; i < y.size(); i++) {
				x.put(i, y.get(i));
			}
			o = x;
			return x;
		}

		@SuppressWarnings("unchecked")
		@Deprecated
		public <T> T createNewInstance(Class<T> x) {
			try {
				if (x.getName().startsWith("java.")) {
					if (x.isInstance(o)) {
						return (T) o;
					}
				}
				if (!(o instanceof Map)) {
					throw new Error("unsupported json type");
				}
				final T t = (T) JsonParser.createNewInstance(x);
				final Map<?, ?> z = (Map<?, ?>) o;
				for (final Field f : t.getClass().getDeclaredFields()) {
					f.setAccessible(true);
					final Object o = z.get(f.getName());
					if (Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					removeFinal(f);
					if (o instanceof Itself) {
						f.set(t, t);
					} else {
						f.set(o, t);
					}
				}
				return t;
			} catch (final Exception e) {
				throw new Error(e);
			}
		}

		@Override
		public Iterator<Obj> iterator() {
			final ArrayList<Obj> objs = new ArrayList<>();
			if (o instanceof List) {
				for (int i = 0; i < ((List<?>) o).size(); i++) {
					objs.add(next(i));
				}
			} else if (o instanceof Map) {
				for (final Entry<?, ?> x : ((Map<?, ?>) o).entrySet()) {
					objs.add(next(x.getKey()));
				}
			} else if (o instanceof String) {
				final char[] x = ((String) o).toCharArray();
				for (int i = 0; i < x.length; i++) {
					objs.add(next(i));
				}
			}
			return new Iterator<JsonParser.Obj>() {

				@Override
				public boolean hasNext() {
					return !objs.isEmpty();
				}

				@Override
				public Obj next() {
					final Obj o = objs.get(0);
					objs.remove(0);
					return o;
				}

				@Override
				public String toString() {
					return objs.toString();
				}
			};
		}

		public Object key() {
			return key;
		}

		public Obj next(Object s) {
			if (o instanceof Itself) {
				return parent.next(s);
			}
			if (s instanceof Integer) {
				if (o instanceof List) {
					return new Obj(((List<?>) o).get((Integer) s), s, key.hashCode() == -1 ? s + "" : key + "." + s,
							this);
				} else if (o instanceof String) {
					try {
						final Integer x = (Integer) s;
						final String c = (String) o;
						return new Obj(c.charAt(x), s, key.hashCode() == -1 ? s + "" : key + "." + s, this);
					} catch (final IllegalArgumentException t) {
					}
				}
			} else {
				s = s.toString();
				if (((String) s).contains(".")) {
					Obj last = this;
					for (final String x : s.toString().split("\\.")) {
						last = last.next(x);
					}
					return last;
				}
				if (o instanceof Map) {
					return new Obj(((Map<?, ?>) o).get(s), s, key.hashCode() == -1 ? s + "" : key + "." + s, this);
				} else if (o instanceof List) {
					try {
						return new Obj(((List<?>) o).get(Integer.parseInt(s.toString())), s,
								key.hashCode() == -1 ? s + "" : key + "." + s, this);
					} catch (final IllegalArgumentException t) {
					}
				} else if (o instanceof String) {
					try {
						final String x = (String) s;
						final String c = (String) o;
						final String[] a = x.split("\\,");
						String builder = new String();
						for (final String z : a) {
							final String[] y = z.split("-", 2);
							if (y.length == 2) {
								builder += c.substring(Integer.parseInt(y[0]), Integer.parseInt(y[1]));
							} else {
								builder += c.charAt(Integer.parseInt(z));
							}
						}
						return new Obj(builder, s, key.hashCode() == -1 ? s + "" : key + "." + s, this);
					} catch (final IllegalArgumentException t) {
					}
				}
			}
			return this;
		}

		public String path() {
			return path;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Obj put(Object key, Object value) {
			if (key == null) {
				if (o instanceof List) {
					((List) o).add(value);
				} else if (o instanceof String) {
					String x = (String) o;
					x += value;
					o = x;
				}
				return this;
			}
			if (key instanceof Integer) {
				final Integer i = (Integer) key;
				if (o instanceof Map) {
					((Map) o).put(key, value);
				} else if (o instanceof List) {
					if (i < 0) {
						((List) o).add(value);
					} else {
						((List) o).add(i, value);
					}
				} else if (o instanceof String) {
					o = ((String) o).substring(0, i) + value
							+ (((String) o).length() + 1 > i ? ((String) o).substring(i + 1) : "");
				}
			} else {
				key = key.toString();
				final String x = (String) key;
				if (x.contains(".")) {
					final String removeLast = x.substring(0, x.lastIndexOf("."));
					next(removeLast).put(x.substring(x.lastIndexOf(".") + 1), value);
					return this;
				}
				if (o instanceof Map) {
					((Map) o).put(key, value);
				} else if (o instanceof List) {
					try {
						final Integer index = Integer.valueOf(key.toString());
						if (index < 0) {
							((List) o).add(value);
						} else {
							((List) o).add(index, value);
						}
					} catch (final IllegalArgumentException t) {
						final Map<Object, Object> ax = convertListToMap();
						ax.put(key, value);
					}
				} else if (o instanceof String) {
					try {
						final Integer index = Integer.valueOf(key.toString());
						o = ((String) o).substring(0, index) + value
								+ (((String) o).length() > index ? ((String) o).substring(index) : "");
					} catch (final IllegalArgumentException t) {
						final Map<Object, Object> ax = convertListToMap();
						ax.put(key, value);
					}
				}
			}
			return this;
		}

		public Obj remove(Object key) {
			if (o instanceof List) {
				if (key instanceof Integer) {
					((List<?>) o).remove((int) key);
				} else {
					try {
						((List<?>) o).remove(Integer.parseInt(key + ""));
					} catch (final Throwable t) {
						((List<?>) o).remove(key);
					}
				}
				return this;
			} else if (o instanceof String) {
				if (key instanceof Integer) {
					o = ((String) o).substring((Integer) key);
					if (((String) o).length() + 1 > (Integer) key) {
						o += ((String) o).substring((Integer) o + 1);
					}
				} else {
					o = ((String) o).replace(key + "", "");
				}
				return this;
			}
			put(key, null);
			return this;
		}

		public String toJSON() {
			return toJSON(o);
		}

		@Override
		public String toString() {
			return o != null ? toJSON() : "undefined";
		}

		public Object value() {
			if (o instanceof Itself) {
				return parent.o;
			}
			return o;
		}
	}

	static class Test {
		String nothing;
		int ab;
		boolean something;
		double testing;
		final Test samething = this;
	}

	public static Object createNewInstance(Class<?> x) {
		try {
			final Class<?> c = Class.forName("sun.misc.Unsafe");
			final Field f = c.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			final Object un = f.get(null);
			final Method me = un.getClass().getMethod("allocateInstance", Class.class);
			return me.invoke(un, x);
		} catch (final Throwable t) {
			throw new Error("can't handle new instantion");
		}
	}

	public static Object defaultValue(Class<?> x) {
		try {
			x = (Class<?>) x.getField("TYPE").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
		}
		return Array.get(Array.newInstance(x, 1), 0);
	}

	public static void main(String[] args) {
		System.out.println(defaultValue(Integer.class));
	}

	static boolean possibleToSplit(String s) {
		return s.startsWith("{") && s.endsWith("}") || s.startsWith("[") || s.endsWith("]");
	}

	static void removeFinal(Field f) throws Exception {
		final Field mod = f.getClass().getDeclaredField("modifiers");
		mod.setAccessible(true);
		mod.set(f, mod.getInt(f) & ~Modifier.FINAL);
	}

	static List<String> splitOut(String s) {
		if (s.length() > 1) {
			s = s.substring(1, s.length() - 1);
		}
		final ArrayList<String> list = new ArrayList<>();
		int cblevel = 0;
		int blevel = 0;
		boolean sq = false;
		boolean dq = false;
		String b = new String();
		int i = 0;
		char last = 0;
		for (final char c : s.toCharArray()) {
			if (!sq && !dq) {
				if (c == '{') {
					cblevel++;
				}
				if (c == '}') {
					cblevel--;
				}
				if (c == '[') {
					blevel++;
				}
				if (c == ']') {
					blevel--;
				}
			}
			if (c == '\'' && !dq && last != '\\') {
				sq = !sq;
			}
			if (c == '"' && !sq && last != '\\') {
				dq = !dq;
			}
			if (c == ',' && cblevel == 0 && blevel == 0 && !dq && !sq || i == s.length() - 1) {
				if (c != ',') {
					b += c;
				}
				list.add(b.trim());
				b = new String();
			} else {
				b += c;
			}
			i++;
			last = c;
		}
		if (cblevel != 0) {
			throw new RuntimeException("unbalanced curly bracket");
		}
		if (blevel != 0) {
			throw new RuntimeException("unbalanced bracket");
		}
		if (sq) {
			throw new RuntimeException("unbalanced single quote");
		}
		if (dq) {
			throw new RuntimeException("unbalanced double quote");
		}
		return list;
	}

	public static String toJSON(Object o) {
		if (o instanceof Obj) {
			return ((Obj) o).toJSON();
		} else {
			return Obj.toJSON(o);
		}
	}

	ArrayList<Function<String, ?>> handlers = new ArrayList<>();
	{
		handlers.add(arg0 -> {
			try {
				return Integer.parseInt(arg0);
			} catch (final Throwable t) {
			}
			return null;
		});
		handlers.add(arg0 -> {
			try {
				return Double.parseDouble(arg0);
			} catch (final Throwable t) {
			}
			return null;
		});
		handlers.add(arg0 -> {
			if (arg0 != null) {
				if (Arrays.asList("true", "false").contains(arg0.toLowerCase())) {
					return Boolean.parseBoolean(arg0);
				}
			}
			return null;
		});
		handlers.add((a) -> {
			if (a != null) {
				if (a.equals("this")) {
					return new Itself(null);
				}
			}
			return null;
		});
	}

	public JsonParser addObjectHandler(Function<String, Object> x) {
		handlers.add(x);
		return this;
	}

	public Obj fromJson(String json) {
		return new Obj(parse(json), null, null, null);
	}

	String get(String s) {
		s = s.trim();
		if (s.equals("this")) {
			return "this";
		}
		s = s.replace("\\\"", "\"").replace("\\'", "'");
		return s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") || s.endsWith("'")
				? s.substring(1, s.length() - 1) : s;
	}

	Object handle(String s) {
		for (final Function<String, ?> h : handlers) {
			final Object o = h.apply(s);
			if (o != null) {
				return o;
			}
		}
		return get(s);
	}

	Object parse(String json) {
		if (json.startsWith("{") && json.endsWith("}")) {
			final Map<Object, Object> objs = new HashMap<>();
			for (final String s : splitOut(json)) {
				final String[] x = s.split(":", 2);
				if (x.length == 2) {
					objs.put(handle(x[0]), parse(x[1]));
				} else {
					throw new RuntimeException("invalid json");
				}
			}
			return objs;
		} else if (json.startsWith("[") && json.endsWith("]")) {
			final ArrayList<Object> objs = new ArrayList<>();
			for (final String s : splitOut(json)) {
				objs.add(parse(s.trim()));
			}
			return objs;
		} else {
			return handle(json);
		}
	}
}
