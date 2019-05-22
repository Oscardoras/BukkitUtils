package org.bukkitutils.command.arguments;

import org.bukkit.permissions.Permission;
import org.bukkitutils.command.exceptions.InvalidRangeException;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

@SuppressWarnings("unchecked")
public class IntegerArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * An integer argument
	 */
	public IntegerArgument() {
		rawType = IntegerArgumentType.integer();
	}
	
	/**
	 * An integer argument with a minimum value
	 * @param min The minimum value this argument can take (inclusive)
	 */
	public IntegerArgument(int min) {
		rawType = IntegerArgumentType.integer(min);
	}
	
	/**
	 * An integer argument with a minimum and maximum value
	 * @param min The minimum value this argument can take (inclusive)
	 * @param max The maximum value this argument can take (inclusive)
	 */
	public IntegerArgument(int min, int max) {
		if(max < min) {
			throw new InvalidRangeException();
		}
		rawType = IntegerArgumentType.integer(min, max);
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) int.class;
	}
	
	@Override
	public boolean isSimple() {
		return true;
	}

	private String[] suggestions;
	
	@Override
	public IntegerArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private Permission permission = null;
	
	@Override
	public IntegerArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
	
}
