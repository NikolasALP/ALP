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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;

/**
 * A container to keep all {@link Appender} instances of a test method, where
 * each appender belongs to its own {@link TestAppenderDispatcher}
 * <p>
 * Lock objects is used to separate appenders per their dispatchers.
 * <p>
 * No operations are allowed if Appenders are already closed.
 * <p>
 * Not synchronized.
 * 
 * @author Mikhail Holovaty
 * 
 */
public class Appenders {

	// If appenders is null, Appenders are already closed
	private Map<Object, Appender> appenders = new HashMap<Object, Appender>();

	/**
	 * Returns the appender of related test method for the given lock object
	 * 
	 * @param lock
	 * @return
	 * @throws AppendersClosedException
	 */
	public Appender getAppender(Object lock)
			throws AppendersClosedException {
		
		if (appenders == null)
			throw new AppendersClosedException();

		return appenders.get(lock);
	}

	/**
	 * Puts the appender of related test method and associates it with the given
	 * lock object.
	 * 
	 * @param lock
	 * @param appender
	 * @throws AppendersClosedException
	 */
	public void putAppender(Object lock, Appender appender)
			throws AppendersClosedException {

		if (appenders == null)
			throw new AppendersClosedException();

		appenders.put(lock, appender);
	}

	/**
	 * Invokes {@link org.apache.log4j.Appender.close} for all appenders of
	 * related test method.
	 * <p>
	 * This is used to free system resources after the test method execution.
	 */
	public void closeAppenders() {
		
		if (appenders == null)
			return;
		
		for (Appender appender : appenders.values()) {
			appender.close();
		}

		appenders = null;
	}

	/**
	 * @return true if appenders are closed
	 */
	public boolean areClosed() {
		return appenders == null;
	}

}
