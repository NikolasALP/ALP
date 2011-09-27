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

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

public class LoggingWebElement implements WebElement, DescribedElement,
		Locatable, WrapsElement {

	protected final Logger logger = Logger.getLogger(getClass());

	protected final WebElement element;

	protected final LogElementsSeleniumFactory factory;

	protected LogDescriptionBean description;

	public LoggingWebElement(WebElement element,
			LogElementsSeleniumFactory factory) {

		if (element == null || factory == null) {
			throw new IllegalArgumentException("Parameters can't be null");
		}

		this.element = element;
		this.factory = factory;
	}

	@Override
	public WebElement getWrappedElement() {
		return element;
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
	public boolean equals(Object obj) {
		if (!(obj instanceof WebElement)) {
			return false;
		}

		WebElement other = (WebElement) obj;
		if (other instanceof WrapsElement) {
			other = ((WrapsElement) other).getWrappedElement();
		}

		return element.equals(other);
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public void click() {
		logger.info(factory.click(this));

		element.click();
	}

	@Override
	public void submit() {
		logger.info(factory.submit(this));

		element.click();
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		logger.info(factory.sendKeys(this, keysToSend));

		element.sendKeys(keysToSend);
	}

	@Override
	public void clear() {
		logger.info(factory.clear(this));

		element.clear();
	}

	@Override
	public String getTagName() {
		return element.getTagName();
	}

	@Override
	public String getAttribute(String name) {
		return element.getAttribute(name);
	}

	@Override
	public boolean isSelected() {
		return element.isSelected();
	}

	@Override
	public boolean isEnabled() {
		return element.isEnabled();
	}

	@Override
	public String getText() {
		return element.getText();
	}

	@Override
	public List<WebElement> findElements(By by) {
		List<WebElement> children = element.findElements(by);
		List<WebElement> loggedChildren = new ArrayList<WebElement>();

		for (WebElement child : children) {
			loggedChildren.add(new LoggingWebElement(child, factory));
		}

		return children;
	}

	@Override
	public WebElement findElement(By by) {
		WebElement child = element.findElement(by);
		WebElement loggedChild = new LoggingWebElement(child, factory);

		return loggedChild;
	}

	@Override
	public String getCssValue(String propertyName) {
		return element.getCssValue(propertyName);
	}

	@Override
	public Point getLocation() {
		return element.getLocation();
	}

	@Override
	public Dimension getSize() {
		return element.getSize();
	}

	@Override
	public boolean isDisplayed() {
		return element.isDisplayed();
	}

	@Override
	public Point getLocationOnScreenOnceScrolledIntoView() {
		if (element instanceof Locatable) {
			return ((Locatable) element)
					.getLocationOnScreenOnceScrolledIntoView();
		}

		throw new UnsupportedOperationException(
				"Underlying element instance does not support location");
	}

	@Override
	public Coordinates getCoordinates() {
		if (element instanceof Locatable) {
			return ((Locatable) element).getCoordinates();
		}

		throw new UnsupportedOperationException(
				"Underlying element instance does not support location");
	}

}
