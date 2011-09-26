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

import org.apache.log4j.Appender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.lohika.alp.log4j.attributes.AppendersAttribute;

/**
 * The {@code TestAppenderDispatcher} class forwards given {@link LoggingEvent},
 * which is issued by currently running test method, to related {@link Appender}
 * instance.
 * <p>
 * A new appender is created for every test method when it makes first log
 * invocation. Then the appender is stored as an attribute of
 * {@link ITestResult} instance for further usage.
 * <p>
 * {@link TestAppenderBuilder} is used to create new appenders.
 * 
 * @author Mikhail Holovaty
 * 
 */
public class TestAppenderDispatcher {

	private TestAppenderBuilder builder;

	/**
	 * Initializes a newly created {@code TestAppenderDispatcher} with
	 * {@link TestAppenderBuilder} instance
	 * 
	 * @param builder
	 */
	public TestAppenderDispatcher(TestAppenderBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Forwards the event, which is issued by currently running test method, to
	 * related appender.
	 * 
	 * @param event
	 */
	public void doAppend(LoggingEvent event) {
		Appender a = getCurrentAppender();
		if (a != null)
			a.doAppend(event);
	}

	/**
	 * Returns {@link Appender} instance, attached to currently running test
	 * method.
	 * <p>
	 * Returns <code>null</code> in the following cases:
	 * <ul>
	 * <li>initialization of test instances
	 * <li>logging from a thread which doesn't belong to TestNG
	 * </ul>
	 * 
	 * @return appender of currently running test method
	 */
	private Appender getCurrentAppender() {
		ITestResult tr = Reporter.getCurrentTestResult();

		return getAppender(tr);
	}

	/**
	 * Returns {@link Appender} instance that is attached to given
	 * {@link ITestResult}
	 * <p>
	 * Returns <code>null</code>, if the test result is <code>null</code> or
	 * already finished.
	 * <p>
	 * Creates a new appender using {@link TestAppenderBuilder}, if the test
	 * result doesn't have attached appender yet.
	 * 
	 * @param tr
	 *            test result
	 * @return appender
	 */
	private Appender getAppender(ITestResult tr) {
		if (tr == null)
			return null;

		Appenders appenders = null;
		Appender appender = null;

		synchronized (tr) {
			appenders = AppendersAttribute.getAppenders(tr);

			// Create a new Appenders
			if (appenders == null) {
				appenders = new Appenders();
				AppendersAttribute.setAppenders(tr, appenders);
			}
		}

		synchronized (appenders) {
			// Prevents issue when Reporter.getCurrentTestResult() returns
			// result of already finished test
			if (appenders.areClosed())
				return null;

			try {
				appender = appenders.getAppender(this);

				// Create a new Appender
				if (appender == null) {
					appender = builder.getAppender(tr);
					appenders.putAppender(this, appender);
				}
			} catch (AppendersClosedException e) {
				LogLog.error("Trying to log into already closed Appenders "
						+ "of test method", e);
			}

			return appender;
		}

	}

}
