package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import me.oscardoras.spigotutils.command.v1_16_1_V1.CustomArgument;

/** Represents a permission argument for a Mojang Brigadier command. */
public class PermissionArgument extends CustomArgument<Permission> {
	
	/** Represents a permission argument for a Mojang Brigadier command. */
	public PermissionArgument() {
		withSuggestionsProvider((cmd) -> {
			List<String> list = new ArrayList<String>();
			for (Permission permission : Bukkit.getPluginManager().getPermissions()) list.add(permission.getName());
			return list;
		});
	}
	
	@Override
	protected Permission parse(String arg, SuggestedCommand cmd) {
		return new Permission(arg);
	}
	
}