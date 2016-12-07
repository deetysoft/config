package com.deetysoft.config;

import java.io.File;
import java.io.FileWriter;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the class {@link ConfigProperties}.
 * @author greg
 *
 */
public class ConfigPropertiesTest {

	/**
	 * The first in a list of property files.
	 */
	protected static final String FILE1 =
		"src/test/resources/com/deetysoft/config/FILE1.properties";
	/**
	 * The second in a list of property files.
	 * This file should contain some over-ridden properties.
	 */
	protected static final String FILE2 =
		"src/test/resources/com/deetysoft/config/FILE2.properties";

	protected static Logger log = null;

	static {
		log = Logger.getLogger(ConfigPropertiesTest.class);
	}

	@BeforeMethod
	public static void beforeMethod () {
		
		log.debug("beforeMethod");
		clearEnv();
		log.debug("leaving beforeMethod");
	}

	@AfterClass
	public static void afterClass () {
		
		log.debug("afterClass");
		clearEnv();
		log.debug("leaving afterClass");
	}

	static void clearEnv () {	
		System.clearProperty(ConfigProperties.FILES_PROPERTY);			
		System.clearProperty("com.deetysoft.config.ENV_TEST");
		File f = new File (ConfigProperties.DEFAULT_FILE);
		f.delete();
	}

	/**
	 * Clear the system property {@link ConfigProperties#FILES_PROPERTY},
	 * use the default constructor and assert that the properties set is empty.
	 * It is empty because no property files are defined.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testDefaultConstructor () throws Exception {
		log.debug("in testDefaultConstructor");
		ConfigProperties c = new ConfigProperties ();
		Assert.assertFalse(c.getKeySet().hasNext(),"The key set is not empty.");
		log.debug("leaving testDefaultConstructor");
	}

	/**
	 * Set the system property {@link ConfigProperties#FILES_PROPERTY}, use default constructor and 
	 * assert that the properties map is not empty.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testConstructorWithSystemProperty () throws Exception {
		log.debug("in testConstructorWithSystemProperty");
		String workingDir = System.getProperty("user.dir");
		// Construct with FILE1,FILE2.
		System.setProperty(ConfigProperties.FILES_PROPERTY,
			workingDir + "/" + FILE1 + "," + workingDir + "/" + FILE2);
		ConfigProperties c = new ConfigProperties ();
		// Make assertions given we constructed with these files.
		testExplicitFiles_(c);
		log.debug("leaving testConstructorWithSystemProperty");
	}

	/**
	 * Test using the default property file.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testDefaultPropertyFile () throws Exception {
		log.debug("in testDefaultPropertyFile");
		File f = new File (ConfigProperties.DEFAULT_FILE);
		try (FileWriter w = new FileWriter (f)){
			String property = "hostname=Betty";
			w.write(property, 0, property.length());
			w.flush();
			ConfigProperties c = new ConfigProperties ();
			Assert.assertTrue(c.getKeySet().hasNext(),"The key set is not empty.");
		} finally {
			f.delete();
		}
		log.debug("leaving testDefaultPropertyFile");
	}

	/**
	 * Test the explicit constructor {@link ConfigProperties#ConfigProperties(String[]}
	 * with an empty file list.
	 * Assert the property map is empty.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testNullFile () throws Exception {
		log.debug("in testNullFile");

		// Assert that constructing with an empty file list results in an empty properties.
		String[] fileNames = {};
		ConfigProperties c = new ConfigProperties (fileNames);
		Assert.assertFalse(c.getKeySet().hasNext(),"The key set is not empty.");
		log.debug("leaving testNullFile");
	}

	/**
	 * Test the explicit constructor {@link ConfigProperties#ConfigProperties(String[]}
	 * with a single file and assert that properties are defined as expected.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testExplicitFile () throws Exception {
		log.debug("in testExplicitFile");

		String workingDir = System.getProperty("user.dir");

		// Assert that constructing with FILE1 yields
		// the expected value for a property.
		String[] fileNames = {workingDir + "/" + FILE1};
		System.setProperty("com.deetysoft.config.ENV_TEST", "fred");
		ConfigProperties c = new ConfigProperties (fileNames);
		testExplicitFile_(c);
		log.debug("leaving testExplicitFile");
	}

	/**
	 * Test the explicit constructor {@link ConfigProperties#ConfigProperties(String[]}
	 * with multiple files in the file list.
	 * Assert that the value substitution and argument substitution features work as expected.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testExplicitFiles () throws Exception {
		log.debug("in testExplicitFiles");

		String workingDir = System.getProperty("user.dir");

		// Construct with FILE1,FILE2.
		String[] fileNames = {workingDir + "/" + FILE1,
				workingDir + "/" + FILE2};
		ConfigProperties c = new ConfigProperties (fileNames);
		// Make assertions given we constructed with these files.
		testExplicitFiles_(c);
		log.debug("in testExplicitFiles");
	}

	/**
	 * Make assertions assuming the given ConfigProperties
	 * was initialized with file FILE1.
	 * @param c	the ConfigProperties
	 * @throws Exception	on any error
	 */
	static void testExplicitFile_ (ConfigProperties c) throws Exception {
		log.debug("in testExplicitFile_");

		Assert.assertTrue(c.getKeySet().hasNext(), "Properties are not empty.");
		Assert.assertEquals(c.get("hostname"), "betty");
		// Assert that the arg substitution feature works.
		String[] args = {"June", "Monday"};
		Assert.assertEquals(c.get("com.deetysoft.config.arg_test", args),
			"The month is June and the day is Monday.");
		// Assert that the given system property was set before init.
		Assert.assertEquals(c.get("com.deetysoft.config.ENV_TEST"), "fred");
		log.debug("leaving testExplicitFile_");
	}

	/**
	 * Make assertions assuming the given ConfigProperties
	 * was initialized with files FILE1,FILE2.
	 * @param c	the ConfigProperties
	 * @throws Exception	on any error
	 */
	static void testExplicitFiles_ (ConfigProperties c) throws Exception {
		log.debug("in testExplicitFiles_");

		// Assert the properties are not empty.
		Assert.assertTrue(c.getKeySet().hasNext(), "Properties are empty.");

		// Assert the value of host name is now 'wilma' after merging.
		Assert.assertEquals(c.get("hostname"), "wilma");

		// Assert that the value substitution feature works.
		Assert.assertEquals(c.get("msg"), "The hostname is wilma and port is 1776.");

		log.debug("leaving testExplicitFiles_");
	}
}
