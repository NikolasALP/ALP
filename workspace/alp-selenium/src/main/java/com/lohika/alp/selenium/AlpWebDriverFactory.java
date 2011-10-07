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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.lohika.alp.selenium.configurator.WebDriverConfigurator;
import com.lohika.alp.selenium.log.LogElementsSeleniumFactory;
import com.lohika.alp.selenium.log.LogElementsSeleniumFactoryJAXB;
import com.lohika.alp.selenium.log.LoggingWebDriver;
import com.lohika.alp.selenium.log.LoggingWebDriverListener;

public class AlpWebDriverFactory {

	public static WebDriver getDriver(String url,
			DesiredCapabilities capabilities) throws MalformedURLException {

		Logger alpSeleniumLogger = Logger.getLogger("com.lohika.alp.selenium");
		if (alpSeleniumLogger.getLevel() == null)
			alpSeleniumLogger.setLevel(Level.DEBUG);

		URL remoteAddress = new URL(url);

		CommandExecutor commandExecutor = new HttpCommandExecutor(remoteAddress);

		WebDriver driver;

		// Set name of WebDriver to be shown in log
		String name = capabilities.getBrowserName();
		
		// Configure capabilities for webdriver to use js error catcher (only FF support now)
		WebDriverConfigurator webDriverConfigurator = new WebDriverConfigurator(name);
		capabilities = webDriverConfigurator.configure(capabilities);
		
		// Create remote WebDriver instance
		driver = new RemoteWebDriverTakeScreenshotFix(commandExecutor,
				capabilities);

		// Set factory for log objects
		LogElementsSeleniumFactory logObjectsFactory = new LogElementsSeleniumFactoryJAXB();

		// Register WebDriver event listener to handle exceptions
		// TODO initialize factory separately
		LogElementsSeleniumFactory elementsFactory = new LogElementsSeleniumFactoryJAXB();
		WebDriverEventListener listener = new LoggingWebDriverListener(
				elementsFactory);

		driver = new EventFiringWebDriver(driver);
		((EventFiringWebDriver) driver).register(listener);

		// Wrap WebDriver with logging facility
		// FIXME Do not use 'instanceof' in logging decorators
		// Otherwise LoggingWebDriver can be only the last in the decorators chain
		driver = new LoggingWebDriver(driver, name, logObjectsFactory);

		return driver;
	}

}
