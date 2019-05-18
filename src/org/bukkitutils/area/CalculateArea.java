package org.bukkitutils.area;

import org.bukkit.Location;

public class CalculateArea {
	
	protected Location positive;
	protected Location negative;
	
	public CalculateArea(Location positive, Location negative) {
		this.positive = new Location(positive.getWorld(), 0, 0, 0);
		this.negative = new Location(positive.getWorld(), 0, 0, 0);
		if (positive.getX() >= negative.getX()) {
			this.positive.setX(positive.getX());
			this.negative.setX(negative.getX());
		} else {
			this.positive.setX(negative.getX());
			this.negative.setX(positive.getX());
		}
		if (positive.getY() >= negative.getY()) {
			this.positive.setY(positive.getY());
			this.negative.setY(negative.getY());
		} else {
			this.positive.setY(negative.getY());
			this.negative.setY(positive.getY());
		}
		if (positive.getZ() >= negative.getZ()) {
			this.positive.setZ(positive.getZ());
			this.negative.setZ(negative.getZ());
		} else {
			this.positive.setZ(negative.getZ());
			this.negative.setZ(positive.getZ());
		}
	}
	
	public Location getPositive() {
		return positive;
	}
	
	public Location getNegative() {
		return negative;
	}
}