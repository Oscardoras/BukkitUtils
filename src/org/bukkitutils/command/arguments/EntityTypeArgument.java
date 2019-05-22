package org.bukkitutils.command.arguments;

import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class EntityTypeArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * An EntityType argument. Represents the type of an Entity
	 */
	public EntityTypeArgument() {
		rawType = SemiReflector.getNMSArgumentInstance("ArgumentEntitySummon");
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) EntityType.class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	private String[] suggestions;
	
	@Override
	public EntityTypeArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private Permission permission = null;
	
	@Override
	public EntityTypeArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
