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
package com.lohika.alp.selenium.configurator;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.lohika.alp.selenium.jscatcher.JsErrorCatcherConfiguration;

/**
 * Java class which configures Firefox browser with custom profile
 * 
 * @author Dmitry Irzhov
 *
 */
public class FirefoxDriverConfigurator implements IWebDriverConfigurator {

	public DesiredCapabilities configure(DesiredCapabilities capabilities) {
		
		if (JsErrorCatcherConfiguration.getInstance().getAllowDomains() == null)
			return capabilities;

		FirefoxProfile profile = new FirefoxProfile();
		// enable access to XPCComponents
		profile.setPreference("signed.applets.codebase_principal_support", true);
		int i=0;
		for (String host: JsErrorCatcherConfiguration.getInstance().getAllowDomains()) {
			profile.setPreference("capability.principal.codebase.p"+i+".granted", "UniversalXPConnect UniversalBrowserRead UniversalBrowserWrite UniversalPreferencesRead UniversalPreferencesWrite UniversalFileRead");
			profile.setPreference("capability.principal.codebase.p"+i+".id", host);
			i++;
		}
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		return capabilities;
	}

}
