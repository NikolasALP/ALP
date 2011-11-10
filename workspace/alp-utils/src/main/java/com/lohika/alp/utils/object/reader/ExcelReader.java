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
	private Sheet sheet;
	protected boolean columnsHorizontal = true;
	protected boolean namedIndex = false;
	
	public boolean isColumnsHorizontal() {
		return columnsHorizontal;
	}

	public void setColumnsHorizontal(boolean columnsHorizontal) {
		this.columnsHorizontal = columnsHorizontal;
	}
	
	public boolean isNamedIndex() {
		return namedIndex;
	}

	public void setNamedIndex(boolean namedIndex) {
		this.namedIndex = namedIndex;
	}
	
	public ExcelReader(String fileName) throws Exception {
		this.open(fileName);
	}
	
	public ExcelReader(String fileName, boolean columnsHorizontal, boolean namedIndex) throws Exception {
		this.open(fileName);
		setColumnsHorizontal(columnsHorizontal);
		setNamedIndex(namedIndex);
	}
	
	protected Cell[] getColumns() {
		if (sheet==null)
			return null;
		if (isColumnsHorizontal())
			return sheet.getRow(0);
		else
			return sheet.getColumn(0);
	}
	
	protected Cell[] getRecord(int index) {
		if (sheet==null)
			return null;
		if (isColumnsHorizontal())
			return sheet.getRow(getIndex(index));
		else
			return sheet.getColumn(getIndex(index));
	}
	
	private int getIndex(int index) {
		return index+1;
	}
	
	public Cell[] getIndexes() {
		if (!isNamedIndex() || sheet==null)
			return null;
		if (isColumnsHorizontal())
			return sheet.getColumn(0);
		else
			return sheet.getRow(0);
	}
	
	public Object readObject(Class<?> type, int index) throws Exception {
		sheet = workbook.getSheet(type.getSimpleName());
		if (sheet == null)
			throw new Exception("Sheet of type '"+type.getName()+
				"' is absent in the file '"+fileName+"'");
		
		// get fields of first row - each column value is a class field name
		Cell[] columnFields = getColumns();
		
		// get all fields from the class
		Field[] classFields = type.getDeclaredFields();

		Cell[] dataFields = getRecord(index);
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
		sheet = workbook.getSheet(type.getSimpleName());
		if (sheet == null)
			throw new Exception("Sheet of type '"+type.getName()+
				"' is absent in the file '"+fileName+"'");
		
		// get fields of first row - each column value is a class field name
		Cell[] columnFields = getColumns();
		
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
		this.fileName = fileName;
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

	public Object readObject(Class<?> type, String index) throws Exception {
		if (index==null || type==null)
			throw new Exception("Parameters should not be null");

		sheet = workbook.getSheet(type.getSimpleName());

		if (sheet == null)
			throw new Exception("Sheet of type '"+type.getName()+
				"' is absent in the file '"+fileName+"'");
			
		Integer objectIndex = null;
		Cell[] indexes = getIndexes();
		for (Cell cell: indexes) {
			if (index.equals(cell.getContents()))
				if (isColumnsHorizontal())
					objectIndex = cell.getRow()-1;
				else
					objectIndex = cell.getColumn()-1;
		}
		
		if (objectIndex==null)
			throw new Exception("Record with '"+index+"' was not found");
		
		Cell[] columnFields = getColumns();
		Field[] classFields = type.getDeclaredFields();
		Cell[] dataFields = getRecord(objectIndex);
		
		// instantiate object of specific type
		Object item = type.getDeclaredConstructor().newInstance();

		for (int column=0; column<classFields.length; column++) {
			if (fieldInArray(columnFields, classFields[column].getName()));
			{
					classFields[column].setAccessible(true);
					if (isNamedIndex())
						classFields[column].set(item,
							dataFields[column+1].getContents());
					else
						classFields[column].set(item,
							dataFields[column].getContents());
			}
		}

		return item;
	}

}
