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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class LogDescriptionDefaultFieldDecorator extends DefaultFieldDecorator {

	public LogDescriptionDefaultFieldDecorator(ElementLocatorFactory factory) {
		super(factory);
	}

	@Override
	protected WebElement proxyForLocator(ClassLoader loader,
			ElementLocator locator) {
		InvocationHandler handler = new LogDescriptionLocatingElementHandler(
				locator);

		WebElement proxy;

		// Temporary fix for
		// http://code.google.com/p/selenium/issues/detail?id=1754
		proxy = (WebElement) Proxy.newProxyInstance(loader, new Class[] {
				WebElement.class, WrapsElement.class, Locatable.class },
				handler);
		return proxy;
	}

}
