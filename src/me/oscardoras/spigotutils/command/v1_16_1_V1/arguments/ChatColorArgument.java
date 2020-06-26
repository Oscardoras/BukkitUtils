package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;

import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;
import me.oscardoras.spigotutils.command.v1_16_1_V1.Reflector;

/** Represents a chat color argument for a Mojang Brigadier command. */
public class ChatColorArgument extends Argument<ChatColor> {
	
	/** Represents a chat color argument for a Mojang Brigadier command. */
	public ChatColorArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentChatFormat"));
	}
	
	@Override
	protected ChatColor parse(String key, CommandContext<?> context) throws Exception {
		Method getColor = Reflector.getMethod(Reflector.getObcClass("util.CraftChatMessage"), "getColor", Reflector.getNmsClass("EnumChatFormat"));
		Object enumChatFormat = Reflector.getMethod(Reflector.getNmsClass("ArgumentChatFormat"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (ChatColor) getColor.invoke(null, enumChatFormat);
	}
	
}