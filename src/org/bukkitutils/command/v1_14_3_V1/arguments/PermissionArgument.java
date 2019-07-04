package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_14_3_V1.CustomArgument;

/** Represents a permission argument for a Mojang Brigadier command */
public class PermissionArgument extends CustomArgument<Permission> {
	
	/** Represents a permission argument for a Mojang Brigadier command */
	public PermissionArgument() {
		super(null);
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