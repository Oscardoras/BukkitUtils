package org.bukkitutils.area;

import org.bukkit.Location;

import com.sun.istack.internal.NotNull;

/** A tool to calculate the coordinates of an area. */
public class CalculateArea {
	
	protected final Location positive;
	protected final Location negative;
	
	/**
	 * A tool to calculate the coordinates of an area.
	 * @param first the first location object
	 * @param second the second location object
	 */
	public CalculateArea(@NotNull Location first, @NotNull Location second) {
		this.positive = new Location(first.getWorld(), 0, 0, 0);
		this.negative = new Location(first.getWorld(), 0, 0, 0);
		if (first.getX() >= second.getX()) {
			this.positive.setX(first.getX());
			this.negative.setX(second.getX());
		} else {
			this.positive.setX(second.getX());
			this.negative.setX(first.getX());
		}
		if (first.getY() >= second.getY()) {
			this.positive.setY(first.getY());
			this.negative.setY(second.getY());
		} else {
			this.positive.setY(second.getY());
			this.negative.setY(first.getY());
		}
		if (first.getZ() >= second.getZ()) {
			this.positive.setZ(first.getZ());
			this.negative.setZ(second.getZ());
		} else {
			this.positive.setZ(second.getZ());
			this.negative.setZ(first.getZ());
		}
	}
	
	/**
	 * Gets the positive location.
	 * @return the positive location
	 */
	public @NotNull Location getPositive() {
		return positive;
	}
	
	/**
	 * Gets the negative location.
	 * @return the negative location
	 */
	public @NotNull Location getNegative() {
		return negative;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof CalculateArea) {
			CalculateArea o = (CalculateArea) object;
			return positive.equals(o.positive) && negative.equals(o.negative);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 27 + positive.hashCode();
		hash *= 14 + negative.hashCode();
		return hash;
	}
	
}