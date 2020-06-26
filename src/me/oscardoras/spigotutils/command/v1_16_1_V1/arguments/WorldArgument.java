package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.oscardoras.spigotutils.command.v1_16_1_V1.CustomArgument;
import me.oscardoras.spigotutils.io.TranslatableMessage;

/** Represents a world argument for a Mojang Brigadier command. */
public class WorldArgument extends CustomArgument<World> {
	
	protected final TranslatableMessage error;
	
	/**
	 * Represents a world argument for a Mojang Brigadier command.
	 * @param error an error to send if the world is not found
	 */
	public WorldArgument(TranslatableMessage message) {
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
				return "The world '" + args[0] + "' was not found";
			}
		});
	}
	
	@Override
	protected World parse(String arg, SuggestedCommand cmd) throws CommandSyntaxException {
		World world = Bukkit.getWorld(arg);
		if (world == null) throw getCustomException(error.getMessage(cmd.getLanguage(), arg));
		return world;
	}
	
}