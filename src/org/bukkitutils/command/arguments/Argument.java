package org.bukkitutils.command.arguments;

import org.bukkitutils.command.CommandPermission;

import com.mojang.brigadier.arguments.ArgumentType;

public interface Argument {
		
	/**
	 * Returns the brigadier equivalent argument type
	 */
	public <T> ArgumentType<T> getRawType();
	
	/**
	 * Returns the class of the primitive type that this enum represents
	 */
	public <V> Class<V> getPrimitiveType();
	
	/**
	 * Returns whether this argument is a "simple" argument. Simple arguments are primitive data types 
	 * @return Whether this argument is a simple argument
	 */
	public boolean isSimple();
	
	public <T extends Argument> T withPermission(CommandPermission permission);
	
	public CommandPermission getArgumentPermission();
		
}
