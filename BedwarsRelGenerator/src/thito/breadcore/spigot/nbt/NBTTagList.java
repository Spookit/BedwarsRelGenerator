package thito.breadcore.spigot.nbt;

import java.io.DataOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import thito.breadcore.utils.Util;

public class NBTTagList implements NBTWrapper {

	protected static final Class<?> clazz = Util.nms("NBTTagList");
	private static final Class<?> base = Util.nms("NBTBase");
	private static Method get;
	private static Method add;
	private static Method clone;
	private static Method size;
	private static Method write;
	private static Method c;
	private static Method getTypeId;
	private static Method f;
	private static Method d;

	static {
		try {
			add = clazz.getDeclaredMethod("add", base);
			get = clazz.getDeclaredMethod("get", int.class);
			clone = clazz.getDeclaredMethod("clone");
			size = clazz.getDeclaredMethod("size");
			write = clazz.getDeclaredMethod("write", DataOutput.class);
			c = clazz.getDeclaredMethod("c", int.class);
			getTypeId = clazz.getDeclaredMethod("getTypeId");
			f = clazz.getDeclaredMethod("f", int.class);
			d = clazz.getDeclaredMethod("d");
		} catch (final Exception e) {
		}
	}

	private Object nbt;

	public NBTTagList() {
		try {
			nbt = clazz.newInstance();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public NBTTagList(Object nbt) {
		this.nbt = nbt;
	}

	public void add(NBTWrapper wrapper) {
		try {
			add.invoke(nbt, wrapper.asNBTBase());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object asNBTBase() {
		return nbt;
	}

	public int[] c(int arg0) {
		try {
			return (int[]) c.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public NBTTagList clone() {
		try {
			return new NBTTagList(clone.invoke(nbt));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int d() {
		try {
			return (int) d.invoke(nbt);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean equals(Object arg0) {
		return nbt.equals(arg0);
	}

	public String f(int arg0) {
		try {
			return (String) f.invoke(nbt, arg0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public NBTWrapper get(int arg0) {
		try {
			final Object o = get.invoke(nbt, arg0);
			if (clazz.isAssignableFrom(o.getClass())) {
				return new NBTTagList(o);
			} else if (NBTTagCompound.clazz.isAssignableFrom(o.getClass())) {
				return new NBTTagCompound(o);
			} else {
				return new NBTTag(o);
			}
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

	public void remove(NBTWrapper wrapper) {
		try {
			final Field list = clazz.getDeclaredField("list");
			list.setAccessible(true);
			((List<?>) list.get(nbt)).remove(wrapper.asNBTBase());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNBTBase(Object o) {
		nbt = o;
	}

	public int size() {
		try {
			return (int) size.invoke(nbt);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
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
	public List<Object> toJavaObject() {
		ArrayList<Object> objs = new ArrayList<>();
		for (int i = 0;i < size();i++) {
			objs.add(get(i).toJavaObject());
		}
		return objs;
	}

}
