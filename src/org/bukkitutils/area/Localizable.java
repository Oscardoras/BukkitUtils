package org.bukkitutils.area;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.sun.istack.internal.NotNull;

/** Represents an area. */
public interface Localizable {
    
	/**
	 * Gets the world of the area.
	 * @return the world
	 */
    public @NotNull World getWorld();
	
    /**
	 * Gets the positive location of the area.
	 * @return the positive location
	 */
	public @NotNull Location getPositive();
	
	/**
	 * Gets the negative location of the area.
	 * @return the negative location
	 */
	public @NotNull Location getNegative();
	
	/**
	 * Sets the locations of the area.
	 * @param positive the first location object
	 * @param negative the second location object
	 */
	public void setLocations(@NotNull Location positive, @NotNull Location negative);
	
	/**
	 * Gets all the given areas containing the location.
	 * @param location the location where search areas
	 * @param list a Localizable list
	 * @return the areas containing the location
	 */
	public static @NotNull <T extends Localizable> List<T> getLocationLocalizables(@NotNull Location location, @NotNull List<T> list) {
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