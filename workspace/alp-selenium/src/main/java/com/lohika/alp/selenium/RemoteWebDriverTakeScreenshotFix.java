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

import java.net.URL;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Fixing issue that RemoteWebDriver not implement
 * 
 */
public class RemoteWebDriverTakeScreenshotFix extends RemoteWebDriver implements
		TakesScreenshot {

	public RemoteWebDriverTakeScreenshotFix(URL remoteAddress, DesiredCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}
	
	public RemoteWebDriverTakeScreenshotFix(CommandExecutor commandExecutor,
			DesiredCapabilities capabilities) {
		super(commandExecutor, capabilities);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target)
			throws WebDriverException {
		// Get the screenshot as base64.
		String base64 = execute(DriverCommand.SCREENSHOT).getValue().toString();
		// ... and convert it.
		return target.convertFromBase64Png(base64);
	}

}
