package org.bukkitutils.command.arguments;

import org.bukkit.Particle;
import org.bukkitutils.command.CommandPermission;
import org.bukkitutils.command.SemiReflector;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class ParticleArgument implements Argument, OverrideableSuggestions {

	ArgumentType<?> rawType;
	
	/**
	 * A Particle argument. Represents Minecraft particles
	 */
	public ParticleArgument() {
		rawType = SemiReflector.getNMSArgumentInstance("ArgumentParticle");
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) Particle.class;
	}

	@Override
	public boolean isSimple() {
		return false;
	}
	
	private String[] suggestions;
	
	@Override
	public ParticleArgument overrideSuggestions(String... suggestions) {
		this.suggestions = suggestions;
		return this;
	}
	
	@Override
	public String[] getOverriddenSuggestions() {
		return suggestions;
	}
	
	private CommandPermission permission = CommandPermission.NONE;
	
	@Override
	public ParticleArgument withPermission(CommandPermission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public CommandPermission getArgumentPermission() {
		return permission;
	}
}
