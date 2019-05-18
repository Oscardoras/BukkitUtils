package org.bukkitutils.io;

public class MessageException extends IllegalArgumentException {

	private static final long serialVersionUID = -8022664399481137796L;
	
	public MessageException(String path) {
		super(path);
	}
	
}