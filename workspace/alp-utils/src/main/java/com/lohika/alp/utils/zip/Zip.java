package com.lohika.alp.utils.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * This class is uses for archive files and folder to zip
 * @author Dmitry Irzhov
 *
 */

public class Zip {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	static final int BUFFER = 2048;
	private BufferedInputStream origin;
	private FileOutputStream dest;
	private CheckedOutputStream checksum;
	private ZipOutputStream out;
	
	private String zipFile;
	
	public String getZipFile() {
		return zipFile;
	}

	public Zip(String zipFile) {
		this.zipFile = zipFile;
		try {
			origin = null;
			dest = new FileOutputStream(zipFile);
			checksum = new CheckedOutputStream(dest, new Adler32());
			out = new ZipOutputStream(new 
					BufferedOutputStream(checksum));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String argv[]) throws Exception {
		String dir = "c:\\ALP-Examples\\ALP-Sanity\\test-output";
	
		Zip zip = new Zip("c:\\test.zip");
		zip.add(dir+File.separatorChar+"results.html");
		zip.add(dir+File.separatorChar+"logs-data");
		zip.add(dir+File.separatorChar+"logs-html");
		zip.close();
	}
	
	public void add(String filePath) throws IOException {
		//out.setMethod(ZipOutputStream.DEFLATED);
		byte data[] = new byte[BUFFER];

		// get a list of files from current directory
		File f = new File(filePath);
		if (f.isDirectory()) {
			File files[] = f.listFiles();
			for (int i=0; i<files.length; i++) {
				add(files[i].getAbsolutePath());
			}
		} else {
			logger.debug("Adding: "+filePath);
			FileInputStream fi = new 
					FileInputStream(filePath);
			origin = new 
					BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(filePath);
			out.putNextEntry(entry);
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		}
		//out.close();
		logger.debug("checksum: "+checksum.getChecksum().getValue());
	}

	public void close() {
		if (out != null)
			try {
				out.close();
			} catch (IOException e){}
	}

	public static byte[] readFileAsBytes(String filePath) {
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } catch (Exception e) {
		} finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return buffer;
	}
}
