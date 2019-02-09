package thito.breadcore.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import jdk.internal.dynalink.beans.StaticClass;

public class ScriptUtil extends jdk.nashorn.internal.runtime.ScriptObject {

	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	public static synchronized Object run(String script,Object...objects) {
		SimpleBindings bindings = new SimpleBindings();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Class) objects[i] = StaticClass.forClass((Class<?>)objects[i]);
			bindings.put("arg"+i, objects[i]);
		}
		try {
			return engine.eval(script, bindings);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized Object runNoClassHandler(String script,Object...objects) {
		SimpleBindings bindings = new SimpleBindings();
		for (int i = 0; i < objects.length; i++) {
			bindings.put("arg"+i, objects[i]);
		}
		try {
			return engine.eval(script, bindings);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[]args) throws Exception{
		run("arg0.out.println(new arg1('hi'))",new Object[] {System.class,String.class});
	}
}
