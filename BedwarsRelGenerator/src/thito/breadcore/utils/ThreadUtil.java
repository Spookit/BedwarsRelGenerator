package thito.breadcore.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadUtil {

	public static void main(String[]args) throws Throwable {
		InputStream stream = Util.class.getResourceAsStream("ThreadUtil.class");
		byte[] bytes = new byte[0];
		byte bite;
		while ((bite = (byte)stream.read()) != -1) {
			bytes = Arrays.copyOf(bytes, bytes.length + 1);
			bytes[bytes.length-1] = bite;
		}
		print(bytes[0]);
		print(bytes[1],bytes[2]);
		print(new String(bytes));
	}
	public static void print(Object...r) {
		for (Object o : r) System.out.println(o);
	}
	public static final ExecutorService SERVICE;
	static Long COUNT = 0L;
	static {
		SERVICE = Executors.newCachedThreadPool(new BacotFactory());
	}
	public static <T> T get(Callable<T> future) {
		try {
			return SERVICE.submit(future).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static class BacotFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			Thread th = new Thread(r, "BacotFactoryThread "+COUNT++);
			return th;
		}

	}
}
