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

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lohika.alp.testng.results.schema.TestInstance;
import com.lohika.alp.testng.results.schema.TestMethod;

@Controller
@RequestMapping(
		value = "/results/test-instance",
		headers = Headers.ACCEPT_XML)
public class TestInstanceController {

	private final String view = "marshallingView";

	@Autowired
	private Jaxb2Marshaller jaxb2Mashaller;

	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/{testInstanceId}")
	String getTestInstance(Model model,
			@PathVariable("testInstanceId") long testInstanceId) {

		// Get from database
		TestInstance testInstance = new TestInstance();
		testInstance.setId(testInstanceId);

		model.addAttribute(testInstance);
		return view;
	}
	
	@RequestMapping(
			method = RequestMethod.POST,
			value = "/{testInstanceId}/test-methods")
	String addTestMethod(Model model, @RequestBody String body,
			@PathVariable("suiteId") long suiteId,
			@PathVariable("testId") long testId) {
		// TODO implement XML controller

		Source source = new StreamSource(new StringReader(body));
		TestMethod testMethod = (TestMethod) jaxb2Mashaller
				.unmarshal(source);

		// Save to database

		// Set database id
		testMethod.setId(1L);

		model.addAttribute(testMethod);
		return view;
	}

}
