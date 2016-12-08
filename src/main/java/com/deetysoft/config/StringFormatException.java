package com.deetysoft.config;

/**
 * A string has invalid format.
 */
@SuppressWarnings("serial")
public class StringFormatException extends Exception
{
	/**
	 * Construct using the given message.
	 * 
	 * @param	string	the message
	 */
	public StringFormatException (String string)
	{
		super (string);
	}
}
