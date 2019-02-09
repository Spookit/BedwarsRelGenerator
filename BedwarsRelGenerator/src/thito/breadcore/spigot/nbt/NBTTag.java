package thito.breadcore.spigot.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import thito.breadcore.utils.Util;

public class NBTTag extends Number implements NBTWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Class<?> NBTTagEnd = Util.nms("NBTTagEnd");

	public static void main(String[] args) {
		wrap(new String[] { "" });
	}

	public static NBTTag wrap(Object value, String NBTClassName) {
		try {
			if (value == null) {
				return new NBTTag(NBTTagEnd.newInstance());
			}
			final Class<?> declared = Util.nms(NBTClassName);
			return new NBTTag(declared).setDataValue(value);

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String fixCapitalize(String n) {
		String b = new String();
		for (final String s : n.split("_")) {
			if (s.isEmpty()) {
				continue;
			}
			if (s.length() > 1) {
				b += s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
			} else {
				b += s.toUpperCase();
			}
		}
		return b;
	}
	public static NBTTag wrap(Object value) {
		try {
			if (value == null) {
				return new NBTTag(NBTTagEnd.newInstance());
			}
			if (value instanceof Boolean) {
				return wrap((Boolean) value ? 1 : 0, "NBTTagByte");
			}
			Class<?> defined = value.getClass();
			boolean array = false;
			if (value.getClass().isArray()) {
				array = true;
				defined = value.getClass().getComponentType();
			}
			if (!defined.isPrimitive() && value instanceof Number) {
				defined = (Class<?>) defined.getField("TYPE").get(null);
			}
			final String simpleName = fixCapitalize(defined.getSimpleName()) + (array ? "Array" : "");
			final Class<?> declared = Util.nms("NBTTag" + simpleName);
			return new NBTTag(declared).setDataValue(value);

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object nbt;
	private Field dataGetter;

	public boolean isNBTTagEnd() {
		return NBTTagEnd.isInstance(nbt);
	}

	NBTTag(Class<?> nbtClass) throws Exception {
		final Constructor<?> cons = nbtClass.getDeclaredConstructor();
		cons.setAccessible(true);
		setNBTBase(cons.newInstance());
	}

	public NBTTag(Object nbt) {
		setNBTBase(nbt);
	}

	@Override
	public Object asNBTBase() {
		return nbt;
	}

	public byte[] byteArrayValue() {
		final Object data = dataValue();
		if (data instanceof byte[]) {
			return (byte[]) data;
		}
		throw new UnsupportedOperationException();
	}

	public Object dataValue() {
		try {
			if (dataGetter != null) {
				dataGetter.get(nbt);
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public boolean booleanValue() {
		final Object data = dataValue();
		if (data instanceof Byte) {
			return (Byte) data == 1;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public double doubleValue() {
		final Object data = dataValue();
		if (data instanceof Number) {
			final Number number = (Number) data;
			return number.doubleValue();
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public float floatValue() {
		final Object data = dataValue();
		if (data instanceof Number) {
			final Number number = (Number) data;
			return number.floatValue();
		}
		throw new UnsupportedOperationException();
	}

	public int[] intArrayValue() {
		final Object data = dataValue();
		if (data instanceof int[]) {
			return (int[]) data;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public int intValue() {
		final Object data = dataValue();
		if (data instanceof Number) {
			final Number number = (Number) data;
			return number.intValue();
		}
		throw new UnsupportedOperationException();
	}

	public long[] longArrayValue() {
		final Object data = dataValue();
		if (data instanceof long[]) {
			return (long[]) data;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public long longValue() {
		final Object data = dataValue();
		if (data instanceof Number) {
			final Number number = (Number) data;
			return number.longValue();
		}
		throw new UnsupportedOperationException();
	}

	public NBTTag setDataValue(Object value) {
		try {
			dataGetter.set(nbt, value);
		} catch (final Exception e) {
		}
		return this;
	}

	@Override
	public void setNBTBase(Object o) {
		nbt = o;
		try {
			dataGetter = o.getClass().getDeclaredField("data");
			dataGetter.setAccessible(true);
		} catch (final Exception e) {
		}
	}

	public String stringValue() {
		final Object data = dataValue();
		if (data == null) {
			return null;
		}
		return String.valueOf(data);
	}

	@Override
	public String toString() {
		return stringValue();
	}

	@Override
	public Object toJavaObject() {
		return dataValue();
	}

}
