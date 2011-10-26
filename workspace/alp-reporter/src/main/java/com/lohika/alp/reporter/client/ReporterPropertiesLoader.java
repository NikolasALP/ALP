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
package com.lohika.alp.reporter.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ReporterPropertiesLoader {

	Logger logger = Logger.getLogger(getClass());

	public ReporterProperties getProperties() {
		ReporterProperties result = new ReporterProperties();

		Properties properties = new Properties();

		InputStream is = getClass().getResourceAsStream("reporter.properties");

		if (is == null)
			is = getClass().getResourceAsStream("/reporter.properties");

		if (is == null) {
			logger.warn("Cannot load reporter.properties file. "
					+ "Using default values");
		} else {
			try {
				properties.load(is);
			} catch (IOException e) {
				logger.error(e);
			}
		}

		String server = properties.getProperty("server");

		if (server != null) {
			result.setServer(server);
		} else {
			// TODO Write warning if there isn't configuration file
			result.setServer("http://localhost:8080/alp-reporter-fe");
		}

		return result;
	}

}
