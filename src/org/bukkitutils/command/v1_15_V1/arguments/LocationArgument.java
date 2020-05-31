package org.bukkitutils.command.v1_15_V1.arguments;

import org.bukkit.Location;
import org.bukkitutils.command.v1_15_V1.Argument;
import org.bukkitutils.command.v1_15_V1.Reflector;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sun.istack.internal.NotNull;

/** Represents a location argument for a Mojang Brigadier command. */
public class LocationArgument extends Argument<Location> {

	/** Represents an location selector type. */
	public enum LocationType {
		/** Represents the integer coordinates of a block. */
		BLOCK_LOCATION, 
		
		/** Represents the exact coordinates of a position. */
		PRECISE_LOCATION;
	}
	
	/** Represents a precise location argument for a Mojang Brigadier command. */
	public LocationArgument() {
		this(LocationType.PRECISE_LOCATION);
	}
	
	/**
	 * Represents a location argument for a Mojang Brigadier command.
	 * @param type the location type
	 */
	public LocationArgument(@NotNull LocationType type) {
		super(new Object() {
			public ArgumentType<?> getRawType() {
				switch(type) {
				case BLOCK_LOCATION:
					return Reflector.getNmsArgumentInstance("ArgumentPosition");
				case PRECISE_LOCATION:
					return Reflector.getNmsArgumentInstance("ArgumentVec3");
				}
				return null;
			}
		}.getRawType());
		locationType = type;
	}
	
	protected final LocationType locationType;
	
	/**
	 * Gets the location type for this argument.
	 * @return the location type for this argument
	 */
	public @NotNull LocationType getLocationType() {
		return locationType;
	}
	
	@Override
	public Location parse(String key, CommandContext<?> context) throws Exception {
		switch(getLocationType()) {
			case BLOCK_LOCATION:
				{
					Object blockPosition = Reflector.getMethod(Reflector.getNmsClass("ArgumentPosition"), "a", CommandContext.class, String.class).invoke(null, context, key);
					int x = (int) Reflector.getMethod(Reflector.getNmsClass("BaseBlockPosition"), "getX").invoke(blockPosition);
					int y = (int) Reflector.getMethod(Reflector.getNmsClass("BaseBlockPosition"), "getY").invoke(blockPosition);
					int z = (int) Reflector.getMethod(Reflector.getNmsClass("BaseBlockPosition"), "getZ").invoke(blockPosition);
					return new Location(Reflector.getCommandLocation(context.getSource()).getWorld(), x, y, z);
				}
			case PRECISE_LOCATION:
				{
					Object vec3D = Reflector.getMethod(Reflector.getNmsClass("ArgumentVec3"), "a", CommandContext.class, String.class).invoke(null, context, key);
					double x = Reflector.getField(vec3D.getClass(), "x").getDouble(vec3D);
					double y = Reflector.getField(vec3D.getClass(), "y").getDouble(vec3D);
					double z = Reflector.getField(vec3D.getClass(),"z").getDouble(vec3D);
					return new Location(Reflector.getCommandLocation(context.getSource()).getWorld(), x, y, z);
				}
		}
		return null;
	}
	
}