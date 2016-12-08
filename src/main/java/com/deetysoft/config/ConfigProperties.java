package com.deetysoft.config;

import com.deetysoft.util.TokenIndexRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * A configuration properties utility.
 * ConfigProperties reads properties from one or more
 * {@link java.util.Properties} files.
 * It merges properties from multiple files to form a single set.
 * <p>
 * A property value may refer to another property.
 * The value of the dependent property is substituted into the property value
 * when the property value is requested (value expansion.)
 * The property to be substituted is defined by a beginning and ending '%'
 * surrounding a property name.
 * For example:
 * <p>
 * p1=blee
 * <br>
 * p2=p1 value is %p1%.
 * <p>
 * In this case, p2's value after substitution is "p1 value is blee."
 * Substitutions may be nested to any level.
 * If the name of the property to be substituted begins with 'env.',
 * ConfigProperties looks for a system property to substitute.
 * See {@link #ENV_PREFIX ENV_PREFIX} for details.
 * <p>
 * ConfigProperties also supports arguments when requesting a property value.
 * For example, when the property:
 * <p>
 * msg =The hostname is {0} and port is {1}.
 * <p>
 * is requested using the call {@link #get(String,String[]) get("msg",args)},
 * where args = {"betty", 1776}, the returned value is 'The hostname is betty and port is 1776.'
 *
 * @see java.util.Properties
 */
public class ConfigProperties
{
	/**
     * The class defining property file names -
     * 'com.deetysoft.config.ConfigPropertiesFiles.'
	 * ConfigPropertiesFiles must define a method
	 * <pre>
	 * public static String[] getFileNames ()
	 * </pre>
	 * to provide the file name list.
     */
	public static final String	FILES_CLASS	=
		"com.deetysoft.config.ConfigPropertiesFiles";

	/**
	 * The default properties file - "config.properties".
	 */
	public static final String	DEFAULT_FILE	= "config.properties";

	/**
	 * The prefix 'env.' for substitution strings denotes a system property.
	 * For example: home=%env.HOME% sets the property named 'home'
	 * to have the value of the HOME system property.
	 */
	public static final String	ENV_PREFIX			= "env.";

	/**
	 * The system property for defining the file list - "config-properties.files".
	 * The value is a ';' separated list of files.
	 * For example when using the -D option to start the vm:
	 * <p>
	 * -Dconfig-properties.files=f1.properties;f2.properties
	 */
	public static final String	FILES_PROPERTY		= 
		"config-properties.files";

	/**
	 * The token used to delimit a substitution -'%'.
	 */
	public static final char	SUBSTITUTION_TOKEN	= '%';

	// The default property file list.
	protected static String[]		defaultList		= {DEFAULT_FILE};

	// The merged properties
	protected HashMap<String, String>	properties	= new HashMap<String, String> ();

	protected Logger log = null;

	/**
	 * The default constructor.
	 * <p>
	 * Look for property files using the following rules:
	 * <p>
	 * 1. If the system property {@link #FILES_PROPERTY} exists, assume 
	 * it specifies a property file list.
	 * <p>
	 * 2. Try to instantiate by name the class
	 * {@link #FILES_CLASS}.
	 * It is not part of the distributed package but may be defined by the
	 * application as an alternative to using the system
	 * property or default file. 
	 * <p>
	 * 3. If neither FILES_PROPERTY or FILES_CLASS is 
	 * defined then try to load
	 * the default properties file {@link #DEFAULT_FILE}.
	 * <p>
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the specified order.
	 *
	 * @exception	IOException			trying to read a property file
	 * @exception	SecurityException	from security manager on accessing
	 *									system property
	 */
	public ConfigProperties () throws IOException, SecurityException
	{
		log = Logger.getLogger(this.getClass());
		log.debug("In default constructor.");

		// If the system property defining the file names exists

		String fileNames = System.getProperty (FILES_PROPERTY);

		if (fileNames != null)
		{
			log.debug("Constructing from system property '"+FILES_PROPERTY+
				"' with files :\n'"+fileNames+"'.");
/*
			// Use the file names.
			Vector<String> v = new Vector<String> ();

			StringTokenizer tokenizer = new StringTokenizer (fileNames,";");

			while (tokenizer.hasMoreTokens ())
			{
				String fileName = tokenizer.nextToken ();
				v.addElement (fileName);
			}

			String[] list = new String [v.size()];

			list = v.toArray (list);
*/
			//String[] list = fileNames.split("(?!^)");
			String[] list = fileNames.split(",");
			init (list);
		}
		else
		{
			String[] names = null;

			// Try to instantiate the config files class.
			try
			{
				Class<?> class_ = Class.forName (FILES_CLASS);

				Method method = class_.getMethod ("getFileNames", (Class[])null);

				names = (String[]) method.invoke (null, (Object[])null);
			}
			catch (Exception e)
			{
				// Ok, names will be null.
			}

			if (names != null)
			{
				log.debug("Constructing using class '"+FILES_CLASS+
					"' with files '"+fileNames+"'.");
				init (names);
			}
			else
			{
				// Use the default file list.
				// TBD convert list to string.
				log.debug("Constructing using default list :\n'"+defaultList+"'.");
				init (defaultList);
			}
		}
		log.debug("Leaving default constructor.");
	}

	/**
	 * Construct using an array of property file names.
	 * A file name may include a complete path.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the array order.
	 *
	 * @param		fileNames		the property file names
	 * @exception	IOException		trying to read a property file
	 */
	public  ConfigProperties (String[] fileNames) throws IOException
	{
		log = Logger.getLogger(this.getClass());
		log.debug("In explicit constructor.");
		init (fileNames);
		log.debug("Leaving explicit constructor.");
	}

	/**
	 * Dump the properties using System.out.
	 */
	public void dump ()
	{
		Iterator<String> iter = properties.keySet ().iterator ();

		while (iter.hasNext())
		{
			String key = iter.next();
			log.debug (key+" "+properties.get (key));
		}
	}

	/**
	 * Get the value for the given property, expanding if necessary.
	 * Use {@link #getPropertyUnexpanded getPropertyUnexpanded}
	 * to get the unexpanded value.
	 *
	 * @param	name	the property name
	 * @return			the value
	 * @see				#getPropertyUnexpanded
	 * @exception		MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		StringFormatException
	 *					if property value has unmatched substitution
	 *					delimiters
	 */
	public String	get (String name)
		throws MissingPropertyException, StringFormatException {

		String value = properties.get (name);

		if (value == null)
		{
			throw new MissingPropertyException
				("Property '"+name+"' not found in property map.");
		}

		List<Integer> tokenIndices = TokenIndexRetriever.getIndices (value,
				SUBSTITUTION_TOKEN);

		// If the tokens are not paired
		if (tokenIndices.size() % 2 != 0)
		{
			throw new StringFormatException
				("The value for property '"+name+"' has unmatched '"+
				 SUBSTITUTION_TOKEN+"' :\n'"+value+"'.");
		}

		String expandedValue = new String (value);

		for (int i = 0; i < tokenIndices.size()-1; i += 2)
		{
			int startIndex = tokenIndices.get(i);
			int endIndex = tokenIndices.get(i+1);

			String propertyName = value.substring
				(startIndex+1, endIndex);

			String propertyValue = null;

			if (propertyName.indexOf (ENV_PREFIX) == 0)
			{
				String envName = propertyName.substring
					(ENV_PREFIX.length(),propertyName.length());

				propertyValue = System.getProperty (envName);

				if (propertyValue == null)
				{
					throw new MissingPropertyException
						("Property '"+envName+"' not found in System properties.");
				}
			}
			else
			{
				propertyValue = get (propertyName);

				if (propertyValue == null)
				{
					throw new MissingPropertyException
						("Property '"+propertyName+"' not found in property map.");
				}
			}
			expandedValue = expandedValue.replace(
				SUBSTITUTION_TOKEN+propertyName+SUBSTITUTION_TOKEN, propertyValue);
		}

		return expandedValue;
	}

	/**
	 * Get the value for the given property, expanding references to other
	 * properties if necessary.
	 * <br>
	 * Do run-time substitution of args into the property value.
	 * See {@link java.text.MessageFormat#format MessageFormat} for info
	 * on how to define the property value and args.
	 * Briefly - defines property values like this:
	 * <br>
	 * MyClass.MY_PROPERTY=The day is {0} and month is {1}.
	 * <br>
	 * Then call get with args = {"Monday", "April"} and receive
	 * a return value of "The day is Monday and the month is April."
	 *
	 * @param	name	the property name
	 * @param	args	the run-time args
	 * @return			the value
	 * @see				java.text.MessageFormat
	 * @see				#getPropertyUnexpanded
	 * @exception		MissingPropertyException
	 *					if property or any nested property not found
	 * @exception		StringFormatException
	 *					if property value has unmatched substitution
	 *					delimiters
	 */
	public String	get (String name, String[] args)
		throws MissingPropertyException, StringFormatException
	{
		String temp = get (name);

		return MessageFormat.format (temp, (Object[])args);
	}

	/**
	 * Get the value for the given property without expanding.
	 *
	 * @param	name	the property name
	 * @return			the value or null
	 * @exception		MissingPropertyException
	 *					if property or any nested property not found
	 */
	public String	getUnexpanded (String name)
		throws MissingPropertyException
	{
		String value = properties.get (name);

		if (value == null)
		{
			throw new MissingPropertyException
				("Property '"+name+"' not found in property map.");
		}
		return value;
	}

	/**
	 * Get an iterator over the set of property names.
	 * @return		an iterator over strings
	 */
	public Iterator<String> getKeySet ()
	{
		return properties.keySet().iterator();
	}

	/**
	 * Initialize using an array of property file names.
	 * If no path is specified for a file, search the working directory
	 * and the CLASSPATH for the file.
	 * Properties are read and merged in the array order.
	 *
	 * @param		fileNames		the property file names
	 * @exception	IOException		trying to read a property file
	 */
	protected void init (String[] fileNames) throws IOException
	{
		log.debug("In init.");
		if (fileNames.length == 0)
		{
			return;
		}
		Properties p = new Properties ();

		for (int i = 0; i < fileNames.length; i++)
		{
			String fileName = fileNames [i];

			File file = new File (fileName);

			InputStream stream = null;

			if (file.getParent () == null)
			{
				// See if it is in the working dir.

				boolean exists = false;

				// Allow for security exception.
				try
				{
					exists = file.exists ();
				}
				catch (Exception e)
				{
					System.out.println ("Property file "+file.getName()+" not found, searching CLASSPATH.");
				}

				if (exists)
				{
					stream = new FileInputStream (file);
				}
				else
				{
					// Search the CLASSPATH for the file.

					Class<?> class_ = getClass ();
					ClassLoader cl = class_.getClassLoader ();

					if (cl.getResource (fileName) != null)
					{
						stream = cl.getResourceAsStream (fileName);
					}
					else
						continue;
				}
			}
			else // There is a complete path.
			{
				stream = new FileInputStream (file);
			}

			// Merge the properties.
			p.load (stream);
			//properties.putAll (p);
			Enumeration<?> enumer = p.propertyNames();
			while (enumer.hasMoreElements()) {
				String name = (String) enumer.nextElement();
				properties.put (name, p.getProperty(name));
			}
			stream.close();
		}
		log.debug("Leaving init.");
	}
}
