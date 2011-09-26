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
package com.lohika.alp.log4j.xml;

import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.lohika.alp.log4j.CloneableLayout;

public class XMLLayout extends Layout implements CloneableLayout {

	private Writer buffer;

	private XMLStreamWriter xmlWriter;

	private Marshaller marshaller;

	// TODO make concatenation of default values and values provided by user
	private String contextPath = "com.lohika.alp.log.schema:com.lohika.alp.log.elements.schema";

	@Override
	public void activateOptions() {
		buffer = new StringWriter();

		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		try {
			xmlWriter = factory.createXMLStreamWriter(buffer);
			JAXBContext jc = JAXBContext.newInstance(contextPath);

			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		} catch (Exception e) {
			LogLog.error("XMLLayout error", e);
		}

	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getContentType() {
		return "text/xml";
	}

	@Override
	public String getHeader() {
		try {
			xmlWriter.writeStartDocument();
			xmlWriter.writeCharacters("\n");
			xmlWriter.writeStartElement("log");

			// TODO allow namespaces to be configured
			xmlWriter.setDefaultNamespace("http://alp.lohika.com/log/schema");
			xmlWriter.writeNamespace("", "http://alp.lohika.com/log/schema");
			xmlWriter.writeNamespace("elm",
					"http://alp.lohika.com/log/elements/schema");

			xmlWriter.writeCharacters("\n");
			xmlWriter.flush();
		} catch (XMLStreamException e) {
			LogLog.error("XMLLayout error", e);
		}

		return buffer.toString();
	}

	@Override
	public String getFooter() {
		try {
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
		} catch (XMLStreamException e) {
			LogLog.error("XMLLayout error", e);
		}

		return buffer.toString();
	}

	@Override
	public String format(LoggingEvent event) {
		try {
			xmlWriter.writeStartElement("event");
			xmlWriter.writeAttribute("level", event.getLevel() + "");
			xmlWriter.writeAttribute("thread", event.getThreadName());
			xmlWriter.writeAttribute("logger", event.getLoggerName());
			xmlWriter.writeAttribute("timestamp", event.getTimeStamp() + "");

			Object message = event.getMessage();

			if (isJAXBElement(message)) {
				try {
					marshaller.marshal(message, xmlWriter);
				} catch (JAXBException e) {
					LogLog.error("XMLLayout error", e);
				}

			} else {
				xmlWriter.writeCharacters(message.toString() + "\r\n");
			}
			
			// Handle throwable
			String[] s = event.getThrowableStrRep();
			StringBuffer sb = new StringBuffer();
			if (s != null) {
				for (int i = 0; i < s.length; i++) {
					sb.append(s[i]);
					sb.append("\r\n");
				}
				
				xmlWriter.writeCharacters(sb.toString());
			}

			xmlWriter.writeEndElement();
			xmlWriter.writeCharacters("\r\n");
		} catch (XMLStreamException e) {
			LogLog.error("XMLLayout error", e);
		}

		return buffer.toString();
	}

	@Override
	public boolean ignoresThrowable() {
		return false;
	}

	private boolean isJAXBElement(Object object) {
		if (object == null)
			return false;
			
		XmlType xmlType = object.getClass().getAnnotation(XmlType.class);

		if (xmlType == null)
			return false;

		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		XMLLayout clone = (XMLLayout) super.clone();

		clone.activateOptions();
		return clone;
	}

	@Override
	public Layout cloneLayout() throws CloneNotSupportedException {
		return (Layout) clone();
	}

}
