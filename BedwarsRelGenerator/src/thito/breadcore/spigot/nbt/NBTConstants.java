package thito.breadcore.spigot.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public interface NBTConstants {

	public static int LONGARRAY = 12;
	public static int INTARRAY = 11;
	public static int COMPOUND = 10;
	public static int LIST = 9;
	public static int STRING = 8;
	public static int BYTEARRAY = 7;
	public static int DOUBLE = 6;
	public static int FLOAT = 5;
	public static int LONG = 4;
	public static int INT = 3;
	public static int SHORT = 2;
	public static int BYTE = 1;
	public static int END = 0;
	public static int NUMBER = 99;

	public static void main(String[]args) throws Exception {
		File file = new File("C:/xx.NBT");
		NBTOutputStream output = new NBTOutputStream(new FileOutputStream(file));
		output.writeNBTObject(Arrays.asList("something","that","i","ever","heard"));
		output.close();
		NBTInputStream input = new NBTInputStream(new FileInputStream(file));
		Object o = input.readNBTObject();
		System.out.println(o);
		input.close();
	}
	
	
}
