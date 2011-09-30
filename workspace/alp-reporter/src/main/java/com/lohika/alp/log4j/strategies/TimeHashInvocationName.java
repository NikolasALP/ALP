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

import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.lohika.alp.log4j.LogNameStrategy;

public class TimeHashInvocationName implements LogNameStrategy {

	@Override
	public String getName(ITestResult result) {

		// Start milliseconds of test
		long millis = result.getStartMillis();

		// Hash code to distinguish several test instances of the same
		// class. Collisions can be ignored for small amount of test objects
		int hash = result.getInstance().hashCode();

		// Test instance class name
		String clazz = result.getTestClass().getRealClass().getSimpleName();

		// Test name (usually test method name)
		String test = result.getName();

		// Test method current invocation. If method has only one invocation,
		// the prefix is omitted
		String invocation = "";

		// FIXME getCurrentInvocationCount fails in parallel mode
		ITestNGMethod testNGMethod = result.getMethod();
		if (testNGMethod.getInvocationCount() > 1) {
			invocation = "." + testNGMethod.getCurrentInvocationCount();
		}

		String name = millis + "." + hash + invocation + "-" + clazz + "."
				+ test;

		return name;
	}

}
