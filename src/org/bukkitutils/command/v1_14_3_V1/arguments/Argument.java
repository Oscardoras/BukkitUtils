package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/** Represents an argument for a Mojang Brigadier command */
public abstract class Argument<T> {
	
	private ArgumentType<?> rawType;
	private Permission permission = null;
	
	/**
	 * Represents an argument for a Mojang Brigadier command
	 * @param rawType the raw type for this argument
	 */
	protected Argument(ArgumentType<?> rawType) {
		this.rawType = rawType;
	}
	
	/**
	 * Gets the raw type for this argument.
	 * @return the raw type for this argument
	 */
	@SuppressWarnings("unchecked")
	public final <R> ArgumentType<R> getRawType() {
		return (ArgumentType<R>) rawType;
	}
	
	/**
	 * The suggestions provider.
	 * @param executor the CommandSender object representing the executor of this command
	 * @param location the location where this command is performed
	 * @param args the arguments given to this command
	 */
	public Collection<String> getSuggestions(CommandSender executor, Location location, Object[] args) {
		return null;
	}
	
	/**
	 * Sets a permission for this argument.
	 * @param permission the permission to set
	 * @return this argument
	 * @throws Exception if an exception occure
	 */
	public final Argument<T> withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}
	
	/**
	 * Gets the permission for this argument.
	 * @return the permission for this argument
	 */
	public final Permission getPermission() {
		return permission;
	}
	
	/**
	 * The argument parser.
	 * @param key the key of the command in the argument map
	 * @param context the command context of this command
	 * @param executor the CommandSender object representing the executor of this command
	 * @param location the location where this command is performed
	 * @throws CommandSyntaxException if the command is malformed
	 * @throws Exception if an exception occure
	 */
	public abstract T getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception;
	
}