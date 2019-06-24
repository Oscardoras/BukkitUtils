package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/** Represents an enity selector argument for a Mojang Brigadier command */
public class EntitySelectorArgument extends Argument<Object> {
	
	/** Represents an an entity selector type */
	public enum EntitySelector {
		/** A single entity. Returns an Entity. */
		ONE_ENTITY("a"),
		
		/** A single player. Returns a Player. */
		ONE_PLAYER("c"),
		
		/** Many entities. Returns a Collection of Entities. */
		MANY_ENTITIES("multipleEntities"),
		
		/** Many players. Returns a Collection of Players. */
		MANY_PLAYERS("d");
		
		private String function;
		
		private EntitySelector(String nmsFunction) {
			this.function = nmsFunction;
		}
		
		private String getNmsFunction() {
			return function;
		}
	}
	
	protected final EntitySelector selector;
	
	/**
	 * Represents an enity selector argument for a Mojang Brigadier command
	 * @param selector the entity selector type for this argument
	 */
	public EntitySelectorArgument(EntitySelector selector) {
		super(Reflector.getNmsArgumentInstance("ArgumentEntity", selector.getNmsFunction()));
		
		this.selector = selector;
	}
	
	/**
	 * Gets the entity selector type for this argument.
	 * @return the entity selector type for this argument
	 */
	public EntitySelector getEntitySelector() {
		return selector;
	}
	
	@Override
	public Object getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		switch(getEntitySelector()) {
			case MANY_ENTITIES:
			default:
				try {
					Collection<?> collectionOfEntities = (Collection<?>) Reflector.getMethod(Reflector.getNmsClass("ArgumentEntity"), "c", CommandContext.class, String.class).invoke(null, context, key);
					Collection<Entity> entities = new ArrayList<>();
					for(Object nmsEntity : collectionOfEntities) {
						entities.add((Entity) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(nmsEntity));
					}
					return entities;
				}
				catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) return (Collection<Entity>) new ArrayList<Entity>();
				}
				break;
			case MANY_PLAYERS:
				try {
					Collection<?> collectionOfPlayers = (Collection<?>) Reflector.getMethod(Reflector.getNmsClass("ArgumentEntity"), "d", CommandContext.class, String.class).invoke(null, context, key);
					Collection<Player> players = new ArrayList<>();
					for(Object nmsPlayer : collectionOfPlayers) {
						players.add((Player) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(nmsPlayer));
					}
					return players;
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) return (Collection<Player>) new ArrayList<Player>();
				}
				break;
			case ONE_ENTITY:
				try {
					Object entity = (Object) Reflector.getMethod(Reflector.getNmsClass("ArgumentEntity"), "a", CommandContext.class, String.class).invoke(null, context, key);
					return (Entity) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(entity);
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
			case ONE_PLAYER:
				try {
					Object player = (Object) Reflector.getMethod(Reflector.getNmsClass("ArgumentEntity"), "e", CommandContext.class, String.class).invoke(null, context, key);
					return (Player) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(player);
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
		}
		return null;
	}
	
}