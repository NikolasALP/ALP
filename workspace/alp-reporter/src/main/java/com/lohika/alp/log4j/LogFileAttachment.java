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
package com.lohika.alp.log4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;
import org.testng.Reporter;

import com.lohika.alp.log4j.attributes.LogFileAttachmentAttribute;
import com.lohika.alp.log4j.attributes.LogFileAttribute;

public class LogFileAttachment {

	// Attachment name should be at least 3 characters length
	// TODO add arguments check
	public static File getAttachmentFile(String name, String type)
			throws IOException {

		List<File> attachments;

		ITestResult tr = Reporter.getCurrentTestResult();

		synchronized (tr) {
			attachments = LogFileAttachmentAttribute.getAttachmentFiles(tr);

			if (attachments == null) {
				attachments = new ArrayList<File>();
				LogFileAttachmentAttribute.setAttachmentFiles(tr, attachments);
			}
		}

		File log = LogFileAttribute.getLogFile(tr);

		File attachment;

		if (log != null) {
			String fileName = log.getName() + "-" + name + attachments.size()
					+ "." + type;

			attachment = new File(log.getParentFile(), fileName);

		} else {
			// This is a case, when no TestFileAppender is used or log file
			// isn't created yet
			attachment = File.createTempFile(name, type);
		}

		attachments.add(attachment);

		return attachment;
	}

}
