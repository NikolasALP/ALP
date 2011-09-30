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
import org.testng.ITestResult;

/**
 * Defines interface to create appenders of the same type for given test
 * methods.
 * 
 * @author Mikhail Holovaty
 * 
 */
public interface TestAppenderBuilder {

	/**
	 * Creates a new {@link Appender} to log messages from a test method, which
	 * is represented by the given {@link ITestResult}
	 * 
	 * @param tr
	 *            test result of the method to be logged
	 * @return appender
	 */
	public Appender getAppender(ITestResult tr);

}
