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
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.lohika.alp.log4j.attributes.LogFileAttribute;
import com.lohika.alp.reporter.helpers.ResultsHelper;
import com.lohika.alp.testng.results.schema.Groups;
import com.lohika.alp.testng.results.schema.GroupsDependedUpon;
import com.lohika.alp.testng.results.schema.Logfile;
import com.lohika.alp.testng.results.schema.MethodStatus;
import com.lohika.alp.testng.results.schema.MethodType;
import com.lohika.alp.testng.results.schema.MethodTypes;
import com.lohika.alp.testng.results.schema.MethodsDependedUpon;
import com.lohika.alp.testng.results.schema.ObjectFactory;
import com.lohika.alp.testng.results.schema.Results;
import com.lohika.alp.testng.results.schema.Suite;
import com.lohika.alp.testng.results.schema.Test;
import com.lohika.alp.testng.results.schema.TestInstance;
import com.lohika.alp.testng.results.schema.TestMethod;

/**
 * The <code>ResultsXML</code> class generates XML file with general test run
 * results according to {@link http://alp.lohika.com/testng/results/schema}
 * <p>
 * <code>results.xml</code> file is extended analog of
 * <code>testng-results.xml</code>
 */
public class ResultsXML {

	private final Logger logger = Logger.getLogger(getClass());

	private final ResultsHelper helper = new ResultsHelper();

	private String contextPath = "com.lohika.alp.testng.results.schema";

