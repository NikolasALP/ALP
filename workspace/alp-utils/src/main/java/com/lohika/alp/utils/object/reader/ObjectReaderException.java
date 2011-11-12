package com.lohika.alp.utils.object.reader;

public class ObjectReaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5253648746746360191L;

	public ObjectReaderException(String message) {
		super(message);
	}
	
	public ObjectReaderException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
