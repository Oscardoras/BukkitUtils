package org.bukkitutils.command.arguments;

import org.bukkit.permissions.Permission;
import org.bukkitutils.command.exceptions.BadLiteralException;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unchecked")
public class LiteralArgument implements Argument {

	String literal;
	
	/**
	 * A literal argument. Only takes one string value which cannot be modified 
	 */
	public LiteralArgument(final String literal) {
		if(literal == null) {
			throw new BadLiteralException(true);
		}
		if(literal.isEmpty()) {
			throw new BadLiteralException(false);
		}
		this.literal = literal;
	}
	
	@Override
	public <T> ArgumentType<T> getRawType() {
		/*
		 * The literal argument builder is NOT technically an argument.
		 * Therefore, it doesn't have an ArgumentType.
		 * 
		 * This is a wrapper for the object "LiteralArgumentBuilder<>"
		 */
		return null;
	}

	@Override
	public <V> Class<V> getPrimitiveType() {
		return (Class<V>) String.class;
	}

	public String getLiteral() {
		return literal;
	}
	
	@Override
	public boolean isSimple() {
		return false;
	}
	
	private Permission permission = null;
	
	@Override
	public LiteralArgument withPermission(Permission permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public Permission getArgumentPermission() {
		return permission;
	}
}