	private String filename = "results.xml";

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {

		final File outputFile = new File(outputDirectory, filename);

		final ObjectFactory factory = new ObjectFactory();

		// Create 'results' tag
		Results resultsTag = factory.createResults();

		for (ISuite suite : suites) {

			// Create 'suite' tag
			Suite tSuite = factory.createSuite();
			resultsTag.getSuite().add(tSuite);

			// Set 'name' attribute
			tSuite.setName(suite.getName());

			for (Entry<String, ISuiteResult> eTest : suite.getResults()
					.entrySet()) {
				String testName = eTest.getKey();
				ISuiteResult test = eTest.getValue();

				// Create 'test' tag
				Test tTest = factory.createTest();
				tSuite.getTest().add(tTest);

				// Set 'name' attribute
				tTest.setName(testName);

				List<ITestResult> results = helper.getAllTestResults(test);

				Collections.sort(results);

				Map<Object, TestInstance> instances = new HashMap<Object, TestInstance>();

				for (ITestResult result : results) {
					Object testInstance = helper.getTestInstance(result);

					// Create 'test-instance' tag
					TestInstance tTestInstance = instances.get(testInstance);

					if (tTestInstance == null) {
						tTestInstance = factory.createTestInstance();
						tTest.getTestInstance().add(tTestInstance);
						instances.put(testInstance, tTestInstance);

						// Set 'class' attribute
						tTestInstance.setClazz(result.getTestClass().getName());
					}

					// Create 'test-method' tag
					TestMethod tTestMethod = factory.createTestMethod();
					tTestInstance.getTestMethod().add(tTestMethod);

					long startMillis = result.getStartMillis();
					long endMillis = result.getEndMillis();

					// Set 'duration-ms' attribute
					tTestMethod.setDurationMs(endMillis - startMillis);

					// Set 'started-at' attribute
					tTestMethod
							.setStartedAt(newXMLGregorianCalendar(startMillis));

					// Set 'finished-at' attribute
					tTestMethod
							.setFinishedAt(newXMLGregorianCalendar(endMillis));

					// Set 'name' attribute
					tTestMethod.setName(result.getName());

					switch (result.getStatus()) {
					case ITestResult.SUCCESS:
						tTestMethod.setStatus(MethodStatus.SUCCESS);
						break;
					case ITestResult.FAILURE:
						tTestMethod.setStatus(MethodStatus.FAILURE);
						break;
					case ITestResult.SKIP:
						tTestMethod.setStatus(MethodStatus.SKIP);
						break;
					// TODO Add ITestResult.SUCCESS_PERCENTAGE_FAILURE support
					}

					ITestNGMethod testNGMethod = result.getMethod();

					// Create 'method-types' tag
					MethodTypes tMethodTypes = factory.createMethodTypes();
					List<MethodType> tType = tMethodTypes.getType();

					if (testNGMethod.isBeforeSuiteConfiguration())

						tType.add(MethodType.BEFORE_SUITE);

					if (testNGMethod.isBeforeTestConfiguration())
						tType.add(MethodType.BEFORE_TEST);

					if (testNGMethod.isBeforeClassConfiguration())
						tType.add(MethodType.BEFORE_CLASS);

					if (testNGMethod.isBeforeGroupsConfiguration())
						tType.add(MethodType.BEFORE_GROUP);

					if (testNGMethod.isBeforeMethodConfiguration())
						tType.add(MethodType.BEFORE_METHOD);

					if (testNGMethod.isAfterSuiteConfiguration())
						tType.add(MethodType.AFTER_SUITE);

					if (testNGMethod.isAfterTestConfiguration())
						tType.add(MethodType.AFTER_TEST);

					if (testNGMethod.isAfterClassConfiguration())
						tType.add(MethodType.AFTER_CLASS);

					if (testNGMethod.isAfterGroupsConfiguration())
						tType.add(MethodType.AFTER_GROUP);

					if (testNGMethod.isAfterMethodConfiguration())
						tType.add(MethodType.AFTER_METHOD);

					// Create 'method-types' tag only if it has values
					if (tType.size() > 0)
						tTestMethod.setTypes(tMethodTypes);

					String[] groups = testNGMethod.getGroups();

					if (groups.length > 0) {
						// Create 'groups' tag
						Groups tGroups = factory.createGroups();
						tTestMethod.setGroups(tGroups);

						List<String> tGroup = tGroups.getGroup();

						for (String group : groups) {
							tGroup.add(group);
						}
					}

					String[] methodsDependedUpon = testNGMethod
							.getMethodsDependedUpon();

					if (methodsDependedUpon.length > 0) {
						// Create 'depends-on-methods' tag
						MethodsDependedUpon tMethodsDependsUpon = factory
								.createMethodsDependedUpon();
						tTestMethod.setDependsOnMethods(tMethodsDependsUpon);

						List<String> tMethods = tMethodsDependsUpon.getMethod();

						for (String methodDependedUpon : methodsDependedUpon) {

							// Get only method name, since method dependency can
							// be only inside class
							String[] parts = methodDependedUpon.split("\\.");
							String methodName = parts[parts.length - 1];

							tMethods.add(methodName);
						}
					}

					String[] groupsDependedUpon = testNGMethod
							.getGroupsDependedUpon();

					if (groupsDependedUpon.length > 0) {
						// Create 'depends-on-groups' tag
						GroupsDependedUpon tGroupsDependsUpon = factory
								.createGroupsDependedUpon();
						tTestMethod.setDependsOnGroups(tGroupsDependsUpon);

						List<String> tGroups = tGroupsDependsUpon.getGroup();

						for (String groupDependedUpon : groupsDependedUpon) {
							tGroups.add(groupDependedUpon);
						}
					}

					Throwable throwable = result.getThrowable();

					if (throwable != null) {
						// Create 'exception' tag
						com.lohika.alp.testng.results.schema.Exception tException = factory
								.createException();
						tTestMethod.setException(tException);

						// Create 'class' tag
						tException.setClazz(throwable.getClass().getName());

						// Create 'message' tag
						tException.setMessage(throwable.getMessage());

						// Convert stack trace to string
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						throwable.printStackTrace(pw);
						pw.flush();

						// Create 'full-stacktrace' tag
						tException.setFullStacktrace(sw.getBuffer().toString());
					}

					// Get XML log file stored as ITestResult attribute
					File log = LogFileAttribute.getLogFile(result);

					if (log != null) {
						// Create 'logfile' tag
						Logfile tLogfile = factory.createLogfile();
						tTestMethod.setLogfile(tLogfile);

						// Get relative path
						URI uri1 = outputFile.getParentFile().toURI();
						URI uri2 = log.toURI();
						URI relative = uri1.relativize(uri2);

						tLogfile.setPath(relative.toString());
					}
				}
			}
		}

		try {
			final JAXBContext jc = JAXBContext.newInstance(contextPath);

			final Marshaller marshaller = jc.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(resultsTag, new FileOutputStream(outputFile));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected XMLGregorianCalendar newXMLGregorianCalendar(long millis) {
		try {
			DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

			GregorianCalendar cal = new GregorianCalendar();

			return datatypeFactory.newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			logger.error(e);
			return null;
		}
	}

}
