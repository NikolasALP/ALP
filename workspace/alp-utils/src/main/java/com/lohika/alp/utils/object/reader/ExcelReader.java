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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelReader implements ObjectReader {

	protected String fileName;
	private Workbook workbook;
	
	public ExcelReader(String fileName) throws Exception {
		this.open(fileName);
	}
	
	public Object readObject(Class<?> type, int index) throws Exception {
		Sheet sheet = workbook.getSheet(type.getSimpleName());
		if (sheet == null)
			throw new Exception("Sheet of type '"+type.getName()+
				"' is absent in the file '"+fileName+"'");
		
		// get fields of first row - each column value is a class field name
		Cell[] columnFields = sheet.getRow(0);
		
		// get all fields from the class
		Field[] classFields = type.getDeclaredFields();

		Cell[] dataFields = sheet.getRow(index+1);
		// instantiate object of specific type
		Object item = type.getDeclaredConstructor().newInstance();

		for (int column=0; column<classFields.length; column++) {
			if (fieldInArray(columnFields, classFields[column].getName()));
			{
					//System.out.println(dataFields[column].getContents());
					classFields[column].setAccessible(true);
					classFields[column].set(item, dataFields[column].getContents());
			}
		}

		return item;
	}

	public List<?> readAllObjects(Class<?> type) throws Exception {
		Sheet sheet = workbook.getSheet(type.getSimpleName());
		if (sheet == null)
			throw new Exception("Sheet of type '"+type.getName()+
				"' is absent in the file '"+fileName+"'");
		
		// get fields of first row - each column value is a class field name
		Cell[] columnFields = sheet.getRow(0);
		
		// get all fields from the class
		Field[] classFields = type.getDeclaredFields();

		int count = sheet.getRows()-1;
        List<Object> result = new ArrayList<Object>();

		for (int row=0; row<count; row++) {
			Cell[] dataFields = sheet.getRow(row+1);
			Object item = type.getDeclaredConstructor().newInstance();

			for (int column=0; column<classFields.length; column++) {
				if (fieldInArray(columnFields, classFields[column].getName()));
				{
						//System.out.println(dataFields[column].getContents());
						classFields[column].setAccessible(true);
						classFields[column].set(item, dataFields[column].getContents());
				}
			}
			result.add(item);
		}

		return result;
	}

	public void open(String fileName) throws Exception {
		workbook = Workbook.getWorkbook(new File(fileName));
	}

	public void close() {
		workbook.close();
	}

	/**
	 * check if the excel columns contain fieldName
	 * @param cell
	 * @param fieldName
	 * @return
	 */
	private boolean fieldInArray(Cell[] cell, String fieldName) {
		for (int i=0; i<cell.length; i++) 
			if (cell[i].getContents().equals(fieldName))
				return true;
		return false;
	}

}
