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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestSummary;
import com.lohika.alp.reporter.fe.dao.SuiteDAO;
import com.lohika.alp.reporter.fe.dao.TestDAO;
import com.lohika.alp.reporter.fe.form.TestFilter;

@Controller
public class TestController {

	private final String view = "marshallingView";
	private final String FROM = "from";
	private final String TILL = "till";
	
	@Autowired
	private TestDAO testDAO;
	
	@Autowired
	private SuiteDAO suiteDAO;
	
	@ModelAttribute("testFilter")
	public TestFilter getTestFilter() {
		TestFilter filter = new TestFilter();
		return filter;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor editor = new CustomDateEditor(format, true);
		
		binder.registerCustomEditor(Date.class, editor);
	}
	
	private void setDefaultPeriod(TestFilter filter) {
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
			value = "/results/test",
			headers = Headers.ACCEPT_HTML)
	public String getTest(@CookieValue(value = FROM, required = false) String from,
			@CookieValue(value = TILL, required = false) String till,
			Model model, 
			@ModelAttribute("testFilter") TestFilter filter) throws Exception {
		
		setDefaultPeriod(filter);
		
		// set times from the cookies
		if (from!=null && till!=null) {
			DateFormat formatter = new SimpleDateFormat("yy-MM-dd");
		    Date f = (Date)formatter.parse(from);
		    Date t = (Date)formatter.parse(till);
			filter.setFrom(f);
			filter.setTill(t);
		}
		
		List<Test> list = testDAO.listTest(filter);
		Map<Test, TestSummary> map = testDAO.getTestSummaryMap(list);
		
		model.addAttribute("tests", list);
		model.addAttribute("summaryMap", map);
		model.addAttribute("testFilter", filter);	
		
		return "test";
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/suite/{suiteId}/test",
			headers = Headers.ACCEPT_HTML)
	public String getTestForSuite(Model model,
			@PathVariable("suiteId") long suiteId,
			@ModelAttribute("testFilter") TestFilter filter) {
		
		Suite suite = suiteDAO.getSuite(suiteId);
		
		List<Test> list = testDAO.listTest(filter, suite);
		Map<Test, TestSummary> map = testDAO.getTestSummaryMap(list);
		
		model.addAttribute("tests", list);
		model.addAttribute("summaryMap", map);
		model.addAttribute("testFilter", filter);
		model.addAttribute("suiteId", suiteId);
		
		return "test";
	}

	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/results/test/{testId}",
			headers = Headers.ACCEPT_XML)
	String getTest(Model model,
			@PathVariable("testId") long testId) {
		// TODO implement XML controller
		
		model.addAttribute(null);
		return view;
	}
	
	@RequestMapping(
			method = RequestMethod.POST, 
			value = "/results/test/{testId}/test-instances",
			headers = Headers.CONTENT_TYPE_XML)
	String addTestInstance(Model model, @RequestBody String body,
			@PathVariable("testId") long testId) {
		// TODO implement XML controller

		model.addAttribute(null);
		return view;
	}

}
