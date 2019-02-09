package thito.breadcore.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ObjectWrapper {

	public static void main(String[]args) throws Exception {
		long start = System.currentTimeMillis();
		ObjectWrapper wrapper = new ObjectWrapper(B.class);
		wrapper.set("a", 12);
		System.out.println(wrapper.getInstance().toString());
		System.out.println(System.currentTimeMillis()-start);
	}
	public static class C {
		int c = 20;
	}
	public static class A extends C {
		int a = 10;
	}
	public static class B extends A {
		int b = 12;
		public String toString() {
			return c+"-"+a;
		}
	}
	private Object instance; 
	private Class<?> ins;
	public ObjectWrapper(Class<?> packet,Object...objects) {
		ins = packet;
		for (Constructor<?> cons : packet.getDeclaredConstructors()) {
			if (cons.getParameterCount() == objects.length) {
				boolean accept = true;
				int index = 0;
				for (Class<?> cl : cons.getParameterTypes()) {
					if (objects[index] == null) continue;
					if (!objects[index].getClass().isAssignableFrom(cl)) {
						try {
							if (cl.isPrimitive() && ((Class<?>)objects[index].getClass().getField("TYPE").get(null)).isAssignableFrom(cl)) continue;
							if (objects[index].getClass().isPrimitive() && ((Class<?>)cl.getField("TYPE").get(null)).isAssignableFrom(objects[index].getClass())) continue;
						} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						}
						accept = false;
						break;
					}
					index++;
				}
				if (accept) {
					try {
						instance = cons.newInstance(objects);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public ObjectWrapper() {
	}
	
	public void setInstance(Object o) {
		instance = o;
		ins = o.getClass();
	}
	
	public Object getInstance() {
		return instance;
	}
	public Field[] fields() {
		Field[] fields = ins.getDeclaredFields();
		Class<?> superclass = ins;
		while ((superclass = superclass.getSuperclass()) != null) {
			fields = ArrayUtil.combine(fields,superclass.getDeclaredFields());
		}
		for (Field f : fields) {
			f.setAccessible(true);
		}
		return fields;
	}
	public Method[] methods() {
		Method[] methods = ins.getDeclaredMethods();
		Class<?> superclass = ins;
		while ((superclass = superclass.getSuperclass()) != null) {
			methods = ArrayUtil.combine(methods,superclass.getDeclaredMethods());
		}
		for (Method m : methods) m.setAccessible(true);
		return methods;
	}
	public void set(String field, Object value) {
		Field f = ArrayUtil.get(a->{
			return a.getName().equals(field);
		}, fields());
		if (f != null) {
			try {
				f.setAccessible(true);
				f.set(Modifier.isStatic(f.getModifiers()) ? null : instance, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean isAssignableFrom(Class<?> cl,Class<?> cl2) {
		if (cl.isAssignableFrom(cl2)) return true;
		try {
			if (cl.isPrimitive() && ((Class<?>)cl2.getField("TYPE").get(null)).isAssignableFrom(cl)) return true;
			if (cl2.isPrimitive() && ((Class<?>)cl.getField("TYPE").get(null)).isAssignableFrom(cl2)) return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
		return false;
	}
	public Object invokeMethod(String method,Object...args) {
		for (Method met : methods()) {
			if (met.getParameterCount() == args.length) {
				boolean accept = true;
				int index = 0;
				for (Class<?> cl : met.getParameterTypes()) {
					if (!args[index].getClass().isAssignableFrom(cl)) {
						try {
							if (cl.isPrimitive() && ((Class<?>)args[index].getClass().getField("TYPE").get(null)).isAssignableFrom(cl)) continue;
						} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						}
						accept = false;
						break;
					}
					index++;
				}
				if (accept) {
					try {
						return Modifier.isStatic(met.getModifiers()) ? met.invoke(null, args) : met.invoke(instance, args);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public Object get(String field) {
		Field f = ArrayUtil.get(a->{
			return a.getName().equals(field);
		}, fields());
		if (f != null) {
			try {
				f.setAccessible(true);
				return f.get(Modifier.isStatic(f.getModifiers()) ? null : instance);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
