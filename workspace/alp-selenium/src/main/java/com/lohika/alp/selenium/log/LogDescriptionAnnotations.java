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
package com.lohika.alp.selenium.log;

import java.lang.reflect.Field;

public class LogDescriptionAnnotations {

	private final Field field;

	public LogDescriptionAnnotations(Field field) {
		this.field = field;
	}

	public LogDescriptionBean buildLogDescriptionBean() {
		LogDescriptionBean bean = new LogDescriptionBean();

		LogDescription description = field.getAnnotation(LogDescription.class);

		if (description != null) {
			bean.setName(description.name());
			bean.setType(description.type());
		}

		return bean;
	}

}
