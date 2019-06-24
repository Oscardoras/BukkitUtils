package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a potion effect argument for a Mojang Brigadier command */
public class PotionEffectArgument extends Argument<PotionEffectType> {
	
	/** Represents a potion effect argument for a Mojang Brigadier command */
	public PotionEffectArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentMobEffect"));
	}
	
	@Override
	public PotionEffectType getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Constructor<?> craftPotionType = Reflector.getObcClass("potion.CraftPotionEffectType").getConstructor(Reflector.getNmsClass("MobEffectList"));
		Object mobEffect = Reflector.getMethod(Reflector.getNmsClass("ArgumentMobEffect"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (PotionEffectType) craftPotionType.newInstance(mobEffect);
	}
	
}
