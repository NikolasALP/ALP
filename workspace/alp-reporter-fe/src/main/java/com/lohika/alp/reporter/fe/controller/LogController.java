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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.lohika.alp.reporter.db.model.TestMethod;
import com.lohika.alp.reporter.fe.dao.TestMethodDAO;
import com.lohika.alp.reporter.fe.form.UploadItem;
import com.lohika.alp.reporter.fe.logs.LogStorage;

@Controller
public class LogController {
	
	@Autowired
	LogStorage logStorage;
	
	@Autowired
	private TestMethodDAO testMethodDAO;
	
	@ModelAttribute("uploadItem")
	public UploadItem getLogFile() {
		return new UploadItem();
	}

	@RequestMapping(
			method = RequestMethod.POST,
			value = "/results/test-method/{testMethodId}/log")
	void saveLog(@PathVariable("testMethodId") long id,
			@ModelAttribute("uploadItem") UploadItem uploadItem,
			HttpServletResponse response) 
	throws IOException {		

		CommonsMultipartFile fileData = uploadItem.getFileData();

		// TODO Handle unexpected form data
		String name = fileData.getOriginalFilename();
		InputStream is = fileData.getInputStream();

		// If file extension is '.xml' or none save it as 'index.xml'
		if (name.toLowerCase().endsWith(".xml")
				|| !name.matches(".*\\.\\w{1,3}$")) {

			logStorage.saveLog(id, "index.xml", is);
			
			// TODO TestMethod database creation should be performed with Spring
			// and REST web services, not from test listeners directly
			TestMethod testMethod = testMethodDAO.getTestMethod(id);
			
			// Set into DB that TestMethod has index log file
			testMethod.setHasLog(true);
			testMethodDAO.saveTestMethod(testMethod);
		} else {
			// Else save it with its original name

			logStorage.saveLog(id, name, is);
		}
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		// TODO add log URL to response according REST principles
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test-method/{testMethodId}/log/")
	String getLog(Model model,
			HttpServletRequest request,
			@PathVariable("testMethodId") long id) throws IOException {
		
			InputStream is = logStorage.getLog(id, "index.xml");
			model.addAttribute(is);
			
			// add return URL for 'Back' button
			model.addAttribute("backButtonUrl", request.getHeader("referer"));
		
			// Set relative context path
			model.addAttribute("contextPath", "../../../..");
		
			return "log";
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results/test-method/{testMethodId}/log/{name}")
	void getLogBinary(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("testMethodId") long id) throws IOException {
		
		String uri = request.getRequestURI();
			
			String[] parts = uri.split("/");
			String name = parts[parts.length - 1];
			
			InputStream is = logStorage.getLog(id, name);
			
			OutputStream os = response.getOutputStream();
			
			int c;
			while ((c = is.read()) != -1)
				os.write(c);
	}

}
