package org.bukkitutils.area;

import org.bukkit.Location;

/** A tool to calculate an area coordinates */
public class CalculateArea {
	
	protected final Location positive;
	protected final Location negative;
	
	/**
	 * A tool to calculate an area coordinates
	 * @param positive the first location object
	 * @param negative the second location object
	 */
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
	
	/**
	 * Gets the positive location
	 * @return the positive location
	 */
	public Location getPositive() {
		return positive;
	}
	
	/**
	 * Gets the negative location
	 * @return the negative location
	 */
	public Location getNegative() {
		return negative;
	}
	
}