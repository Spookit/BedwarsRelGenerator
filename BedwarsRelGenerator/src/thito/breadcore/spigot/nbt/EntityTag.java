package thito.breadcore.spigot.nbt;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import thito.breadcore.utils.Util;

public class EntityTag implements NBTContainer<Entity> {
	

	public static class CustomData {
		public final String key;
		public final String value;
		public final UUID uuid;
		public CustomData(String key,String value, UUID uuid) {
			this.key = key;
			this.value = value;
			this.uuid = uuid;
		}
		public boolean asBoolean() {
			return Boolean.parseBoolean(value);
		}
		public int asInt() {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
			return 0;
		}
		public boolean isBoolean() {
			return "true".equals(value) || "false".equals(value);
		}
		public double asDouble() {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
			}
			return 0;
		}
	}
	// INFORMATION
	public static final String TYPE_CONTAINER = "Type";
	public static final CustomData EMPTY_CUSTOMDATA = new CustomData(new String(),"0", new UUID(12345,67890));
	public static final String[] EXCLUDE = { "Pos", "WorldUUIDMost", "WorldUUIDLeast", "Dimension", "APX", "APY",
			"APZ" };

	// DATA MANAGEMENT
	public static EntityTag fromString(String s) {
		if (s == null) {
			return new EntityTag();
		}
		if (s.equalsIgnoreCase("empty")) {
			final EntityTag data = new EntityTag();
			return data;
		}
		// Entity
		return new EntityTag(s);
	}

	public static Object MojangsonParser_parse(String s) {
		try {
			final Class<?> MojangsonParser = Util.nms("MojangsonParser");
			return MojangsonParser.getMethod("parse", String.class).invoke(null, s);
		} catch (final Exception t) {
			throw new RuntimeException(t);
		}
	}

	// DATA STORAGE
	private NBTTagCompound NBT;

	public EntityTag() {
	}

	public Object NMSEntity;

	public EntityTag(Entity entity) {
		try {
			if (Util.getVersionNumber() >= 12) {
				NMSEntity = entity.getClass().getMethod("getHandle").invoke(entity);
				final Method getter = NMSEntity.getClass().getMethod("save", Util.nms("NBTTagCompound"));
				final Object NBTTagCompound = Util.nms("NBTTagCompound").newInstance();
				getter.invoke(NMSEntity, NBTTagCompound);
				NBT = new NBTTagCompound(NBTTagCompound);
			} else {
				NMSEntity = entity.getClass().getMethod("getHandle").invoke(entity);
				final Method getter = NMSEntity.getClass().getMethod("e", Util.nms("NBTTagCompound"));
				final Object NBTTagCompound = Util.nms("NBTTagCompound").newInstance();
				getter.invoke(NMSEntity, NBTTagCompound);
				NBT = new NBTTagCompound(NBTTagCompound);
			}
			setString(TYPE_CONTAINER, entity.getType().name());
			for (final String s : EXCLUDE) {
				remove(s);
			}
			if (!getNBT().hasKey("IsBaby")) {
				getNBT().setBoolean("IsBaby", false);
			}
		} catch (final Exception t) {
			throw new RuntimeException(t);
		}
	}

	public EntityTag(NBTTagCompound nbtTagCompound) {
		NBT = nbtTagCompound;
	}

	public EntityTag(String json) {
		NBT = new NBTTagCompound(MojangsonParser_parse(json));
	}

	public UUID a(String s) {
		return new UUID(NBT.getLong("UUIDMost"), NBT.getLong("UUIDLeast"));
	}

	public void a(String s, UUID uuid) {
		NBT.setLong("UUIDMost", uuid.getMostSignificantBits());
		NBT.setLong("UUIDLeast", uuid.getLeastSignificantBits());
	}

	public void apply(Entity entity) {
		try {
			final Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
			final Method setter = nmsEntity.getClass().getMethod("f", Util.nms("NBTTagCompound"));
			relocate(entity.getLocation());
			if (entity instanceof Ageable) {
				if (getNBT().hasKey("IsBaby")) {
					final boolean isBaby = getNBT().getBoolean("IsBaby");
					if (isBaby) {
						((Ageable) entity).setBaby();
					} else {
						((Ageable) entity).setAdult();
					}
				}
			}
			setter.invoke(nmsEntity, NBT.asNBTBase());
		} catch (final Exception t) {
			throw new RuntimeException(t);
		}
	}

	public Map<String, Double> getAttributeBase() {
		final Map<String, Double> attr = new HashMap<>();
		final NBTTagList a = NBT.getList("Attributes", 10);
		for (int i = 0; i < a.size(); i++) {
			final NBTWrapper wrapper = a.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound tag = (NBTTagCompound) wrapper;
				attr.put(tag.getString("Name"), tag.getDouble("Base"));
			}
		}
		return attr;
	}
	
	public void setAttributeBase(String attr, double val) {
		final NBTTagList a = NBT.getList("Attributes", 10);
		for (int i = 0; i < a.size(); i++) {
			final NBTWrapper wrapper = a.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound tag = (NBTTagCompound) wrapper;
				if (tag.getString("Name").equals(attr)) {
					tag.setDouble("Base", val);
					break;
				}
			}
		}
	}


	// DATA HANDLER
	public NBTTagCompound getNBT() {
		return NBT;
	}
	public boolean hasCustomData(String key) {
		return getCustomData(key) != EMPTY_CUSTOMDATA;
	}
	public CustomData getCustomData(String key) {
		if (NBT == null) return EMPTY_CUSTOMDATA;
		NBTTagList attr = NBT.getList("Attributes", 10);
		if (attr == null) NBT.set("Attributes", attr = new NBTTagList());
		for (int i = 0; i < attr.size(); i++) {
			final NBTWrapper wrapper = attr.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound comp = (NBTTagCompound) wrapper;
				if ("generic.movementSpeed".equals(comp.getString("Name"))) {
					final NBTTagList modifiers = comp.getList("Modifiers", 10);
					if (modifiers.size() > 0) {
						for (int x = 0; x < modifiers.size(); x++) {
							final NBTWrapper wr = modifiers.get(x);
							if (wr instanceof NBTTagCompound) {
								final NBTTagCompound mod = (NBTTagCompound) wr;
								final String n = mod.getString("Name");
								if (n.startsWith("CD:"+key+":")) {
									return new CustomData(key,n.substring(key.length()+4),new UUID(mod.getLong("UUIDMost"),mod.getLong("UUIDLeast")));
								}
							}
						}
					}
				}
			}
		}
		return EMPTY_CUSTOMDATA;
	}
	
	public void setCustomData(String key,String value, UUID uuidValue) {
		NBTTagList attr = NBT.getList("Attributes", 10);
		if (attr == null) NBT.set("Attributes", attr = new NBTTagList());
		NBTTagCompound comp = null;
		for (int i = 0; i < attr.size(); i++) {
			final NBTWrapper wrapper = attr.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound com = (NBTTagCompound) wrapper;
				if ("generic.movementSpeed".equals(com.getString("Name"))) {
					comp = com;
				}
			}
		}
		if (comp == null) {
			comp = new NBTTagCompound();
			attr.add(comp);
		}
		final NBTTagList modifiers = comp.getList("Modifiers", 10);
		NBTTagCompound mod = null;
		for (int i = 0; i < modifiers.size(); i++) {
			final NBTWrapper wrapper = modifiers.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound modif = (NBTTagCompound) wrapper;
				if (modif.getString("Name").startsWith("CD:"+key+":")) {
					mod = modif;
				}
			}
		}
		if (mod == null) {
			mod = new NBTTagCompound();
			modifiers.add(mod);
		}
		mod.setLong("UUIDMost", uuidValue.getMostSignificantBits());
		mod.setLong("UUIDLeast", uuidValue.getLeastSignificantBits());
		mod.setString("Name", "CD:" + key + ":" + value);
		if (!comp.hasKey("Modifiers")) {
			comp.set("Modifiers", modifiers);
		}
	}
	
	public void removeCustomData(String key) {
		NBTTagList attr = NBT.getList("Attributes", 10);
		if (attr == null) NBT.set("Attributes", attr = new NBTTagList());
		NBTTagCompound comp = null;
		for (int i = 0; i < attr.size(); i++) {
			final NBTWrapper wrapper = attr.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound com = (NBTTagCompound) wrapper;
				if ("generic.movementSpeed".equals(com.getString("Name"))) {
					comp = com;
				}
			}
		}
		if (comp == null) {
			comp = new NBTTagCompound();
			attr.add(comp);
		}
		final NBTTagList modifiers = comp.getList("Modifiers", 10);
		for (int i = 0; i < modifiers.size(); i++) {
			final NBTWrapper wrapper = modifiers.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound modif = (NBTTagCompound) wrapper;
				if (modif.getString("Name").startsWith("CD:"+key+":")) {
					modifiers.remove(modif);
				}
			}
		}
	}
	
	public Set<CustomData> getCustomDatas() {
		if (NBT == null) new HashSet<>();
		NBTTagList attr = NBT.getList("Attributes", 10);
		if (attr == null) NBT.set("Attributes", attr = new NBTTagList());
		final HashSet<CustomData> datas = new HashSet<>();
		for (int i = 0; i < attr.size(); i++) {
			final NBTWrapper wrapper = attr.get(i);
			if (wrapper instanceof NBTTagCompound) {
				final NBTTagCompound comp = (NBTTagCompound) wrapper;
				if ("generic.movementSpeed".equals(comp.getString("Name"))) {
					final NBTTagList modifiers = comp.getList("Modifiers", 10);
					if (modifiers.size() > 0) {
						for (int x = 0; x < modifiers.size(); x++) {
							final NBTWrapper wr = modifiers.get(x);
							if (wr instanceof NBTTagCompound) {
								final NBTTagCompound mod = (NBTTagCompound) wr;
								final String n = mod.getString("Name");
								if (n.startsWith("CD:")) {
									String par = n.substring(3);
									String[] vals = par.split(":",2);
									if (vals.length == 2) {
										datas.add(new CustomData(vals[0],vals[1], new UUID(mod.getLong("UUIDMost"),mod.getLong("UUIDLeast"))));
									}
								}
							}
						}
					}
				}
			}
		}
		return datas;
	}

	public String getString(String s) {
		return NBT.getString(s);
	}

	public EntityType getType() {
		return EntityType.valueOf(getString(TYPE_CONTAINER));
	}

	private void relocate(Location loc) throws Exception {
		final NBTTagList list = new NBTTagList();
		list.add(NBTTag.wrap(loc.getX()));
		list.add(NBTTag.wrap(loc.getY()));
		list.add(NBTTag.wrap(loc.getZ()));
		NBT.set("Pos", list);
	}

	public void remove(String key) {
		NBT.remove(key);
	}


	public void setString(String key, String value) {
		NBT.setString(key, value);
	}

	public String toJSON() {
		return NBT.asNBTBase().toString();
	}


	@Override
	public String toString() {
		return toJSON();
	}

}
