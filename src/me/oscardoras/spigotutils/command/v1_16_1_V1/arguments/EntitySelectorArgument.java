package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;
import me.oscardoras.spigotutils.command.v1_16_1_V1.Reflector;

/** Represents an entity selector argument for a Mojang Brigadier command. */
public class EntitySelectorArgument extends Argument<Object> {
	
	/** Represents an entity selector type. */
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
		
		private EntitySelector(String function) {
			this.function = function;
		}
	}
	
	protected final EntitySelector selector;
	
	/**
	 * Represents an entity selector argument for a Mojang Brigadier command.
	 * @param selector the entity selector type for this argument
	 */
	public EntitySelectorArgument(EntitySelector selector) {
		super(Reflector.getNmsArgumentInstance("ArgumentEntity", selector.function));
		
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
	protected Object parse(String key, CommandContext<?> context) throws Exception {
		Class<?> clazz = Reflector.getNmsClass("ArgumentEntity");
		switch(getEntitySelector()) {
			case MANY_ENTITIES:
			default:
				try {
					Collection<?> collectionOfEntities = (Collection<?>) Reflector.getMethod(clazz, "c", CommandContext.class, String.class).invoke(null, context, key);
					Collection<Entity> entities = new ArrayList<Entity>();
					for(Object nmsEntity : collectionOfEntities)
						entities.add((Entity) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(nmsEntity));
					if (entities.isEmpty()) throw ((SimpleCommandExceptionType) Reflector.getField(clazz, "d").get(null)).create();
					return entities;
				}
				catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
			case MANY_PLAYERS:
				try {
					Collection<?> collectionOfPlayers = (Collection<?>) Reflector.getMethod(clazz, "d", CommandContext.class, String.class).invoke(null, context, key);
					Collection<Player> players = new ArrayList<Player>();
					for(Object nmsPlayer : collectionOfPlayers)
						players.add((Player) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(nmsPlayer));
					if (players.isEmpty()) throw ((SimpleCommandExceptionType) Reflector.getField(clazz, "e").get(null)).create();
					return players;
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
			case ONE_ENTITY:
				try {
					Object entity = (Object) Reflector.getMethod(clazz, "a", CommandContext.class, String.class).invoke(null, context, key);
					return (Entity) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(entity);
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
			case ONE_PLAYER:
				try {
					Object player = (Object) Reflector.getMethod(clazz, "e", CommandContext.class, String.class).invoke(null, context, key);
					return (Player) Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(player);
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof CommandSyntaxException) throw (CommandSyntaxException) e.getCause();
				}
				break;
		}
		return null;
	}
	
}