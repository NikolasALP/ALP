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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>Java class of ALP configuration
 * <p>Read all parameter from environment.properties file. These parameters are uses
 * for ALP at all.
 * <p>jsErrorAutolog allow to logs js errors automatically
 * hosts is an array which contains each host separated by comma 
 * which is uses in our projects
 * @author Dmitry Irzhov
 */
public class Configuration {

	private final Logger log = Logger.getLogger(getClass());

	private static Configuration instance = null;
	private Boolean jsErrorAutolog = false;
	private String[] hosts;
	
	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public Boolean getJsErrorAutolog() {
		return jsErrorAutolog;
	}

	public void setJsErrorAutolog(Boolean jsErrorAutolog) {
		this.jsErrorAutolog = jsErrorAutolog;
	}

	private Configuration() {
		// read environment.properties file
		Properties props = new Properties();
        try {
			props.load(new FileInputStream("environment.properties"));
	        Boolean b = Boolean.parseBoolean(props.getProperty("jserrcatcher.autolog"));
	        if (b!=null) jsErrorAutolog=b;
	        
	        String hostStr = props.getProperty("jserrcatcher.hosts");
	        if (jsErrorAutolog==true)
	        if (hostStr!=null) {
		        hosts = hostStr.split(",");
		        for (String host : hosts) {
		        	URL url = new URL(host);
		        	url.openConnection();
		        }
	        } else {
	        	jsErrorAutolog = false;
	        	log.warn("Unable to read 'jserrcatcher.hosts' parameter in the 'environment.properties' file. Auto logging of js errors was diactivated.");
	        }
		} catch (MalformedURLException e) {
        	jsErrorAutolog = false;
        	log.warn("Wrong URL in 'jserrcatcher.hosts' parameter. Auto logging of js errors was diactivated.");
		} catch (IOException e)
        {
        	jsErrorAutolog = false;
            log.error("Load properties",e.getCause());
        }
	}
	
	public static Configuration getInstance() {
		if (instance==null)
			instance = new Configuration();
		return instance;
	}

}
