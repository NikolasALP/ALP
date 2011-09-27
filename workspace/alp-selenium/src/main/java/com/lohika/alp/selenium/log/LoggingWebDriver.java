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
package com.lohika.alp.selenium.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class LoggingWebDriver implements WebDriver, JavascriptExecutor,
		HasInputDevices, HasCapabilities, TakesScreenshot, WrapsDriver,
		DescribedElement {

	protected final Logger logger = Logger.getLogger(getClass());

	protected final WebDriver driver;

	protected final LogElementsSeleniumFactory factory;

	protected LogDescriptionBean description = new LogDescriptionBean();

	public LoggingWebDriver(WebDriver driver, String name,
			LogElementsSeleniumFactory factory) {

		if (driver == null || name == null || factory == null) {
			throw new IllegalArgumentException("Parameters can't be null");
		}

		this.driver = driver;
		this.factory = factory;

		description.setName(name);
		description.setType("driver");
	}

	@Override
	public WebDriver getWrappedDriver() {
		return driver;
	}

	@Override
	public void setDescription(LogDescriptionBean description) {
		this.description = description;
	}

	@Override
	public LogDescriptionBean getDescription() {
		return description;
	}

	@Override
	public void get(String url) {
		logger.info(factory.get(this, url));

		driver.get(url);
	}

	@Override
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return driver.getTitle();
	}

	protected WebElement createWebElement(WebElement from) {
		return new LoggingWebElement(from, factory);
	}

	@Override
	public List<WebElement> findElements(By by) {
		List<WebElement> elemets = driver.findElements(by);

		List<WebElement> loggedElements = new ArrayList<WebElement>();

		for (WebElement element : elemets) {
			loggedElements.add(createWebElement(element));
		}

		return loggedElements;
	}

	@Override
	public WebElement findElement(By by) {
		WebElement element = driver.findElement(by);
		WebElement loggedElement = createWebElement(element);

		return loggedElement;
	}

	@Override
	public String getPageSource() {
		return driver.getPageSource();
	}

	@Override
	public void close() {
		logger.info(factory.close(this));

		driver.close();
	}

	@Override
	public void quit() {
		logger.info(factory.quit(this));
		driver.quit();
	}

	@Override
	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		// TODO wrap TargetLocator
		return driver.switchTo();
	}

	@Override
	public Navigation navigate() {
		// TODO wrap Navigation
		return driver.navigate();
	}

	@Override
	public Options manage() {
		// TODO wrap Options
		return driver.manage();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target)
			throws WebDriverException {
		if (driver instanceof TakesScreenshot) {
			return ((TakesScreenshot) driver).getScreenshotAs(target);
		}
		throw new UnsupportedOperationException(
				"Underlying driver instance does not support taking screenshots");
	}

	@Override
	public Object executeScript(String script, Object... args) {
		if (driver instanceof JavascriptExecutor) {

			Object result = ((JavascriptExecutor) driver).executeScript(script,
					args);

			return result;
		}
		throw new UnsupportedOperationException(
				"Underlying driver instance does not support executing javascript");
	}

	@Override
	public Object executeAsyncScript(String arg0, Object... arg1) {
		if (driver instanceof JavascriptExecutor) {
			return ((JavascriptExecutor) driver).executeAsyncScript(arg0, arg1);
		}

		throw new UnsupportedOperationException(
				"Underlying driver instance does not support executing javascript");
	}

	@Override
	public Capabilities getCapabilities() {
		// This 'if' handle case when LoggingWebDriver.driver is instanceof EventFiringWebDriver . 
		//In this case RemoteWebDriver is field within EventFiringWebDriver while EventFiringWebDriver don't support getting Capabilities
		if (driver instanceof EventFiringWebDriver
				&& ((EventFiringWebDriver) driver).getWrappedDriver() instanceof HasCapabilities) {
			return ((HasCapabilities) ((EventFiringWebDriver) driver)
					.getWrappedDriver()).getCapabilities();
		} else if (driver instanceof HasCapabilities) {
			return ((HasCapabilities) driver).getCapabilities();
		}

		throw new UnsupportedOperationException(
				"Underlying driver instance does not support capabilities");
	}

	@Override
	public Keyboard getKeyboard() {
		if (driver instanceof HasInputDevices) {
			return ((HasInputDevices) driver).getKeyboard();
		}

		throw new UnsupportedOperationException(
				"Underlying driver instance does not support input devices");
	}

	@Override
	public Mouse getMouse() {
		if (driver instanceof HasInputDevices) {
			return ((HasInputDevices) driver).getMouse();
		}

		throw new UnsupportedOperationException(
				"Underlying driver instance does not support input devices");
	}

}
