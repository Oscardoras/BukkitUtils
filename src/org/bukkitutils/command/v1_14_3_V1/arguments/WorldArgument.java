package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkitutils.command.v1_14_3_V1.CustomArgument;
import org.bukkitutils.io.TranslatableMessage;

import com.sun.istack.internal.NotNull;

/** Represents a world argument for a Mojang Brigadier command. */
public class WorldArgument extends CustomArgument<World> {
	
	protected final TranslatableMessage error;
	
	/**
	 * Represents a world argument for a Mojang Brigadier command.
	 * @param error an error to send if the world is not found
	 */
	public WorldArgument(@NotNull TranslatableMessage message) {
		this.error = message;
		withSuggestionsProvider((cmd) -> {
			List<String> list = new ArrayList<String>();
			for (World world : Bukkit.getWorlds()) list.add(world.getName());
			return list;
		});
	}
	
	
	/** Represents a world argument with an English error message for a Mojang Brigadier command. */
	public WorldArgument() {
		this(new TranslatableMessage(null, "world.does_not_exist") {
			public String getMessage(String language, String... args) {
				return "§cThe world '" + args[0] + "' was not found";
			}
		});
	}
	
	@Override
	protected World parse(String arg, SuggestedCommand cmd) throws CustomArgumentException {
		World world = Bukkit.getWorld(arg);
		if (world == null) throw new CustomArgumentException(error.getMessage(cmd.getLanguage(), arg));
		return world;
	}
	
}