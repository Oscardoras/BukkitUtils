package org.bukkitutils.command.arguments;

import org.bukkit.permissions.Permission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

import net.md_5.bungee.api.chat.BaseComponent;


@SuppressWarnings("unchecked")
public class ChatComponentArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * A ChatComponent argument. Represents raw JSON text, used in Book MetaData, Chat and other various areas of Minecraft
	 * @see <a href="https://minecraft.gamepedia.com/Commands#Raw_JSON_text">Raw JSON text</a>
	 */
	public ChatComponentArgument() {
		rawType = SemiReflector.getNMSArgumentInstance("ArgumentChatComponent");
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) BaseComponent[].class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	private String[] suggestions;
	
	@Override
	public ChatComponentArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}

	private Permission permission = null;
	
	@Override
	public ChatComponentArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
