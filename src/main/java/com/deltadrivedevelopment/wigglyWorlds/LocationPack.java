package com.deltadrivedevelopment.wigglyWorlds;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationPack implements Serializable {
	
	private static final long serialVersionUID = -8100514952085724461L;

	private final String worldname;
	private final double x;
	private final double y;
	private final double z;

	public LocationPack(Location location) {
		this.worldname = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}

	public Location unpack() {
		Location location = new Location(Bukkit.getWorld(this.worldname),
				this.x, this.y, this.z);

		return location;
	}
}