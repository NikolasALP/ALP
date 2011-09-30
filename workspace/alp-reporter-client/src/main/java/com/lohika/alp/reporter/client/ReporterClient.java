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
package com.lohika.alp.reporter.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.client.RestOperations;

import com.lohika.alp.testng.results.schema.Suite;

public class ReporterClient {

	private final RestOperations restTemplate;

	public ReporterClient(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void addSuite() {

		String suiteURL = "http://localhost:8080/alp-reporter-fe/results/suite/";
		
		Suite suite = new Suite();
		suite.setName("Smoke");
		
		suite = restTemplate.postForObject(suiteURL,
				suite, Suite.class);

		System.out.println(suite.getId());

	}

	public static void main(String[] args) {

		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");

		ReporterClient client = applicationContext.getBean("reporterClient",
				ReporterClient.class);

		client.addSuite();
	}

}
