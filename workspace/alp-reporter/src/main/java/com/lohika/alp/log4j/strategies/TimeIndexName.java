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
package com.lohika.alp.log4j.strategies;

import org.testng.ITestResult;

import com.lohika.alp.log4j.LogNameStrategy;

public class TimeIndexName implements LogNameStrategy {

	private int index = 0;

	/**
	 * @return next log index
	 */
	protected synchronized int nextIndex() {
		return index++;
	}

	@Override
	public String getName(ITestResult result) {
		// Start milliseconds of test
		long millis = result.getStartMillis();

		int index = nextIndex();

		// Test instance class name
		String clazz = result.getTestClass().getRealClass().getSimpleName();

		// Test name (usually test method name)
		String test = result.getName();

		String name = millis + "." + index + "-" + clazz + "." + test;

		return name;
	}

}
