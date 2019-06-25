package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/** Represents a permission argument for a Mojang Brigadier command */
public class PermissionArgument extends CustomArgument<Permission> {
	
	/** Represents a permission argument for a Mojang Brigadier command */
	public PermissionArgument() {
		super(null);
	}
	@Override
	public Collection<String> getSuggestions(CommandSender executor, Location location, Object[] args) {
		List<String> list = new ArrayList<String>();
		for (Permission permission : Bukkit.getPluginManager().getPermissions()) list.add(permission.getName());
		return list;
	}
	
	@Override
	public Permission getArg(String arg, CommandSender executor, Location location) {
		return new Permission(arg);
	}
	
}