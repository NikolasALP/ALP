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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.fe.dao.TestDAO;
import com.lohika.alp.reporter.fe.form.TestFilter;

/**
 * Experimental controller to try pagination and data sorting from server side
 *
 */
@Controller
public class TestControllerAjax {
	
	@Autowired
	private TestDAO testDAO;
	
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results_a/test",
			headers = Headers.ACCEPT_HTML)
	public String getTest() {
		return "test_ajax";
	}

	@RequestMapping(
			method = RequestMethod.GET,
			value = "/results_a/test/ajax")
	public @ResponseBody
	String getTestajax(
			@RequestParam String sEcho,
			@RequestParam String _,
			@RequestParam int iDisplayLength,
			@RequestParam int iDisplayStart) {
		
		TestFilter filter = new TestFilter();
		
		List<Test> tests = testDAO.listTest(filter);
		
		int totalRecords = tests.size();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"sEcho\":" + sEcho +",");
		sb.append("\"iTotalRecords\": " + totalRecords + ",");
		sb.append("\"iTotalDisplayRecords\": " + totalRecords + ",");
		sb.append("\"aaData\": [");
		for (Test test : tests) {
			sb.append("{");
			sb.append("\"id\":\"" + test.getSuite().getId() + "\",");
			sb.append("\"suite\":\"" + test.getSuite().getName() + "\",");
			sb.append("\"section\":\"" + test.getName() + "\",");
			sb.append("\"total\":\"0\",");
			sb.append("\"failed\":\"0\",");
			sb.append("\"skipped\":\"0\",");
			sb.append("\"test id\":\"-\",");
			sb.append("},");
		}
		sb.append("]");
		sb.append("}");
		
		return sb.toString();
	}

}
