package thito.breadcore.spigot.nbt;

public interface NBTWrapper {

	public Object asNBTBase();

	public void setNBTBase(Object o);
	
	public Object toJavaObject();

}
