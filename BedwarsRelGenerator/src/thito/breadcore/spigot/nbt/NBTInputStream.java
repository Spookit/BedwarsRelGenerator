package thito.breadcore.spigot.nbt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTInputStream extends DataInputStream {

	public NBTInputStream(InputStream in) {
		super(in);
	}
	
	public NBTWrapper readNBT() throws IOException {
		return NBTTag.wrap(get(readByte(),this));
	}
	
	public Object readNBTObject() throws IOException {
		return get(readByte(),this);
	}
	
	private Object get(byte b,DataInput input) throws IOException {
		switch (b) {
		case 1:
			return input.readByte();
		case 2:
			return input.readShort();
		case 3:
			return input.readInt();
		case 4:
			return input.readLong();
		case 5:
			return input.readFloat();
		case 6:
			return input.readDouble();
		case 7:
			byte[] array = new byte[input.readInt()];
			input.readFully(array);
			return array;
		case 8:
			return input.readUTF();
		case 9:
			byte type = input.readByte();
			int capacity = input.readInt();
			ArrayList<Object> list = new ArrayList<>(capacity);
			fill(type,capacity,list,input);
			return list;
		case 10:
			Map<String,Object> map = new HashMap<>();
			fill(map,input);
			return map;
		case 11:
			int[] array2 = new int[input.readInt()];
			for (int i = 0; i < array2.length; i++) array2[i] = input.readInt();
			return array2;
		case 12:
			long[] array3 = new long[input.readInt()];
			for (int i = 0; i < array3.length; i++) array3[i] = input.readLong();
			return array3;
		case 0:
			byte by = input.readByte();
			if (by != 0) return get(by,input);
		}
		throw new IllegalArgumentException("no type supported: "+b);
	}
	private void fill(Map<String,Object> obj, DataInput input) throws IOException {
		byte b;
		while ((int)(b = input.readByte()) != 0) {
			obj.put(input.readUTF(), get(b,input));
		}
	}
	private void fill(byte b,int cap,List<Object> obj,DataInput input) throws IOException {
		for (int x = 1 ; x - 1 < cap; x++) {
			obj.add(get(b,input));
		}
	}

}
