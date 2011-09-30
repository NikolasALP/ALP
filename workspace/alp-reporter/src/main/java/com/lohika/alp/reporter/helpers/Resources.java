//Copyright 2011 Lohika .  This file is part of ALP.
//
//    ALP is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    ALP is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with ALP.  If not, see <http://www.gnu.org/licenses/>.
package com.lohika.alp.reporter.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * <code>Resources</code> class helps to copy own project resources outside
 * programmatically. The project can be located in a file system or being a JAR
 * package.
 * 
 * @author Mikhail Holovaty
 */
public class Resources {

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Copy own project resources to outside destination.
	 * <p>
	 * Works for regular files and JAR packages
	 * 
	 * @param clazz
	 *            any java class that lives in the same place as the resources
	 *            you want
	 * @param path
	 *            project resources path. Should end with "/", but not start
	 *            with one
	 * @param destination
	 *            outside destination
	 * @throws IOException
	 */
	public void copy(Class<?> clazz, String path, File destination)
			throws IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);

		if (dirURL != null && dirURL.getProtocol().equals("file")) {

			logger.debug("Resources to copy is stored in file system " + dirURL);

			// A file path: easy enough
			copy(new File(dirURL.getPath()), destination);
			return;

		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as clazz
			 */
			String me = clazz.getName().replace(".", "/") + ".class";

			dirURL = clazz.getClassLoader().getResource(me);
		}

		logger.debug("Resources to copy is stored in jar " + dirURL);

		if (dirURL.getProtocol().equals("jar")) {
			// A JAR path
			String jarPath = dirURL.getPath().substring(5,
					dirURL.getPath().indexOf("!")); // strip out only the JAR
													// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));

			copy(jar, path, destination);
			return;

		}

		throw new UnsupportedOperationException("Cannot copy files from URL "
				+ dirURL);
	}

	/**
	 * Copy JAR resources from path to outside destination.
	 * 
	 * If destination does not exist, it will be created
	 * 
	 * @param jar
	 *            package with required resources
	 * @param path
	 *            resources path inside package. Should end with "/", but not
	 *            start with one
	 * @param destination
	 *            outside destination
	 * @throws IOException
	 */
	public void copy(JarFile jar, String path, File destination)
			throws IOException {
		logger.debug("Copying " + path + " to " + destination);

		Enumeration<JarEntry> entries = jar.entries();

		int len = path.length();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			String name = entry.getName();

			// Use only entries from source
			if (name.startsWith(path)) {

				// Replace path with destination
				File file = new File(destination, name.substring(len));

				if (entry.isDirectory())
					// If entry is directory, create it

					file.mkdir();
				else {
					// Otherwise write entry to file

					InputStream is = jar.getInputStream(entry);
					FileOutputStream os = new FileOutputStream(file);

					while (is.available() > 0) {
						os.write(is.read());
					}
					os.close();
					is.close();
				}
			}
		}
	}

	/**
	 * Copy project files located in file system to outside destination.
	 * <p>
	 * If destination does not exist, it will be created.
	 * 
	 * @param path
	 *            resources path located in file system
	 * @param destination
	 *            outside destination
	 * @throws IOException
	 */
	public void copy(File path, File destination) throws IOException {

		logger.debug("Copying " + path + " to " + destination);

		if (path.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdir();
			}

			String[] children = path.list();
			for (int i = 0; i < children.length; i++) {
				copy(new File(path, children[i]), new File(destination,
						children[i]));
			}
		} else {

			InputStream in = new FileInputStream(path);
			OutputStream out = new FileOutputStream(destination);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

}
