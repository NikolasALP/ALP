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
package com.lohika.alp.reporter.fe.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestInstance;
import com.lohika.alp.reporter.db.model.TestMethod;
import com.lohika.alp.reporter.fe.dao.SuiteDAO;
import com.lohika.alp.reporter.fe.dao.TestDAO;
import com.lohika.alp.reporter.fe.dao.TestInstanceDAO;
import com.lohika.alp.reporter.fe.dao.TestMethodDAO;
import com.lohika.alp.reporter.fe.form.TestMethodFilter;

@Controller
public class TestMethodController {
	
	private final String xmlView = "marshallingView";
	
	@Autowired
	private SuiteDAO suiteDAO;
	
	@Autowired
	private TestDAO testDAO;
	
	@Autowired
	private TestInstanceDAO testInstanceDAO;
	
	@Autowired
	private TestMethodDAO testMethodDAO;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor editor = new CustomDateEditor(format, true);
		
		binder.registerCustomEditor(Date.class, editor);
	}
	
	@ModelAttribute("testMethodFilter")
	public TestMethodFilter getTestMethodFilter() {		
		return new TestMethodFilter();
	}
	
	private void setDefaultPeriod(TestMethodFilter filter) {
		Calendar cal = Calendar.getInstance();
		// Set one day period if period is not set
		if (filter.getFrom() == null || filter.getTill() == null) {
			filter.setTill(cal.getTime());
			cal.add(Calendar.DATE, -1);
			filter.setFrom(cal.getTime());
		}
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test-method",
			headers = Headers.ACCEPT_HTML)
	public String getTestMethod(Model model, 
			@ModelAttribute("testMethodFilter") TestMethodFilter filter) {
		
		setDefaultPeriod(filter);
		
		model.addAttribute("testMethodFilter", filter);		
		model.addAttribute("testMethods", testMethodDAO.listTestMethod(filter));
		return "test-method";
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test-instance/{testInstanceId}/test-method",
			headers = Headers.ACCEPT_HTML)
	public String getTestMethodForTestInstance(Model model,
			@PathVariable("testInstanceId") long testInstanceId,
			@ModelAttribute("testMethodFilter") TestMethodFilter filter) {
		
		TestInstance testInstance = testInstanceDAO.getTestInstance(testInstanceId);		
		List<TestMethod> list = testMethodDAO.listTestMethod(filter, testInstance);
		
		model.addAttribute("testMethodFilter", filter);
		model.addAttribute("testMethods", list);
		model.addAttribute("testInstanceId", testInstanceId);
		return "test-method";
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/suite/{suiteId}/test-method",
			headers = Headers.ACCEPT_HTML)
	public String getTestMethodForSuite(Model model,
			@PathVariable("suiteId") long suiteId,
			@ModelAttribute("testMethodFilter") TestMethodFilter filter) {
		
		Suite suite = suiteDAO.getSuite(suiteId);	
		List<TestMethod> list = testMethodDAO.listTestMethod(filter, suite);
		
		model.addAttribute("testMethodFilter", filter);
		model.addAttribute("testMethods", list);
		model.addAttribute("suiteId", suiteId);
		return "test-method";
	}

	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test/{testId}/test-method",
			headers = Headers.ACCEPT_HTML)
	public String getTestMethodForTest(Model model,
			@PathVariable("testId") long testId,
			@ModelAttribute("testMethodFilter") TestMethodFilter filter) {
		
		Test test = testDAO.getTest(testId);		
		List<TestMethod> list = testMethodDAO.listTestMethod(filter, test);
		
		model.addAttribute("testMethodFilter", filter);
		model.addAttribute("testMethods", list);
		model.addAttribute("testId", testId);
		return "test-method";
	}

	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test-method/{testMethodId}",
			headers = Headers.ACCEPT_XML)
	String getTestMethodXML(Model model,
			@PathVariable("testMethodId") long testMethodId) {

		// TODO implement XML controller
		model.addAttribute(null);
		return xmlView;
	}
	
}
