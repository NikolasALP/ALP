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
package com.lohika.alp.log4j;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.ITestResult;

import com.lohika.alp.log4j.attributes.LogFileAttribute;
import com.lohika.alp.log4j.strategies.TimeIndexName;

/**
 * {@code TestFileAppender} appends log events of test methods into separate
 * files.
 * 
 * @author Mikhail Holovaty
 * 
 */
public class TestFileAppender extends AppenderSkeleton implements
		TestAppenderBuilder {

	private String directory = "test-output" + File.separator + "logs";

	// Set default strategy
	private String nameStrategy = TimeIndexName.class.getName();

	private LogNameStrategy nameStrategyImpl;

	protected TestAppenderDispatcher dispatcher = new TestAppenderDispatcher(
			this);

	/**
	 * Returns value of directory property
	 * 
	 * @return directory of output log files
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * Sets directory property where log files should be created
	 * 
	 * @param directory
	 *            of output log files
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * Returns value nameStrategy property
	 * 
	 * @return
	 */
	public String getNameStrategy() {
		return nameStrategy;
	}

	/**
	 * Sets nameStrategy property, which defines algorithm of unique log file
	 * names
	 * 
	 * @param nameStrategy
	 */
	public void setNameStrategy(String nameStrategy) {
		this.nameStrategy = nameStrategy;
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	public void activateOptions() {
		try {
			// TODO Replace lookup class with unified parameterized strategy
			nameStrategyImpl = LogNameStrategyLookup
					.getLogNameStrategy(nameStrategy);
		} catch (Exception e) {
			errorHandler.error(e.toString());
		}
	}

	@Override
	public void close() {
		// No need to close here because appenders are closed from test listener
	}

	@Override
	protected void append(LoggingEvent event) {
		// Forwards log event to proper test method appender
		dispatcher.doAppend(event);
	}

	@Override
	public Appender getAppender(ITestResult tr) {
		String filename = nameStrategyImpl.getName(tr);

		try {
			String path = directory + File.separator + filename;

			File logFile = new File(path);
			if (logFile.exists())
				LogLog.warn("Attempting to create FileAppender for already "
						+ "existing file: " + logFile.getAbsolutePath());

			Layout layoutClone = layout;

			if (layout instanceof CloneableLayout) {
				layoutClone = ((CloneableLayout) layout).cloneLayout();
			}

			// Building FileAppender with empty constructor to specify UTF-8
			// encoding explicitly to be independent from locale settings
			FileAppender appender = new FileAppender();
			appender.setLayout(layoutClone);
			appender.setFile(path);
			appender.setBufferedIO(false);
			appender.setEncoding("UTF-8");
			appender.activateOptions();

			// Store log file as TestNG result attribute
			LogFileAttribute.setLogFile(tr, new File(path));
			return appender;
		} catch (Exception e) {
			errorHandler.error(e.toString());
			return null;
		}
	}

	class UTFFileAppender extends FileAppender {

		public UTFFileAppender(Layout layout, String filename)
				throws IOException {
			super(layout, filename);
		}

	}

}
