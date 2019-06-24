package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents an item stack argument for a Mojang Brigadier command */
public class ItemStackArgument extends Argument<ItemStack> {
	
	/** Represents an item stack argument for a Mojang Brigadier command */
	public ItemStackArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentItemStack"));
	}
	
	@Override
	public ItemStack getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Method asBukkitCopy = Reflector.getMethod(Reflector.getObcClass("inventory.CraftItemStack"), "asBukkitCopy", Reflector.getNmsClass("ItemStack"));
		Object argumentIS = Reflector.getMethod(Reflector.getNmsClass("ArgumentItemStack"), "a", CommandContext.class, String.class).invoke(null, context, key);
		Object nmsIS = Reflector.getMethod(argumentIS.getClass(), "a", int.class, boolean.class).invoke(argumentIS, 1, false);
		return (ItemStack) asBukkitCopy.invoke(null, nmsIS);
	}
	
}