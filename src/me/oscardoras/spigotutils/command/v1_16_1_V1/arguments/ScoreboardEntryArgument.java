package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;
import me.oscardoras.spigotutils.command.v1_16_1_V1.Reflector;

/** Represents a scoreboard entry argument for a Mojang Brigadier command. */
public class ScoreboardEntryArgument extends Argument<Object> {
	
	/** Represents an entry selector type. */
	public enum EntrySelector {
		/** A single entity. Returns an Entity. */
		ONE_ENTITY("a"),
		
		/** A single player. Returns a Player. */
		ONE_PLAYER("a"),
		
		/** Many entities. Returns a Collection of Entities. */
		MANY_ENTITIES("b"),
		
		/** Many players. Returns a Collection of Players. */
		MANY_PLAYERS("b");
		
		private String function;
		
		private EntrySelector(String function) {
			this.function = function;
		}
	}
	
	protected final EntrySelector selector;
	
	/**
	 * Represents a scoreboard entry argument for a Mojang Brigadier command.
	 * @param selector the entry selector type for this argument
	 */
	public ScoreboardEntryArgument(EntrySelector selector) {
		super(Reflector.getNmsArgumentInstance("ArgumentScoreholder", selector.function));

		this.selector = selector;
	}
	
	/**
	 * Gets the entity selector type for this argument.
	 * @return the entity selector type for this argument
	 */
	public EntrySelector getEntrySelector() {
		return selector;
	}
	
	@Override
	protected Object parse(String key, CommandContext<?> context) throws Exception {
		Class<?> clazz = Reflector.getNmsClass("ArgumentScoreholder");
		Collection<?> collection = (Collection<?>) Reflector.getMethod(clazz, "b", CommandContext.class, String.class).invoke(null, context, key);
		if (selector == EntrySelector.ONE_PLAYER || selector == EntrySelector.MANY_PLAYERS) {
			for (Iterator<?> it = collection.iterator(); it.hasNext();) {
				String entry = (String) it.next();
				try {
					UUID.fromString(entry);
					it.remove();
				} catch (IllegalArgumentException e) {}
			}
		}
		if (collection.isEmpty()) throw ((SimpleCommandExceptionType) Reflector.getField(clazz, "c").get(null)).create();
		if (selector.function.equals("a")) return collection.iterator().next();
		else return collection;
	}
	
}