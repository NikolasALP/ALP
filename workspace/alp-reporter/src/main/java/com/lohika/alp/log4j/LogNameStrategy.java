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

import org.testng.ITestResult;

/**
 * Declares interface of an algorithm to give unique name prefixes for test
 * method logs.
 * 
 * @author Mikhail Holovaty
 * 
 */
public interface LogNameStrategy {

	/**
	 * Provides a unique name prefix of log file for the given test result
	 * 
	 * @param tr
	 *            result of test to be logged
	 * @return unique name prefix of log file
	 */
	public String getName(ITestResult tr);

}
