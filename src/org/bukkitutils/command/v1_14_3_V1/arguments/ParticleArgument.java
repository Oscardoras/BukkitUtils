package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkitutils.command.v1_14_3_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a particle argument for a Mojang Brigadier command */
public class ParticleArgument extends Argument<Particle> {
	
	/** Represents a particle argument for a Mojang Brigadier command */
	public ParticleArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentParticle"));
	}
	
	@Override
	public Particle getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Method toBukkit = Reflector.getMethod(Reflector.getObcClass("CraftParticle"), "toBukkit", Reflector.getNmsClass("ParticleParam"));
		Object particleParam = Reflector.getMethod(Reflector.getNmsClass("ArgumentParticle"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Particle) toBukkit.invoke(null, particleParam);
	}
	
}