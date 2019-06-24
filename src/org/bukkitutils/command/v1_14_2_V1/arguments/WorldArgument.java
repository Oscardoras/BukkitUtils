package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkitutils.io.TranslatableMessage;

/** Represents a world argument for a Mojang Brigadier command */
public class WorldArgument extends CustomArgument<World> {
	
	/** Represents a world argument with an english error message for a Mojang Brigadier command */
	public WorldArgument() {
		this(new TranslatableMessage(null, "world.does_not_exist") {
			public String getMessage(String language, String... args) {
				return "§cThe world '" + args[0] + "' was not found";
			}
		});
	}
	
	/**
	 * Represents a world argument for a Mojang Brigadier command
	 * @param error an error to send if the world is not found. Set null for don't send an error message
	 */
	public WorldArgument(TranslatableMessage error) {
		super(error);
	}

	@Override
	public Collection<String> getSuggestions(CommandSender executor, Location location, Object[] args) {
		List<String> list = new ArrayList<String>();
		for (World world : Bukkit.getWorlds()) list.add(world.getName());
		return list;
	}
	
	@Override
	public World getArg(String arg, CommandSender executor, Location location) {
		return Bukkit.getWorld(arg);
	}
	
}