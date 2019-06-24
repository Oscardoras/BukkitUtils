package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a chat color argument for a Mojang Brigadier command */
public class ChatColorArgument extends Argument<ChatColor> {
	
	/** Represents a chat color argument for a Mojang Brigadier command */
	public ChatColorArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentChatFormat"));
	}
	
	@Override
	public ChatColor getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Method getColor = Reflector.getMethod(Reflector.getObcClass("util.CraftChatMessage"), "getColor", Reflector.getNmsClass("EnumChatFormat"));
		Object enumChatFormat = Reflector.getMethod(Reflector.getNmsClass("ArgumentChatFormat"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (ChatColor) getColor.invoke(null, enumChatFormat);
	}
	
}