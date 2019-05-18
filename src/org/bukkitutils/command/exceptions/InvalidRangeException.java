package org.bukkitutils.command.exceptions;

@SuppressWarnings("serial")
public class InvalidRangeException extends RuntimeException {
	
	@Override
    public String getMessage() {
		return "Cannot have a maximum value smaller than a minimum value";
    }
	
}
