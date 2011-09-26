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

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Expression;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.lohika.alp.log4j.attributes.LogFileAttachmentAttribute;
import com.lohika.alp.log4j.attributes.LogFileAttribute;
import com.lohika.alp.reporter.client.LogUploader;
import com.lohika.alp.reporter.db.model.EMethodStatus;
import com.lohika.alp.reporter.db.model.EMethodType;
import com.lohika.alp.reporter.db.model.Group;
import com.lohika.alp.reporter.db.model.MethodType;
import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestClass;
import com.lohika.alp.reporter.db.model.TestInstance;
import com.lohika.alp.reporter.db.model.TestMethod;
import com.lohika.alp.reporter.db.model.TestMethodException;
import com.lohika.alp.reporter.helpers.ResultsHelper;

/**
 * Helper class to save test method result into database.
 * 
 */
public class DBResultHelper {

	private final SessionFactory factory;

	// TODO clear values after test run
	private final Map<Object, TestInstance> testInstances = Collections
			.synchronizedMap(new HashMap<Object, TestInstance>());

	private final Map<String, Group> groups = Collections
			.synchronizedMap(new HashMap<String, Group>());

	private final Map<XmlSuite, Suite> suites = Collections
			.synchronizedMap(new HashMap<XmlSuite, Suite>());

	private final Map<XmlTest, Test> tests = Collections
			.synchronizedMap(new HashMap<XmlTest, Test>());

	private final ResultsHelper helper = new ResultsHelper();

	// TODO Use LogUploader with client of results web services
	private final LogUploader logUploader = new LogUploader();

	public DBResultHelper() {
		AnnotationConfiguration conf = new AnnotationConfiguration()
				.addAnnotatedClass(TestClass.class)
				.addAnnotatedClass(TestInstance.class)
				.addAnnotatedClass(TestMethod.class)
				.addAnnotatedClass(Group.class)
				.addAnnotatedClass(MethodType.class)
				.addAnnotatedClass(TestMethodException.class)
				.addAnnotatedClass(Suite.class).addAnnotatedClass(Test.class);

		// TODO make configuration accessible to a library user
		conf.configure("/hibernate.cfg.xml");

		factory = conf.buildSessionFactory();
	}

