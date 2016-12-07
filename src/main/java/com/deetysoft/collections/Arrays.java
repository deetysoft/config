package com.deetysoft.collections;

/**
 * Array utilities.
 */
public class Arrays
{
	/**
	 * Create an Object Array of length 1 containing the given object.
	 *
	 * @param	obj	the single element
	 * @return			the array
	 */
	public static Object[] create (Object obj)
	{
		Object[] objs = new Object [1];
		objs [0] = obj;
		return objs;
	}

	/**
	 * Create an String Array of length 1 containing the given String.
	 *
	 * @param	str	the single element
	 * @return			the array
	 */
	public static String[] create (String str)
	{
		String[] objs = new String [1];
		objs [0] = str;
		return objs;
	}

	/**
	 * Create an String Array of length 2 containing the given Strings.
	 *
	 * @param	str1	the first element
	 * @param	str2	the second element
	 * @return			the array
	 */
	public static String[] create (String str1, String str2)
	{
		String[] objs = new String [2];
		objs [0] = str1;
		objs [1] = str2;
		return objs;
	}
}
