package thito.breadcore.spigot.nbt;

import org.bukkit.Location;

import thito.breadcore.utils.ScriptUtil;
import thito.breadcore.utils.Util;

public class TileEntityTag implements NBTContainer<Location> {

	private NBTTagCompound compound;
	public TileEntityTag(Location loc) {
		compound = new NBTTagCompound();
		Object tile = ScriptUtil.run("arg0.getTileEntityAt(arg1,arg2,arg3)", 
				loc.getWorld(),
				loc.getBlockX(),
				loc.getBlockY(),
				loc.getBlockY());
		if (Util.getVersionNumber() >= 12) {
			ScriptUtil.run("arg0.save(arg1)", tile,compound.asNBTBase());
		} else {
			ScriptUtil.run("arg0.b(arg1)", tile, compound.asNBTBase());
		}
	}
	public NBTTagCompound getNBT() {
		return compound;
	}
	@Override
	public void apply(Location loc) {
		if (Util.getVersionNumber() >= 12) {
			ScriptUtil.run("arg0.load(arg1)", 
					ScriptUtil.run("arg0.getTileEntityAt(arg1,arg2,arg3)", 
							loc.getWorld(),
							loc.getBlockX(),
							loc.getBlockY(),
							loc.getBlockY()),
					compound.asNBTBase());
		} else {
			ScriptUtil.run("arg0.a(arg1)", 
					ScriptUtil.run("arg0.getTileEntityAt(arg1,arg2,arg3)", 
							loc.getWorld(),
							loc.getBlockX(),
							loc.getBlockY(),
							loc.getBlockY()),
					compound.asNBTBase());
		}
	}
}
