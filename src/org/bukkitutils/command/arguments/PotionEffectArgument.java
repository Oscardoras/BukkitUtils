package org.bukkitutils.command.arguments;

import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffectType;
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
	
	private Permission permission = null;
	
	@Override
	public PotionEffectArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
