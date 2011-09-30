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
package com.lohika.alp.selenium;

import junit.framework.ComparisonFailure;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.lohika.alp.selenium.log.LogElementsSeleniumFactory;
import com.lohika.alp.selenium.log.LogElementsSeleniumFactoryJAXB;

/**
 * Extending assertion error messages with screenshot
 * 
 */
public class WebDriverAssert extends junit.framework.Assert {

	private static Logger logger = Logger.getLogger(WebDriverAssert.class);

	// TODO initialize factory separately
	private static LogElementsSeleniumFactory elementsFactory = new LogElementsSeleniumFactoryJAXB();

	static public void assertEquals(WebDriver driver, String expected,
			String actual) {

		try {
			assertEquals(expected, actual);
		} catch (ComparisonFailure tr) {
			logger.error("", tr);
			String descr = tr.getClass().getName();
			Object screenshot = elementsFactory.screenshot(driver, descr);
			logger.error(screenshot);

			throw tr;
		}

	}

}
