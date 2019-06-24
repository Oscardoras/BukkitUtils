package org.bukkitutils.io;

/** Represents a translatable message not found */
public class TranslatableMessageException extends IllegalArgumentException {

	private static final long serialVersionUID = -8022664399481137796L;
	
	/**
	 * Represents a translatable message not found
	 * @param path the path of the translatable message in translation files which was not found
	 */
	public TranslatableMessageException(String path) {
		super(path);
	}
	
}