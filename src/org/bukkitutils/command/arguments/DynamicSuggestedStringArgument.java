package org.bukkitutils.command.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

@SuppressWarnings("unchecked")
public class DynamicSuggestedStringArgument implements Argument {
	
	@FunctionalInterface
	public interface DynamicSuggestionsWithCommandSender {
		String[] getSuggestions(CommandSender sender);
	}
	
	@FunctionalInterface
	public interface DynamicSuggestions {
		String[] getSuggestions();
	} 
	
	ArgumentType<?> rawType;
	private DynamicSuggestions suggestions;
	private DynamicSuggestionsWithCommandSender suggestionsWithCS;
	
	/**
	 * A string argument which has suggestions which are determined at runtime
	 */
	public DynamicSuggestedStringArgument(DynamicSuggestions suggestions) {
		rawType = StringArgumentType.word();
		this.suggestions = suggestions;
		this.suggestionsWithCS = null;
	}
	
	/**
	 * A string argument which has suggestions which are determined at runtime
	 */
	public DynamicSuggestedStringArgument(DynamicSuggestionsWithCommandSender suggestions) {
		rawType = StringArgumentType.word();
		this.suggestionsWithCS = suggestions;
		this.suggestions = null;
	}
		
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) String.class;
	}

	@Override
	public boolean isSimple() {
		return true;
	}
	
	public DynamicSuggestions getDynamicSuggestions() {
		return suggestions;
	}
	
	public DynamicSuggestionsWithCommandSender getDynamicSuggestionsWithCommandSender() {
		return suggestionsWithCS;
	}
	
	private Permission permission = null;
	
	@Override
	public DynamicSuggestedStringArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
