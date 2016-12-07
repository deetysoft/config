package com.deetysoft.config;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the class {@link ConfigPropertiesS}.
 * @author greg
 *
 */
public class ConfigPropertiesSTest {

	protected static Logger log = null;

	static {
		log = Logger.getLogger(ConfigPropertiesSTest.class);
	}

	@BeforeClass
	public static void beforeClass () {
		log.debug("beforeClass");
		ConfigPropertiesTest.clearEnv ();
		// Cause ConfigPropertiesS to load.
		// Assert no files are read.
		Assert.assertFalse(ConfigPropertiesS.getKeySet().hasNext());
		log.debug("beforeClass");
	}

	@BeforeMethod
	public static void beforeMethod () {
		
		log.debug("beforeMethod");
		ConfigPropertiesTest.clearEnv();
		log.debug("leaving beforeMethod");
	}

	/**
	 * Test init {@link ConfigProperties#init(String[]}.
	 * Define a file list and assert they are merged properly.
	 * @throws Exception	on any error
	 */
	@Test
	public static void testInit () throws Exception {
		log.debug("in testInit");

		String workingDir = System.getProperty("user.dir");

		// Assert that initializing with FILE1 yields
		// the expected values for properties.
		String[] fileNames = {workingDir + "/" + ConfigPropertiesTest.FILE1};
		ConfigPropertiesS.init (fileNames);
		System.setProperty("com.deetysoft.config.ENV_TEST", "fred");
		ConfigPropertiesTest.testExplicitFile_(ConfigPropertiesS.getConfigProperties());

		// Assert that initializing with FILE1,FILE2 yields
		// the expected values for properties.
		String[] fileNames2 = {workingDir + "/" + ConfigPropertiesTest.FILE1,
			workingDir + "/" + ConfigPropertiesTest.FILE2};
		ConfigPropertiesS.init (fileNames2);
		ConfigPropertiesTest.testExplicitFiles_(ConfigPropertiesS.getConfigProperties());
	}
}
