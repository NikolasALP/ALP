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

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class LogUploader {

	private String server;
	
	public LogUploader() {
		ReporterPropertiesLoader propsLoader = new ReporterPropertiesLoader();
		ReporterProperties props = propsLoader.getProperties();
		
		server = props.getServer();
	}

	public void upload(long testMethodId, File logFile)
			throws ClientProtocolException, IOException {
		String url = server + "/results/test-method/" + testMethodId + "/log";
		
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url);

		MultipartEntity mpEntity = new MultipartEntity();

		ContentBody cbFile = new FileBody(logFile);

		mpEntity.addPart("fileData", cbFile);

		httppost.setEntity(mpEntity);

		httpclient.execute(httppost);
	}

}
