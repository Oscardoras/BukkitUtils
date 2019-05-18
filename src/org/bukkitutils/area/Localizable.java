package org.bukkitutils.area;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

public interface Localizable {
    
    public World getWorld();
	
	public Location getPositive();
	
	public Location getNegative();
	
	public void setLocations(Location positive, Location negative);
	
	public static <T extends Localizable> List<T> getLocationLocalizables(Location location, List<T> inWorld) {
		List<T> localizables = new ArrayList<T>();
		for (T localizable : inWorld) {
			Location positive = localizable.getPositive();
			Location negative = localizable.getNegative();
			if (location.getX() < positive.getX() + 1 && location.getX() >= negative.getX()) {
				if (location.getY() < positive.getY() + 1 && location.getY() >= negative.getY()) {
					if (location.getZ() < positive.getZ() + 1 && location.getZ() >= negative.getZ()) {
						localizables.add(localizable);
					}
				}
			}
		}
		return localizables;
	}
	
}