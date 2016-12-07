package com.deetysoft.util;

import org.apache.maven.shared.utils.StringUtils;

/**
 * String utilities.
 * @author charles.young
 *
 */
public class Strings {

	/**
	 * Token for new line.
	 * We can use System.lineSeparator() to get the platform-specific value if we need too.
	 */
	public static final String NL = "\n";

	/**
	 * Pad a string to a fixed length string with space added to right.
	 * @param string	the string to pad
	 * @param length	the number of spaces to add
	 * @return			the padded string
	 */
	public static String rightPad(String string, int length){
		return StringUtils.rightPad(string,length);
	}

	/**
	 * Pad a string to a fixed length string with space added to left.
	 * @param string	the string to pad
	 * @param length	the number of spaces to add
	 * @return			the padded string
	 */
	public static String leftPad(String string, int length){
		return StringUtils.leftPad(string,length);
	}

	/**
	 * Pad a string to a fixed length string with space added to left.
	 * @param string	the string to pad
	 * @param length	the number of spaces to add
	 * @param delimiter	the delimiter
	 * @return			the padded string
	 */
	public static String leftPad(String string, int length, String delimiter){
		return StringUtils.leftPad(string,length,delimiter);
	}
}
