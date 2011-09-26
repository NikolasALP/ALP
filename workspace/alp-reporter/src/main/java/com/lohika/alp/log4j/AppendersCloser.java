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

import com.lohika.alp.log4j.attributes.AppendersAttribute;

/**
 * A helper class to close all {@link Appender} instances that are attached to
 * {@link ITestResult}. This guarantees that all data is flushed properly
 */
public class AppendersCloser {

	/**
	 * Closes all appenders attached to {@link ITestResult}
	 * 
	 * @param itr
	 */
	public void close(ITestResult itr) {
		Appenders appenders = AppendersAttribute.getAppenders(itr);

		if (appenders == null)
			return;

		synchronized (appenders) {
			appenders.closeAppenders();
		}
	}

}
