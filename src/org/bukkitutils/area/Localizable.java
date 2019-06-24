package org.bukkitutils.area;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

/** Represents an area */
public interface Localizable {
    
	/**
	 * Gets the world of the area
	 * @return the world
	 */
    public World getWorld();
	
    /**
	 * Gets the positive location of the area
	 * @return the positive location
	 */
	public Location getPositive();
	
	/**
	 * Gets the negative location of the area
	 * @return the negative location
	 */
	public Location getNegative();
	
	/**
	 * Sets the locations of the area
	 * @param positive the first location object
	 * @param negative the second location object
	 */
	public void setLocations(Location positive, Location negative);
	
	/**
	 * Gets all the given area containing the location
	 * @param location the location where search areas
	 * @param inWorld a Localizable list 
	 */
	public static <T extends Localizable> List<T> getLocationLocalizables(Location location, List<T> list) {
		List<T> localizables = new ArrayList<T>();
		World world = location.getWorld();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		for (T localizable : list) {
			Location positive = localizable.getPositive();
			Location negative = localizable.getNegative();
			if (world.equals(positive.getWorld()))
				if (x <= positive.getBlockX() && x >= negative.getBlockX())
					if (y <= positive.getBlockY() && y >= negative.getBlockY())
						if (z <= positive.getBlockZ() && z >= negative.getBlockZ())
							localizables.add(localizable);
		}
		return localizables;
	}
	
}