	private void saveLog(ITestResult methodResult, TestMethod testMethod) {
		// TODO Use saveLog with client of results web services
		File logFile = LogFileAttribute.getLogFile(methodResult);
		List<File> attachments = LogFileAttachmentAttribute
				.getAttachmentFiles(methodResult);

		if (logFile != null) {
			Session session = factory.openSession();
			session.beginTransaction();

			try {
				long id = testMethod.getId();
				logUploader.upload(id, logFile);

				// Upload attachments
				if (attachments != null) {
					for (File attachment : attachments) {
						logUploader.upload(id, attachment);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	/**
	 * The main function which saves method and all the depended entities into
	 * database
	 * 
	 * @param methodResult
	 * @param status
	 */
	public void saveMethodResult(ITestResult methodResult, EMethodStatus status) {
		Suite suite = saveSuite(methodResult);
		Test test = saveTest(methodResult, suite);
		TestInstance testInstance = saveTestInstance(methodResult, test);
		TestMethodException exception = saveException(methodResult);
		TestMethod testMethod = saveTestMethod(methodResult, testInstance,
				status, exception);
		saveMethodTypes(testMethod, methodResult);

		test.setFinishDate(new Date());
		suite.setFinishDate(new Date());

		updateObject(test);
		updateObject(suite);

		saveLog(methodResult, testMethod);
	}

	/**
	 * Save test method
	 * 
	 * @param methodResult
	 * @param testInstance
	 * @param status
	 * @return
	 */
	protected TestMethod saveTestMethod(ITestResult methodResult,
			TestInstance testInstance, EMethodStatus status,
			TestMethodException exception) {
		TestMethod testMethod = new TestMethod();
		testMethod.setName(methodResult.getName());
		if (!"".equals(methodResult.getMethod().getDescription()))
			testMethod
					.setDescription(methodResult.getMethod().getDescription());
		testMethod.setStartDate(methodResult.getStartMillis());
		testMethod.setFinishDate(methodResult.getEndMillis());
		testMethod.setStatus(status);
		testMethod.setTestInstance(testInstance);

		if (exception != null)
			testMethod.setException(exception);

		String[] methodGroups = methodResult.getMethod().getGroups();

		Set<Group> groupsSet = new HashSet<Group>();

		synchronized (groups) {
			for (int i = 0; i < methodGroups.length; i++) {
				Group group = groups.get(methodGroups[i]);
				if (group == null) {
					group = new Group();
					group.setName(methodGroups[i]);
					saveObject(group);
					groups.put(methodGroups[i], group);
				}
				groupsSet.add(group);
			}
		}

		testMethod.setGroups(groupsSet);

		saveObject(testMethod);
		return testMethod;
	}

	/**
	 * If suite is already created (is present in hashmap) return it; otherwise
	 * create the suite object, save it in DB and save it into hashmap
	 * 
	 * @param methodResult
	 * @return
	 */
	protected Suite saveSuite(ITestResult methodResult) {
		synchronized (suites) {
			XmlSuite xmlsuite = methodResult.getMethod().getTestClass()
					.getXmlTest().getSuite();
			Suite suite = suites.get(xmlsuite);

			if (suite == null) {
				suite = new Suite();
				suite.setName(xmlsuite.getName());
				suite.setStartDate(new Date());
				saveObject(suite);
				suites.put(xmlsuite, suite);
			}
			return suite;
		}
	}

	/**
	 * If test is already created (is present in hashmap) return it; otherwise
	 * create the test object, save it in DB and save it into hashmap
	 * 
	 * @param methodResult
	 * @param suite
	 * @return Test
	 */
	protected Test saveTest(ITestResult methodResult, Suite suite) {
		synchronized (tests) {
			XmlTest xmltest = methodResult.getTestClass().getXmlTest();
			Test test = tests.get(xmltest);

			if (test == null) {
				test = new Test();
				test.setName(xmltest.getName());
				test.setSuite(suite);
				test.setStartDate(new Date());
				saveObject(test);
				tests.put(xmltest, test);
			}

			return test;
		}
	}

	/**
	 * Save test instance into database: If test instance is already created,
	 * get it; othewise create testinstance (create testClass if needed)
	 * 
	 * then save test-to-testinstance mapping
	 * 
	 * @param methodResult
	 * @param test
	 * @return TestInstance
	 */
	protected TestInstance saveTestInstance(ITestResult methodResult, Test test) {
		TestInstance testInstance;

		synchronized (testInstances) {
			// ITestResult#getInstance() issue. Skipped 'deppendsOnMethod'
			// results return <code>null</code> instead of test instance
			Object instance = helper.getTestInstance(methodResult);

			testInstance = testInstances.get(instance);

			if (testInstance == null) {
				testInstance = new TestInstance();
				String className = methodResult.getTestClass().getName()
						.toString();

				TestClass testClass = getTestClass(className);

				if (testClass == null) {
					testClass = new TestClass();
					testClass.setName(className);

					// Save test class
					saveObject(testClass);
				}

				testInstance.setTestClass(testClass);
				testInstance.setTest(test);

				// Save test class instance
				saveObject(testInstance);

				testInstances.put(methodResult.getInstance(), testInstance);

			}
		}

		return testInstance;

	}

	/**
	 * If a testmethod has exception create it and link to testmethod
	 * 
	 * @param methodResult
	 * @param testMethod
	 */
	protected TestMethodException saveException(ITestResult methodResult) {
		TestMethodException exception = null;
		if (methodResult.getThrowable() != null) {
			exception = new TestMethodException();
			Throwable ex = methodResult.getThrowable();
			exception.setClassName(ex.getClass().getName());
			exception.setMessage(ex.getMessage());

			String stacktrace = "";
			StackTraceElement[] el = ex.getStackTrace();
			for (int i = 0; i < el.length; i++)
				stacktrace = stacktrace + el[i].toString() + "\n";

			exception.setFullStacktrace(stacktrace);
			saveObject(exception);
		}
		return exception;
	}

	/**
	 * Create TestClass by classname
	 * 
	 * @param className
	 * @return
	 */
	protected TestClass getTestClass(String className) {
		Session session = factory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			@SuppressWarnings("unchecked")
			List<TestClass> list = session.createCriteria(TestClass.class)
					.add(Expression.eq("name", className)).list();

			tx.commit();

			if (list.size() == 0)
				return null;
			else
				return list.get(0);

		} finally {
			session.close();
		}
	}

	/**
	 * Detect and save all test method types
	 * 
	 * @param method
	 * @param methodResult
	 */
	protected void saveMethodTypes(TestMethod method, ITestResult methodResult) {
		ITestNGMethod m = methodResult.getMethod();

		if (m.isAfterClassConfiguration())
			saveMethodType(method, EMethodType.AFTER_CLASS);

		if (m.isAfterGroupsConfiguration())
			saveMethodType(method, EMethodType.AFTER_GROUP);

		if (m.isAfterMethodConfiguration())
			saveMethodType(method, EMethodType.AFTER_METHOD);

		if (m.isAfterSuiteConfiguration())
			saveMethodType(method, EMethodType.AFTER_SUITE);

		if (m.isAfterTestConfiguration())
			saveMethodType(method, EMethodType.AFTER_TEST);

		if (m.isBeforeClassConfiguration())
			saveMethodType(method, EMethodType.BEFORE_CLASS);

		if (m.isBeforeGroupsConfiguration())
			saveMethodType(method, EMethodType.BEFORE_GROUP);

		if (m.isBeforeMethodConfiguration())
			saveMethodType(method, EMethodType.BEFORE_METHOD);

		if (m.isBeforeSuiteConfiguration())
			saveMethodType(method, EMethodType.BEFORE_SUITE);

		if (m.isBeforeTestConfiguration())
			saveMethodType(method, EMethodType.BEFORE_TEST);
	}

	/**
	 * Save method type
	 * 
	 * @param testMethod
	 * @param type
	 */
	protected void saveMethodType(TestMethod testMethod, EMethodType type) {
		MethodType methodType = new MethodType();
		methodType.setTestMethod(testMethod);
		methodType.setMethodType(type);
		saveObject(methodType);
	}

	/**
	 * Save Hibernate persistent object
	 * 
	 * @param obj
	 */
	protected void saveObject(Object obj) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			session.save(obj);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
		} finally {
			session.close();
		}
	}

	/**
	 * Save Hibernate persistent object
	 * 
	 * @param obj
	 */
	protected void updateObject(Object obj) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			session.update(obj);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
		} finally {
			session.close();
		}
	}

}
