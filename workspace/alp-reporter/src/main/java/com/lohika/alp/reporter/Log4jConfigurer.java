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
package com.lohika.alp.reporter;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

import com.lohika.alp.log.elements.renderer.ActionRenderer;
import com.lohika.alp.log.elements.renderer.CommentRenderer;
import com.lohika.alp.log.elements.renderer.LinkRenderer;
import com.lohika.alp.log.elements.renderer.ScreenshotRenderer;
import com.lohika.alp.log.elements.renderer.TextareaRenderer;
import com.lohika.alp.log.elements.schema.Action;
import com.lohika.alp.log.elements.schema.Comment;
import com.lohika.alp.log.elements.schema.Link;
import com.lohika.alp.log.elements.schema.Screenshot;
import com.lohika.alp.log.elements.schema.Textarea;
import com.lohika.alp.log4j.TestFileAppender;
import com.lohika.alp.log4j.xml.XMLLayout;

/**
 * Configures Log4j programmatically to use {@link XMLLayout} and
 * {@link TestFileAppender}, if they aren't already configured in
 * log4j.properties
 * <p>
 * Configures all {@link ObjectRenderer} implementations for
 * <code>com.lohika.alp.log.elements</code> package
 */
public class Log4jConfigurer {

	// Invocation of getLogger method before configuring Log4j programmatically
	// allows log4j.properties file to be loaded properly
	private Logger logger = Logger.getLogger(getClass());

	// Default value can be overwritten with TestFileAppender 'directory'
	// parameter in log4j.properties file
	private String outputDirectory = "test-output";

	public Log4jConfigurer() {
		Logger rootLogger = Logger.getRootLogger();

		TestFileAppender testFileAppender = null;

		// Check if TestFileAppender is already configured in log4j.properties
		Enumeration<?> rootAppenders = rootLogger.getAllAppenders();
		boolean isNotConfigured = true;

		while (isNotConfigured && rootAppenders.hasMoreElements()) {
			Object appender = rootAppenders.nextElement();

			if (appender instanceof TestFileAppender) {
				isNotConfigured = false;
				testFileAppender = (TestFileAppender) appender;
			}
		}

		if (isNotConfigured) {
			XMLLayout layout = new XMLLayout();
			testFileAppender = new TestFileAppender();
			if (outputDirectory != null && !"".equals(outputDirectory))
				testFileAppender.setDirectory(outputDirectory + File.separator
						+ "logs");

			testFileAppender.setLayout(layout);
			testFileAppender.activateOptions();

			rootLogger.addAppender(testFileAppender);
		} else if (testFileAppender != null) {
			// Set output directory value from log4j.properties file
			outputDirectory = testFileAppender.getDirectory();
		}

		Logger alpLogger = Logger.getLogger("com.lohika.alp");
		if (alpLogger.getLevel() == null)
			alpLogger.setLevel(Level.INFO);

		LoggerRepository repository = logger.getLoggerRepository();

		if (repository instanceof RendererSupport) {

			final RendererMap map = ((RendererSupport) repository)
					.getRendererMap();

			// Get default renderer
			final Object defaultRenderer = map.get(null);

			// Set renderer if it isn't already registered via log4j.properties
			RendererHelper helper = new RendererHelper() {
				@Override
				public void put(Class<?> clazz, ObjectRenderer renderer) {
					if (map.get(clazz).equals(defaultRenderer)) {
						map.put(clazz, renderer);
					}
				}
			};

			// TODO scan renderer classes automatically
			helper.put(Action.class, new ActionRenderer());
			helper.put(Screenshot.class, new ScreenshotRenderer());
			helper.put(Textarea.class, new TextareaRenderer());
			helper.put(Comment.class, new CommentRenderer());
			helper.put(Link.class, new LinkRenderer());
		}
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	private interface RendererHelper {
		void put(Class<?> clazz, ObjectRenderer renderer);

	}

}
