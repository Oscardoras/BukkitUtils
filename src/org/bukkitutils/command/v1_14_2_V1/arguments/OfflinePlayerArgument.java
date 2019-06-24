package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/** Represents an offline player argument for a Mojang Brigadier command */
public class OfflinePlayerArgument extends CustomArgument<OfflinePlayer> {
	
	/** Represents an offline player argument for a Mojang Brigadier command */
	public OfflinePlayerArgument() {
		super(null);
	}

	@Override
	public Collection<String> getSuggestions(CommandSender executor, Location location, Object[] args) {
		List<String> list = new ArrayList<String>();
		for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) list.add(offlinePlayer.getName());
		return list;
	}

	@SuppressWarnings("deprecation")
	@Override
	public OfflinePlayer getArg(String arg, CommandSender executor, Location location) {
		return Bukkit.getOfflinePlayer(arg);
	}
	
}