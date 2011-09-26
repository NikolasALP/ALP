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
package com.lohika.alp.reporter;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Transforms logs from XML to HTML
 * 
 */
public class HTMLLogTransformer {

	private Transformer transformer;

	/**
	 * Constructs {@link HTMLLogTransformer} with initial XSL template and
	 * relative path to html log data
	 * 
	 * @param xsl
	 *            initial template file
	 * @param relative
	 *            template variable that contains relative path from html log to
	 *            html data (e.g. '../html-data')
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	public HTMLLogTransformer(File xsl, String relative)
			throws TransformerConfigurationException, IOException {

		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();

		transformer = factory.newTransformer(new StreamSource(xsl));

		// Set logs-data path as template parameter
		transformer.setParameter("contextPath", relative);
	}

	/**
	 * Transform XML to HTML log
	 * 
	 * @param xml
	 *            source XML log file
	 * @param html
	 *            output HTML log file
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	public void transform(File xml, File html) throws TransformerException,
			TransformerConfigurationException, IOException {

		StreamSource source = new StreamSource(xml);
		StreamResult result = new StreamResult(html);

		// Save log into file
		transformer.transform(source, result);
	}

}
