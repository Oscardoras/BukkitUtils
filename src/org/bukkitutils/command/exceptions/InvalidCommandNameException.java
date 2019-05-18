package org.bukkitutils.command.exceptions;

@SuppressWarnings("serial")
public class InvalidCommandNameException extends RuntimeException {
	
	private String commandName;
	
	public InvalidCommandNameException(String commandName) {
		this.commandName = commandName;
	}
	
	@Override
    public String getMessage() {
		return "Invalid command with name '" + commandName + "' cannot be registered!";
    }
	
}
