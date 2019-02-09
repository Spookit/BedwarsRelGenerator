package thito.breadcore.spigot.nbt;

public interface NBTContainer<T> {

	public NBTWrapper getNBT(); 
	public void apply(T t);
	
}
