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
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.lohika.alp.log4j.attributes.LogFileAttachmentAttribute;
import com.lohika.alp.log4j.attributes.LogFileAttribute;
import com.lohika.alp.reporter.helpers.Resources;
import com.lohika.alp.reporter.helpers.ResultsHelper;

/**
 * The <code>ResultsXML2HTML</code> class generates HTML file from XML results
 * file according to {@link http://alp.lohika.com/testng/results/schema}
 * 
 */
public class ResultsXML2HTML {

	private final Logger logger = Logger.getLogger(getClass());

	private final ResultsHelper helper = new ResultsHelper();

	private String resultsHtmlXslName = "results.xsl";
	private String resultsName = "results.xml";
	private String resultsHtmlName = "results.html";
	private String logsHtmlDir = "logs-html";
	private String logsDataDir = "logs-data";
	private String logHtmlXslName = "log.xsl";

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {

		// Transform XML logs to HTML
		try {
			transformLogs(suites, outputDirectory);
		} catch (Exception e) {
			logger.error("Log transformation failure", e);
		}

		// Transform XML result to HTML
		try {
			transformResults(outputDirectory);
		} catch (Exception e) {
			logger.error("HTML Log transformation failure", e);
		}
	}

	protected void transformLogs(List<ISuite> suites, String outputDirectory)
			throws IOException, TransformerException {

		// HTML logs output directory
		File logsHtml = new File(outputDirectory, logsHtmlDir);

		// HTML data output directory
		File logsData = new File(outputDirectory, logsDataDir);

		// XSL file (extracted to logs-data)
		File xsl = new File(logsData, logHtmlXslName);

		// Relative path from HTML logs to logs-data directory
		String relative = "../" + logsDataDir;

		if (!logsHtml.exists())
			logsHtml.mkdir();

		if (!logsData.exists())
			logsData.mkdir();

		// Copy logs-data from resources to output directory
		Resources resources = new Resources();
		resources.copy(getClass(), logsDataDir + "/", logsData);

		HTMLLogTransformer transformer = new HTMLLogTransformer(xsl, relative);

		// Transform to HTML logs for each test result
		for (ISuite suite : suites) {
			for (ISuiteResult test : suite.getResults().values()) {

				List<ITestResult> results = helper.getAllTestResults(test);

				for (ITestResult result : results) {
					// Get XML log file stored as ITestResult attribute
					File xml = LogFileAttribute.getLogFile(result);

					if (xml != null) {
						// Using name of XML log for HTML log
						String name = xml.getName().replace(".xml", "");

						// Set HTML file extension
						name = name + ".html";
						File html = new File(logsHtml, name);

						transformer.transform(xml, html);
					}

					// Copy log attachments
					List<File> attachments = LogFileAttachmentAttribute
							.getAttachmentFiles(result);
					
					if (attachments != null) {
						for (File attachment : attachments) {
							FileUtils.copyFileToDirectory(attachment, logsHtml);
						}
					}
				}
			}
		}
	}

	protected void transformResults(String outputDirectory) throws IOException,
			TransformerException {
		// HTML data output directory
		File logsData = new File(outputDirectory, logsDataDir);
		// XSL file (extracted to logs-data)
		File xsl = new File(logsData, resultsHtmlXslName);
		// XML file (created xml log file)
		File xml = new File(outputDirectory, resultsName);
		// XML file (created html log file)
		File html = new File(outputDirectory, resultsHtmlName);

		HTMLLogTransformer transformer = new HTMLLogTransformer(xsl, null);
		transformer.transform(xml, html);

		logger.info("Suite results: " + html.toURI().toURL());
	}
}
