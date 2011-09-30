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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.IResultMap;
import org.testng.ISuiteResult;
import org.testng.ITestResult;

public class ResultsHelper {

	private Logger logger = Logger.getLogger(getClass());

	public List<ITestResult> getAllTestResults(ISuiteResult suiteResult) {
		List<ITestResult> testResults = new ArrayList<ITestResult>();
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getPassedTests());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getFailedTests());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getSkippedTests());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getPassedConfigurations());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getSkippedConfigurations());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getFailedConfigurations());
		addAllTestResults(testResults, suiteResult.getTestContext()
				.getFailedButWithinSuccessPercentageTests());
		return testResults;
	}

	public void addAllTestResults(List<ITestResult> testResults,
			IResultMap resultMap) {
		if (resultMap != null) {
			testResults.addAll(resultMap.getAllResults());
		}
	}

	/**
	 * Workaround for {@link ITestResult#getInstance()} issue. Skipped
	 * 'deppendsOnMethod' results return <code>null</code> instead of test
	 * instance
	 * <p/>
	 * 
	 * Workaround doesn't help for factory created instances.
	 * 
	 * @param result
	 *            The test class used this object is a result for
	 * @return
	 */
	public Object getTestInstance(ITestResult result) {
		Object instance = result.getInstance();

		if (instance == null) {
			Object[] testInstances = result.getTestClass().getInstances(false);

			if (testInstances.length == 1) {
				instance = testInstances[0];
			} else {
				logger.warn("TestNG factory issue. "
						+ "You have deppendsOn skipped method: "
						+ result.getName());
			}
		}

		return instance;
	}

}
