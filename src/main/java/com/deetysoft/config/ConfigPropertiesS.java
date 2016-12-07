package com.deetysoft.config;

import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * A singleton wrapping a {@link ConfigProperties}.
 * ConfigPropertiesS is sufficient for many applications.
 * Reusable components, APIs etc. should use a ConfigProperties of their own to
 * avoid conflict.
 * 
 * @see ConfigProperties
 */
public class ConfigPropertiesS
{
	// The ConfigProperties
	protected static ConfigProperties	configProperties;

	/**
	 * Create the single ConfigProperties.
	 */
	static
	{
		Logger log = Logger.getLogger(ConfigProperties.class);
		log.debug("In static initializer.");

		try
		{
			log.debug("Constructing singleton.");
			configProperties = new ConfigProperties ();
			Iterator<String> properties = configProperties.getKeySet ();
			if (!properties.hasNext ())
			{
				log.debug ("Singleton has no properties defined.");
			}
		}
		catch (Exception e)
		{
			log.debug ("Exception creating singleton :\n" + e);
		}
		log.debug("leaving static initializer.");
	}

	/**
	 * The default constructor.
	 */
	private ConfigPropertiesS ()
	{
	}

	/**
	 * Initialize the {@link ConfigProperties#init(String[]) ConfigProperties}.
	 * @param fileNames		property file names
	 * @throws IOException	trying to read a property file
	 */
	public static void init (String[] fileNames) throws IOException {
		configProperties.init(fileNames);
	}

	/**
	 * Static version of {@link ConfigProperties#dumpProperties}.
	 */
	public static void dump ()
	{
		configProperties.dump ();
	}

	/**
	 * Static version of {@link ConfigProperties#get(String) get}.
	 * @param	name	the name of the property
	 * @return			the property value
	 * @exception		MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		StringFormatException
	 *					if property value has unmatched substitution
	 *					delimiters
	 */
	public static String get (String name)
		throws MissingPropertyException, StringFormatException
	{
		return configProperties.get (name);
	}

	/**
	 * Static version of {@link ConfigProperties#get(String, String[]) get}.
	 * @param	name	the property name
	 * @param	args	the run-time args
	 * @return			the value
	 * @exception		MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		StringFormatException
	 *					if property value has unmatched substitution
	 *					delimiters
	 */
	public static String get (String name, String[] args)
		throws MissingPropertyException, StringFormatException
	{
		return configProperties.get (name, args);
	}


	/**
	 * Static version of {@link ConfigProperties#getPropertyUnexpanded getPropertyUnexpanded}.
	 * @param	name	the property name
	 * @return			the unexpanded property value
	 */
	public static String	getUnexpanded (String name)
		throws MissingPropertyException
	{
		return configProperties.getUnexpanded (name);
	}

	/**
	 * Static version of {@link ConfigProperties#getProperties getProperties}.
	 * @return		an iterator over strings
	 */
	public static Iterator<String> getKeySet ()
	{
		return configProperties.getKeySet();
	}

	/**
	 * Get the ConfigProperties.
	 * @return	the configProperties
	 */
	public static ConfigProperties getConfigProperties () {
		return configProperties;
	}

	/**
	 * Set the ConfigProperties.
	 *
	 * @param	instance_	the new ConfigProperties
	 */
	public static void setConfigProperties (ConfigProperties instance_)
	{
		configProperties = instance_;
	}
}
