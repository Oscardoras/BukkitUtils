package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.enchantments.Enchantment;

import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;
import me.oscardoras.spigotutils.command.v1_16_1_V1.Reflector;

/** Represents an enchantment argument for a Mojang Brigadier command. */
public class EnchantmentArgument extends Argument<Enchantment> {
	
	/** Represents an enchantment argument for a Mojang Brigadier command. */
	public EnchantmentArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentEnchantment"));
	}
	
	@Override
	protected Enchantment parse(String key, CommandContext<?> context) throws Exception {
		Constructor<?> craftEnchant = Reflector.getObcClass("enchantments.CraftEnchantment").getConstructor(Reflector.getNmsClass("Enchantment"));
		Object nmsEnchantment = Reflector.getMethod(Reflector.getNmsClass("ArgumentEnchantment"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Enchantment) craftEnchant.newInstance(nmsEnchantment);
	}
	
}