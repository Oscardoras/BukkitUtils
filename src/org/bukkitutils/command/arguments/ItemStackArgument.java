package org.bukkitutils.command.arguments;

import org.bukkit.inventory.ItemStack;
import org.bukkitutils.command.CommandPermission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class ItemStackArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;

	/**
	 * An ItemStack argument. Always returns an itemstack of size 1
	 */
	public ItemStackArgument() {
		rawType = SemiReflector.getNMSArgumentInstance("ArgumentItemStack");
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) ItemStack.class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	private String[] suggestions;
	
	@Override
	public ItemStackArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private CommandPermission permission = CommandPermission.NONE;
	
	@Override
	public ItemStackArgument withPermission(CommandPermission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public CommandPermission getArgumentPermission() {
		return permission;
	}
}
