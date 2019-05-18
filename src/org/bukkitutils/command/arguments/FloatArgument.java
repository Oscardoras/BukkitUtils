package org.bukkitutils.command.arguments;

import org.bukkitutils.command.CommandPermission;
import org.bukkitutils.command.exceptions.InvalidRangeException;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;

@SuppressWarnings("unchecked")
public class FloatArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * An float argument
	 */
	public FloatArgument() {
		rawType = FloatArgumentType.floatArg();
	}
	
	/**
	 * A float argument with a minimum value
	 * @param min The minimum value this argument can take (inclusive)
	 */
	public FloatArgument(int min) {
		rawType = FloatArgumentType.floatArg(min);
	}
	
	/**
	 * A float argument with a minimum and maximum value
	 * @param min The minimum value this argument can take (inclusive)
	 * @param max The maximum value this argument can take (inclusive)
	 */
	public FloatArgument(int min, int max) {
		if(max < min) {
			throw new InvalidRangeException();
		}
		rawType = FloatArgumentType.floatArg(min, max);
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) float.class;
	}

	@Override
	public boolean isSimple() {
		return true;
	}
	
	private String[] suggestions;
	
	@Override
	public FloatArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private CommandPermission permission = CommandPermission.NONE;
	
	@Override
	public FloatArgument withPermission(CommandPermission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public CommandPermission getArgumentPermission() {
		return permission;
	}
}
