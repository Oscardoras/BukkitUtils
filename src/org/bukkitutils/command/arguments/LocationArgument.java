package org.bukkitutils.command.arguments;

import org.bukkit.Location;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class LocationArgument implements Argument, OverrideableSuggestions {

	public enum LocationType {
		/**
		 * Represents the integer coordinates of a block, for example: (10, 70, -19)
		 */
		BLOCK_POSITION, 
		
		/**
		 * Represents the exact coordinates of a position, for example: (10.24, 70.00, -18.79)
		 */
		PRECISE_POSITION;
	}
	
	ArgumentType<?> rawType;
	
	/**
	 * A Location argument. Represents Minecraft locations
	 */
	public LocationArgument() {
		this(LocationType.PRECISE_POSITION);
	}
	
	/**
	 * A Location argument. Represents Minecraft locations
	 */
	public LocationArgument(LocationType type) {
		locationType = type;
		switch(type) {
			case BLOCK_POSITION:
				rawType = SemiReflector.getNMSArgumentInstance("ArgumentPosition");
				break;
			case PRECISE_POSITION:
				rawType = SemiReflector.getNMSArgumentInstance("ArgumentVec3");
				break;
		}
	}
	
	private final LocationType locationType;
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) Location.class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	public LocationType getLocationType() {
		return locationType;
	}
	
	private String[] suggestions;
	
	@Override
	public LocationArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private Permission permission = null;
	
	@Override
	public LocationArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
