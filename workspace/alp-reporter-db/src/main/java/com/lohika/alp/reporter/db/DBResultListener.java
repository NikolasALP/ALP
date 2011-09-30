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
package com.lohika.alp.reporter.db;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.lohika.alp.reporter.db.model.EMethodStatus;

/**
 * Implementation of TestNG listener to store test results into database.
 * 
 */
public class DBResultListener implements IResultListener {

	private DBResultHelper dbResults = new DBResultHelper();

	public void onStart(ITestContext tc) {
	}

	public void onFinish(ITestContext arg0) {
	}

	public void onTestStart(ITestResult arg0) {
	}

	public void onTestSuccess(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.SUCCESS);
	}

	public void onTestFailure(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.FAILURE);
	}

	public void onTestSkipped(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.SKIP);
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO handle test failure with success percentage
	}

	public void onConfigurationSuccess(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.SUCCESS);
	}

	public void onConfigurationFailure(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.FAILURE);
	}

	public void onConfigurationSkip(ITestResult itr) {
		dbResults.saveMethodResult(itr, EMethodStatus.SKIP);
	}

}
