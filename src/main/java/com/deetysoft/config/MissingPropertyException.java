package com.deetysoft.config;

/**
 * A property was not found.
 */
@SuppressWarnings("serial")
public class MissingPropertyException extends Exception
{
	/**
	 * Construct using the given message, the default key and null args.
	 * 
	 * @param	string	the message for the exception
	 */
	public MissingPropertyException (String string)
	{
		super (string);
	}
}
