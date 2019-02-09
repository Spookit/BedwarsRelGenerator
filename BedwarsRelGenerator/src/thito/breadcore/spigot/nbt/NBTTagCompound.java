package thito.breadcore.spigot.nbt;

import java.io.DataOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import thito.breadcore.utils.Util;

public class NBTTagCompound implements NBTWrapper {

	protected static final Class<?> clazz = Util.nms("NBTTagCompound");
	private static final Class<?> base = Util.nms("NBTBase");
	private static Method set;
	private static Method remove;
	private static Method get;
	private static Method clone;
	private static Method getBoolean;
	private static Method getByte;
	private static Method getShort;
	private static Method getInt;
	private static Method getLong;
	private static Method getFloat;
	private static Method getDouble;
	private static Method isEmpty;
	private static Method write;
	private static Method setBoolean;
	private static Method setByte;
	private static Method setDouble;
	private static Method setFloat;
	private static Method setInt;
	private static Method setLong;
	private static Method setShort;
	private static Method getString;
	private static Method b;
	private static Method hasKey;
	private static Method getList;
	private static Method getTypeId;
	private static Method setString;
	private static Method setByteArray;
	private static Method setIntArray;
	private static Method hasKeyOfType;
	private static Method getByteArray;
	private static Method getIntArray;
	private static Method getCompound;
	private static Field map;

	static {
		try {
			map = clazz.getDeclaredField("map");
			map.setAccessible(true);
			set = clazz.getDeclaredMethod("set", String.class, base);
			remove = clazz.getDeclaredMethod("remove", String.class);
			get = clazz.getDeclaredMethod("get", String.class);
			clone = clazz.getDeclaredMethod("clone");
			getBoolean = clazz.getDeclaredMethod("getBoolean", String.class);
			getByte = clazz.getDeclaredMethod("getByte", String.class);
			getShort = clazz.getDeclaredMethod("getShort", String.class);
			getInt = clazz.getDeclaredMethod("getInt", String.class);
			getLong = clazz.getDeclaredMethod("getLong", String.class);
			getFloat = clazz.getDeclaredMethod("getFloat", String.class);
			getDouble = clazz.getDeclaredMethod("getDouble", String.class);
			isEmpty = clazz.getDeclaredMethod("isEmpty");
			write = clazz.getDeclaredMethod("write", DataOutput.class);
			setBoolean = clazz.getDeclaredMethod("setBoolean", String.class, boolean.class);
			setByte = clazz.getDeclaredMethod("setByte", String.class, byte.class);
			setDouble = clazz.getDeclaredMethod("setDouble", String.class, double.class);
			setFloat = clazz.getDeclaredMethod("setFloat", String.class, float.class);
			setInt = clazz.getDeclaredMethod("setInt", String.class, int.class);
			setLong = clazz.getDeclaredMethod("setLong", String.class, long.class);
			setShort = clazz.getDeclaredMethod("setShort", String.class, short.class);
			getTypeId = clazz.getDeclaredMethod("getTypeId");
			setString = clazz.getDeclaredMethod("setString", String.class, String.class);
			setByteArray = clazz.getDeclaredMethod("setByteArray", String.class, byte[].class);
			setIntArray = clazz.getDeclaredMethod("setIntArray", String.class, int[].class);
			hasKeyOfType = clazz.getDeclaredMethod("hasKeyOfType", String.class, int.class);
			getByteArray = clazz.getDeclaredMethod("getByteArray", String.class);
			getIntArray = clazz.getDeclaredMethod("getIntArray", String.class);
			getCompound = clazz.getDeclaredMethod("getCompound", String.class);
			b = clazz.getDeclaredMethod("b", String.class);
			hasKey = clazz.getDeclaredMethod("hasKey", String.class);
			getList = clazz.getDeclaredMethod("getList", String.class, int.class);
			getString = clazz.getDeclaredMethod("getString", String.class);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private Object nbt;

	public NBTTagCompound() {
		try {
			nbt = clazz.newInstance();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public NBTTagCompound(Object nbt) {
		this.nbt = nbt;
	}

	@Override
	public Object asNBTBase() {
		return nbt;
	}

	public byte b(String arg0) {
		try {
			return (byte) b.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public NBTTagCompound clone() {
		try {
			return new NBTTagCompound(clone.invoke(nbt));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object arg0) {
		return nbt.equals(arg0);
	}

	public NBTWrapper get(String arg0) {
		try {
			final Object object = get.invoke(nbt, arg0);
			if (clazz.isAssignableFrom(object.getClass())) {
				return new NBTTagCompound(object);
			} else if (NBTTagList.clazz.isAssignableFrom(object.getClass())) {
				return new NBTTagList(object);
			} else {
				return new NBTTag(object);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean getBoolean(String arg0) {
		try {
			return (boolean) getBoolean.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public byte getByte(String arg0) {
		try {
			return (byte) getByte.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public byte[] getByteArray(String arg0) {
		try {
			return (byte[]) getByteArray.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public NBTTagCompound getCompound(String arg0) {
		try {
			return new NBTTagCompound(getCompound.invoke(nbt, arg0));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public double getDouble(String arg0) {
		try {
			return (double) getDouble.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public float getFloat(String arg0) {
		try {
			return (float) getFloat.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0.0F;
	}

	public int getInt(String arg0) {
		try {
			return (int) getInt.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int[] getIntArray(String arg0) {
		try {
			return (int[]) getIntArray.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public NBTTagList getList(String arg0, int arg1) {
		try {
			return new NBTTagList(getList.invoke(nbt, arg0, arg1));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getLong(String arg0) {
		try {
			return (long) getLong.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public short getShort(String arg0) {
		try {
			return (short) getShort.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getString(String arg0) {
		try {
			return (String) getString.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte getTypeId() {
		try {
			return (byte) getTypeId.invoke(nbt);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int hashCode() {
		return nbt.hashCode();
	}

	public boolean hasKey(String arg0) {
		try {
			return (boolean) hasKey.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasKeyOfType(String arg0, int arg1) {
		try {
			return (boolean) hasKeyOfType.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isEmpty() {
		try {
			return (boolean) isEmpty.invoke(nbt);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void remove(String arg0) {
		try {
			remove.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void set(String s, NBTWrapper wrapper) {
		try {
			set.invoke(nbt, s, wrapper.asNBTBase());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setBoolean(String arg0, boolean arg1) {
		try {
			setBoolean.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setByte(String arg0, byte arg1) {
		try {
			setByte.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setByteArray(String arg0, byte[] arg1) {
		try {
			setByteArray.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setDouble(String arg0, double arg1) {
		try {
			setDouble.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setFloat(String arg0, float arg1) {
		try {
			setFloat.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setInt(String arg0, int arg1) {
		try {
			setInt.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setIntArray(String arg0, int[] arg1) {
		try {
			setIntArray.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setLong(String arg0, long arg1) {
		try {
			setLong.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNBTBase(Object o) {
		nbt = o;
	}

	public void setShort(String arg0, short arg1) {
		try {
			setShort.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setString(String arg0, String arg1) {
		try {
			setString.invoke(nbt, arg0, arg1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return nbt.toString();
	}

	public void write(DataOutput arg0) {
		try {
			write.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String,Object> toJavaObject() {
		Map<String,Object> objs = new HashMap<>();
		try {
			for (Object key : ((Map<?,?>)map.get(nbt)).keySet()) {
				objs.put((String)key, get((String)key).toJavaObject());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objs;
	}

}
