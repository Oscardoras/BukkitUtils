package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkitutils.command.v1_14_3_V1.CustomArgument;

/** Represents an offline player argument for a Mojang Brigadier command. */
public class OfflinePlayerArgument extends CustomArgument<OfflinePlayer> {
	
	/** Represents an offline player argument for a Mojang Brigadier command. */
	public OfflinePlayerArgument() {
		withSuggestionsProvider((cmd) -> {
			List<String> list = new ArrayList<String>();
			for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) list.add(offlinePlayer.getName());
			return list;
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	protected OfflinePlayer parse(String arg, SuggestedCommand cmd) {
		return Bukkit.getOfflinePlayer(arg);
	}
	
}