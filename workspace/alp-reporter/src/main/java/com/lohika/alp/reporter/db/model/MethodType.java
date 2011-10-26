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
package com.lohika.alp.reporter.db.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Test method type
 */
@Entity
@Table(name = "METHOD_TYPE")
public class MethodType {

	@Id
	private MethodTypePK primaryKey = new MethodTypePK();

	@Embeddable
	private class MethodTypePK implements Serializable {
		private static final long serialVersionUID = 1L;

		@ManyToOne
		@JoinColumn(name = "TEST_METHOD_ID", referencedColumnName = "ID")
		private TestMethod testMethod;

		@Column(name = "TYPE")
		@Enumerated(EnumType.STRING)
		private EMethodType methodType;

		public TestMethod getTestMethod() {
			return testMethod;
		}

		public void setTestMethod(TestMethod testMethod) {
			this.testMethod = testMethod;
		}

		public com.lohika.alp.reporter.db.model.EMethodType getMethodType() {
			return methodType;
		}

		public void setMethodType(
				com.lohika.alp.reporter.db.model.EMethodType methodType) {
			this.methodType = methodType;
		}
	}

	public TestMethod getTestMethod() {
		return primaryKey.getTestMethod();
	}

	public void setTestMethod(TestMethod testMethod) {
		this.primaryKey.setTestMethod(testMethod);
	}

	public com.lohika.alp.reporter.db.model.EMethodType getMethodType() {
		return primaryKey.getMethodType();
	}

	public void setMethodType(
			com.lohika.alp.reporter.db.model.EMethodType methodType) {
		this.primaryKey.setMethodType(methodType);
	}
	
}
