package thito.breadcore.spigot.packets;

import thito.breadcore.utils.EnumConstant;
import thito.breadcore.utils.Util;

public interface PacketPlayOutTitleConstants {

	public static final EnumConstant TITLE = new EnumConstant("TITLE",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	public static final EnumConstant RESET = new EnumConstant("RESET",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	public static final EnumConstant SUBTITLE = new EnumConstant("SUBTITLE",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	public static final EnumConstant TIMES = new EnumConstant("TIMES",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	public static final EnumConstant ACTIONBAR = new EnumConstant("ACTIONBAR",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	public static final EnumConstant CLEAR = new EnumConstant("CLEAR",Util.nms("PacketPlayOutTitle$EnumTitleAction"));
	
}
