package thito.breadcore.spigot.packets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import thito.breadcore.spigot.nms.NMSEntityPlayer;
import thito.breadcore.spigot.nms.NMSObject;
import thito.breadcore.utils.EnumConstant;
import thito.breadcore.utils.ObjectWrapper;
import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.StrUtil;
import thito.breadcore.utils.Util;

public class SimplePacketWrapper implements PacketWrapper {

	public static boolean DYNAMIC_PACKETPLAYOUTTITLE = true;
	public static SimplePacketWrapper PacketPlayOutChat(String message,Object messageType) {
		return new SimplePacketWrapper("PacketPlayOutChat",false,message,messageType);
	}
	
	@Deprecated
	public static SimplePacketWrapper PacketPlayOutTitle(String title,EnumConstant type,int fadeIn, int stay,int fadeOut) {
		return new SimplePacketWrapper("PacketPlayOutTitle",false,type.get(),title,fadeIn,stay,fadeOut);
	}
	
	public static void sendTitle(Player p,String title,String subtitle,int fadeIn,int stay,int fadeOut) {
		if (DYNAMIC_PACKETPLAYOUTTITLE && Util.getVersionNumber() >= 11) {
			p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			return;
		}
		if (title != null) {
			if (Util.getVersionNumber() <= 8) {
				if (title.length() > 32) {
					title = title.substring(0, 32);
				}
			}
			ObjectWrapper wrapper = new ObjectWrapper(Util.nms("PacketPlayOutTitle"));
			wrapper.set("a", PacketPlayOutTitleConstants.TITLE.get());
			wrapper.set("b", ScriptUtil.run("arg0.a(arg1)",Util.nms("IChatBaseComponent$ChatSerializer"),StrUtil.textJson(title)));
			ScriptUtil.run("arg0.getHandle().playerConnection.sendPacket(arg1)", p,new ObjectWrapper(Util.nms("PacketPlayOutTitle"),fadeIn,stay,fadeOut).getInstance());
			synchronized (wrapper) {
				ScriptUtil.run("arg0.getHandle().playerConnection.sendPacket(arg1)", p,wrapper.getInstance());
			}
		}
		
		if (subtitle != null) {
			if (Util.getVersionNumber() <= 8) {
				if (subtitle.length() > 32) {
					subtitle = subtitle.substring(0, 32);
				}
			}
			ObjectWrapper wrapper = new ObjectWrapper(Util.nms("PacketPlayOutTitle"));
			wrapper.set("a", PacketPlayOutTitleConstants.SUBTITLE.get());
			wrapper.set("b", ScriptUtil.run("arg0.a(arg1)",Util.nms("IChatBaseComponent$ChatSerializer"),StrUtil.textJson(subtitle)));
			ScriptUtil.run("arg0.getHandle().playerConnection.sendPacket(arg1)", p,new ObjectWrapper(Util.nms("PacketPlayOutTitle"),fadeIn,stay,fadeOut).getInstance());
			synchronized (wrapper) {
				ScriptUtil.run("arg0.getHandle().playerConnection.sendPacket(arg1)", p,wrapper.getInstance());
			}
		}
	}
	
	public static SimplePacketWrapper PacketPlayOutPlayerInfo(EnumConstant actionType, List<NMSEntityPlayer> players) {
		return new SimplePacketWrapper("PacketPlayOutPlayerInfo",true,actionType.get(),players);
	}
	
	private ObjectWrapper o;
	public SimplePacketWrapper(String packetName,boolean usingConstructor,Object...fields) {
		this(Util.nms(packetName),usingConstructor,fields);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimplePacketWrapper(Class<?> packetClass,boolean usingConstructor, Object... fields) {
		if (usingConstructor) {
			for (int i = 0; i <fields.length; i++) {
				Object f = fields[i];
				if (f == null) continue;
				if (f instanceof NMSObject) {
					fields[i] = ((NMSObject) f).getWrapped();
				} else if (f instanceof List) {
					ArrayList x = new ArrayList();
					((List) f).forEach(a->{
						if (a instanceof NMSObject) {
							x.add(((NMSObject)a).getWrapped());
						}
					});
					fields[i] = x;
				}
			}
			o = new ObjectWrapper(packetClass,fields);
		} else {
			o = new ObjectWrapper(packetClass);
			int fieldIndex = 0;
			Field[] f = o.fields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i] != null && !Modifier.isStatic(f[fieldIndex].getModifiers())) if (ObjectWrapper.isAssignableFrom(fields[i].getClass(), f[fieldIndex].getType())) {
					try {
						f[fieldIndex].setAccessible(true);
						f[fieldIndex].set(o.getInstance(), fields[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					fieldIndex++;
				} else if (Enum.class.isAssignableFrom(f[fieldIndex].getType())) {
					if (fields[i] instanceof String) {
						try {
							f[fieldIndex].setAccessible(true);
							f[fieldIndex].set(o.getInstance(), Enum.valueOf((Class<? extends Enum>)f[fieldIndex].getType(), (String)fields[i]));
						} catch (Exception e) {
							e.printStackTrace();
						}
						fieldIndex++;
					} else if (fields[i] instanceof Enum) {
						try {
							f[fieldIndex].setAccessible(true);
							f[fieldIndex].set(o.getInstance(), Enum.valueOf((Class<? extends Enum>)f[fieldIndex].getType(), ((Enum<?>)fields[i]).name()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						fieldIndex++;
					}
				} else if (Util.nms("IChatBaseComponent").isAssignableFrom(f[fieldIndex].getType())) {
					if (fields[i] instanceof String) {
						String text = (String)fields[i];
						try {
							f[fieldIndex].setAccessible(true);
							f[fieldIndex].set(o.getInstance(), ScriptUtil.run("arg0.a(arg1)", Util.nms("IChatBaseComponent$ChatSerializer"),StrUtil.textJson(text)));
						} catch (Exception e) {
							e.printStackTrace();
						}
						fieldIndex++;
					}
				}
			}
		}
	}
	@Override
	public Object getPacketInstance() {
		return o.getInstance();
	}
}
