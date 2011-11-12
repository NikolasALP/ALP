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
package com.lohika.alp.utils.object.reader;

import java.util.List;

public interface ObjectReader {
	
	// open a file with data
	public void open(String fileName) throws Exception;
	
	// read object of specific type with the index
	public Object readObject(Class<?> type, int index) throws ObjectReaderException;
	
	// read object of specific type with the index
	public Object readObject(Class<?> type, String index) throws ObjectReaderException;
	
	// read all objects of specific type
	public List<?> readAllObjects(Class<?> type) throws Exception;
	
	// close file
	public void close();

}
