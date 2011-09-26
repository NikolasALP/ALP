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
import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.IConfigurationListener;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import com.lohika.alp.log4j.AppendersCloser;

public class Reporter implements ISuiteListener, ITestListener,
		IConfigurationListener, IReporter {

	private Logger logger = Logger.getLogger(getClass());

	private Log4jConfigurer log4jc = new Log4jConfigurer();
	
	private AppendersCloser appendersCloser = new AppendersCloser();

	// Suite listener implementation

	@Override
	public void onStart(ISuite suite) {
	}

	@Override
	public void onFinish(ISuite suite) {
	}

	// Test listener implementation

	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onFinish(ITestContext context) {
	}

	// Method listener implementation

	@Override
	public void onTestStart(ITestResult itr) {
	}

	@Override
	public void onTestSuccess(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void onTestFailure(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void onTestSkipped(ITestResult itr) {
		appendersCloser.close(itr);
	}

	// Configuration listener implementation

	@Override
	public void onConfigurationSuccess(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void onConfigurationFailure(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void onConfigurationSkip(ITestResult itr) {
		appendersCloser.close(itr);
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {
		String log4jAbsolute = new File(log4jc.getOutputDirectory())
				.getAbsolutePath();
		String testNgAbsolute = new File(outputDirectory).getAbsolutePath();

		if (!testNgAbsolute.equals(log4jAbsolute)) {
			logger.warn("TestNG output directory parameter is ignored. "
					+ "It differs from Log4j file appender parameter");
		}

		// Generate general XML report
		ResultsXML xmlReporter = new ResultsXML();
		xmlReporter.generateReport(xmlSuites, suites, log4jAbsolute);

		// Transform XML to HTML
		ResultsXML2HTML htmlReporter = new ResultsXML2HTML();
		htmlReporter.generateReport(xmlSuites, suites, log4jAbsolute);
	}

}
