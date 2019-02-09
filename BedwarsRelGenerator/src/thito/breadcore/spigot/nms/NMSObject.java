package thito.breadcore.spigot.nms;

import java.util.ArrayList;

import thito.breadcore.utils.ArrayUtil;
import thito.breadcore.utils.ScriptUtil;

public interface NMSObject {

	public Object getWrapped();
	public default Object invoke(String script,Object...args) {
		return ScriptUtil.run(script, ArrayUtil.combine(new Object[] {getWrapped()}, args));
	}
	public static <T extends NMSObject> Object[] convertArray(T[] t) {
		Object o[] = new Object[t.length];
		for (int i = 0; i <t.length; i++) o [i] = t [i].getWrapped();
		return o;
	}
	
	public static <T extends NMSObject> Iterable<Object> convertIterable(Iterable<T> i) {
		ArrayList<Object> obj = new ArrayList<>();
		i.iterator().forEachRemaining(a->{
			obj.add(a.getWrapped());
		});
		return obj;
	}
	
}
