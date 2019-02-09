package thito.breadcore.spigot.nbt;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NBTOutputStream extends DataOutputStream {

	public NBTOutputStream(OutputStream out) {
		super(out);
	}
	
	public void writeNBT(NBTWrapper wrapper) throws IOException {
		set(this,wrapper.toJavaObject());
	}
	
	public void writeNBTObject(Object o) throws IOException {
		set(this,o);
	}
	
	private void set(DataOutput output,Object o) throws IOException {
		set(output,o,true);
	}
	private void set(DataOutput output,Object o,boolean writeType) throws IOException {
		if (o instanceof Byte) {
			if (writeType) output.writeByte(1);
			output.writeByte((byte)o);
		} else if (o instanceof Short) {
			if (writeType) output.writeByte(2);
			output.writeShort((short)o);
		} else if (o instanceof Integer) {
			if (writeType) output.writeByte(3);
			output.writeInt((int)o);
		} else if (o instanceof Long) {
			if (writeType) output.writeByte(4);
			output.writeLong((long)o);
		} else if (o instanceof Float) {
			if (writeType) output.writeByte(5);
			output.writeFloat((float)o);
		} else if (o instanceof Double) {
			if (writeType) output.writeByte(6);
			output.writeDouble((double)o);
		} else if (o instanceof byte[]) {
			if (writeType) output.writeByte(7);
			int length = ((byte[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeByte(((byte[])o)[i]);
		} else if (o instanceof Byte[]) {
			if (writeType) output.writeByte(7);
			int length = ((Byte[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeByte(((Byte[])o)[i]);
		} else if (o instanceof String) {
			if (writeType) output.writeByte(8);
			output.writeUTF((String)o);
		} else if (o instanceof List) {
			if (writeType) output.writeByte(9);
			List<?> list = (List<?>)o;
			if (list.isEmpty()) {
				output.writeByte(0);
			} else {
				output.writeByte(getType(list.get(0)));
			}
			output.writeInt(list.size());
			for (Object ox : list) {
				set(output,ox,false);
			}
		} else if (o instanceof Map) {
			if (writeType) output.writeByte(10);
			Map<?,?> map = (Map<?,?>)o;
			for (Entry<?,?> x : map.entrySet()) {
				output.writeByte(getType(x.getValue()));
				output.writeUTF(String.valueOf(x.getKey()));
				set(output,x.getValue(),false);
			}
			output.writeByte(0);
		} else if (o instanceof int[]) {
			if (writeType) output.writeByte(11);
			int length = ((int[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeInt(((int[])o)[i]);
		} else if (o instanceof Integer[]) {
			if (writeType) output.writeByte(11);
			int length = ((Integer[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeInt(((Integer[])o)[i]);
		} else if (o instanceof long[]) {
			if (writeType) output.writeByte(12);
			int length = ((long[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeLong(((long[])o)[i]);
		} else if (o instanceof Long[]) {
			if (writeType) output.writeByte(12);
			int length = ((Long[]) o).length;
			output.writeInt(length);
			for (int i =0;i <length;i++) output.writeLong(((Long[])o)[i]);
		} else if (o instanceof Boolean) {
			if (writeType) output.writeByte(1);
			output.writeByte(((Boolean)o) ? 1 : 0);
		} else {
			System.out.println("Rejected type: "+(o == null ? "null" : o.getClass().getName()));
		}
	}
	private byte getType(Object o) {
		if (o instanceof Byte) {
			return 1;
		} else if (o instanceof Short) {
			return 2;
		} else if (o instanceof Integer) {
			return 3;
		} else if (o instanceof Long) {
			return 4;
		} else if (o instanceof Float) {
			return 5;
		} else if (o instanceof Double) {
			return 6;
		} else if (o instanceof byte[]) {
			return 7;
		} else if (o instanceof Byte[]) {
			return 7;
		} else if (o instanceof String) {
			return 8;
		} else if (o instanceof List) {
			return 9;
		} else if (o instanceof Map) {
			return 10;
		} else if (o instanceof int[]) {
			return 11;
		} else if (o instanceof Integer[]) {
			return 11;
		} else if (o instanceof long[]) {
			return 12;
		} else if (o instanceof Long[]) {
			return 12;
		} else if (o instanceof Boolean) {
			return 1;
		}
		return 0;
	}
}
