package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.lang.reflect.Method;

import org.bukkitutils.command.v1_14_3_V1.Argument;
import org.bukkitutils.command.v1_14_3_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Represents a chat component argument for a Mojang Brigadier command. */
public class ChatComponentArgument extends Argument<BaseComponent[]> {
	
	/** Represents a chat component argument for a Mojang Brigadier command. */
	public ChatComponentArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentChatComponent"));
	}
	
	@Override
	protected BaseComponent[] parse(String key, CommandContext<?> context) throws Exception {
		Class<?> chatSerializer = Reflector.getNmsClass("IChatBaseComponent$ChatSerializer");
		Method m = Reflector.getMethod(chatSerializer, "a", Reflector.getNmsClass("IChatBaseComponent"));
		Object iChatBaseComponent = Reflector.getMethod(Reflector.getNmsClass("ArgumentChatComponent"), "a", CommandContext.class, String.class).invoke(null, context, key);
		Object resultantString = m.invoke(null, iChatBaseComponent);
		//Convert to spigot thing
		BaseComponent[] components = ComponentSerializer.parse((String) resultantString);
		return (BaseComponent[]) components;
	}
	
}