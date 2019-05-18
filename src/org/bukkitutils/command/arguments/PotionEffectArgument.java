package org.bukkitutils.command.arguments;

import org.bukkit.potion.PotionEffectType;
import org.bukkitutils.command.CommandPermission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class PotionEffectArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * A PotionEffect argument. Represents status/potion effects 
	 */
	public PotionEffectArgument() {
		rawType = SemiReflector.getNMSArgumentInstance("ArgumentMobEffect");
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) PotionEffectType.class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	private String[] suggestions;
	
	@Override
	public PotionEffectArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private CommandPermission permission = CommandPermission.NONE;
	
	@Override
	public PotionEffectArgument withPermission(CommandPermission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public CommandPermission getArgumentPermission() {
		return permission;
	}
}
