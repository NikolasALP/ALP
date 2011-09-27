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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.lohika.alp.log.elements.schema.Action;
import com.lohika.alp.log.elements.schema.ObjectFactory;
import com.lohika.alp.log.elements.schema.Screenshot;
import com.lohika.alp.log.elements.schema.Webelement;
import com.lohika.alp.log4j.LogFileAttachment;

public class LogElementsSeleniumFactoryJAXB implements
		LogElementsSeleniumFactory {

	protected ObjectFactory factory = new ObjectFactory();

	protected Action getAction(DescribedElement self, String name) {
		Action action = factory.createAction();

		action.setName(name);
		action.setWebelement((Webelement) element(self));

		return action;
	}

	@Override
	public Object element(DescribedElement element) {
		Webelement webelement = factory.createWebelement();

		if (element != null && element.getDescription() != null) {
			webelement.setName(element.getDescription().getName());
			webelement.setType(element.getDescription().getType());
		}

		return webelement;
	}

	@Override
	public Object get(DescribedElement self, String url) {
		Action action = getAction(self, "get");

		action.getArg().add(url);
		return action;
	}

	@Override
	public Object close(DescribedElement self) {
		return getAction(self, "close");
	}

	@Override
	public Object quit(DescribedElement self) {
		return getAction(self, "quit");
	}

	@Override
	public Object click(DescribedElement self) {
		return getAction(self, "click");
	}

	@Override
	public Object submit(DescribedElement self) {
		return getAction(self, "click");
	}

	@Override
	public Object sendKeys(DescribedElement self, CharSequence... keysToSend) {
		Action action = getAction(self, "send keys");

		StringBuilder builder = new StringBuilder();
		for (CharSequence key : keysToSend) {
			builder.append(key);
		}

		action.getArg().add(builder.toString());

		return action;
	}

	@Override
	public Object clear(DescribedElement self) {
		return getAction(self, "clear");
	}

	@Override
	public Object toggle(DescribedElement self) {
		return getAction(self, "toggle");
	}

	@Override
	public Object setSelected(DescribedElement self) {
		return getAction(self, "set selected");
	}

	@Override
	public Object hover(DescribedElement self) {
		return getAction(self, "hover");
	}

	@Override
	public Object dragAndDropBy(DescribedElement self, int moveRightBy,
			int moveDownBy) {
		Action action = getAction(self, "drag and drop by");

		action.getArg().add(moveRightBy);
		action.getArg().add(moveDownBy);

		return action;
	}

	@Override
	public Object dragAndDropOn(DescribedElement self, DescribedElement element) {
		Action action = getAction(self, "drag and drop by");

		action.getArg().add(element);

		return action;
	}

	@Override
	public Object screenshot(TakesScreenshot takesScreenshot, String description) {

		Screenshot screenshot = factory.createScreenshot();
		screenshot.setDescription(description);

		File tempFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

		File attachmentFile = null;
		try {
			attachmentFile = LogFileAttachment.getAttachmentFile("", "png");
			FileUtils.copyFile(tempFile, attachmentFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (attachmentFile != null)
			screenshot.setUrl(attachmentFile.getName());

		return screenshot;
	}

	@Override
	public Object screenshot(WebDriver driver, String description) {

		if (driver instanceof TakesScreenshot) {
			return screenshot((TakesScreenshot) driver, description);
		} else {
			return null;
		}
	}
	
}
