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
package com.lohika.alp.selenium.jscatcher;

import java.util.ArrayList;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/*
 * Firefox implementation of javascript error catcher. 
 */

public class FirefoxJsErrorCathcer implements JSErrorCatcher {

	private WebDriver driver;
	
	public FirefoxJsErrorCathcer(WebDriver driver) {
		this.driver = driver;
	}
	
	/*
	 * return list of javascript errors
	 * @see com.lohika.alp.selenium.jscatcher.JSErrorCatcher#getJsErrors()
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getJsErrors() throws JsErrorCatcherException {
		if (JsErrorCatcherConfiguration.getInstance().getAllowDomains()==null)
			throw new JsErrorCatcherException("Unable to get JS errors. Need to provide allowed domains in the config file");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String script = "netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect UniversalBrowserRead UniversalBrowserWrite UniversalPreferencesRead UniversalPreferencesWrite UniversalFileRead');";
		script += "var consoleService = Components.classes[\"@mozilla.org/consoleservice;1\"].getService(Components.interfaces.nsIConsoleService);";
		script += "var errors = {};";
		script += "var count = {};";
		script += "consoleService.getMessageArray(errors, count);";
		script += "var r = [];";
		script += "for (var i=0; i<errors.value.length; i++) {";
		script += "msg = errors.value[i];";
		script += "if (msg instanceof Components.interfaces.nsIScriptError) {";
		script += "msg.QueryInterface(Components.interfaces.nsIScriptError);";
		script += "if (msg.category=='HUDConsole'";
		script += " || msg.category=='content javascript'";
		script += " || msg.category=='CSS Parser'";
		script += " || msg.category=='CSS Loader'";
		script += " || msg.category=='DOM Events'";
		script += " || msg.category=='DOM:HTML'";
		script += " || msg.category=='DOM Window'";
		script += " || msg.category=='SVG'";
		script += " || msg.category=='ImageMap'";
		script += " || msg.category=='HTML'";
		script += " || msg.category=='Canvas'";
		script += " || msg.category=='DOM3 Load'";
		script += " || msg.category=='DOM'";
		script += " || msg.category=='malformed-xml'";
		script += " || msg.category=='DOM Worker javascript')";
		script += "r.push(msg.message);";
		script += "}};";
		script += "consoleService.reset();";
		script += "return r;";
		
		return((ArrayList<String>)js.executeScript(script));
	}

}
