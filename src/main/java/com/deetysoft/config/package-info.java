/**
 * Software installation configuration utilities.
 * <p>
 * This package contains utilities for managing software installation configuration data.
 * Installation configuration data is that data required by software to run in a particular deployment.
 * <p>
 * It is data that may be unique to a particular deployment.
 * For example the host name and port for a remote resource may vary and be different for each user's deployment.
 * <p>
 * Configuration data is best represented by files (in our case property or XML files.)
 * This is because bootstrap data must be available from a simple and reliable source such as a file.
 * <p>
 * This package contains a class {@link ConfigProperties} for using property files as configuration data.
 * Property file based configuration is a suitable approach for many applications.
 * ConfigProperties helps satisfy these requirements:
 * <pre>
 * * Define properties as key/value pairs
 * * Store these pairs in a text file
 * * Merge multiple files into single map
 * * Provide multiple mechanisms for defining the file list
 * * Provide a strategy for trying the mechanisms
 * * Allow each deployment to have its own file versions
 * </pre>
 */
package com.deetysoft.config;
