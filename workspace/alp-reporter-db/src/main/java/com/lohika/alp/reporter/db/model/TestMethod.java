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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Test method representation
 *
 */
@Entity
@Table(name = "TEST_METHODS")
public class TestMethod {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	Long id;

	@Column(name = "NAME")
	String name;
	
	@Column(name = "DESCRIPTION")
	String description;

	@ManyToOne
	@JoinColumn(name = "TEST_INSTANCE_ID")
	TestInstance testInstance;

	@Column(name = "START_DATE")
	Long startDate;
	
	@Transient
	String formattedStartDate;

	@Column(name = "FINISH_DATE")
	Long finishDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	EMethodStatus status;
	
	@OneToOne
	@JoinColumn(name = "EXCEPTION_ID")
	TestMethodException exception;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "METHOD_GROUP", joinColumns = { @JoinColumn(name = "TEST_METHOD_ID") }, inverseJoinColumns = { @JoinColumn(name = "GROUP_ID") })
	Set<Group> groups = new HashSet<Group>();
	
	@Column(name = "HAS_LOG")
	Boolean hasLog = false;
	
	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Long finishDate) {
		this.finishDate = finishDate;
	}

	public EMethodStatus getStatus() {
		return status;
	}

	public void setStatus(EMethodStatus status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TestInstance getTestInstance() {
		return testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}

	public TestMethodException getException() {
		return exception;
	}

	public void setException(TestMethodException exception) {
		this.exception = exception;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Boolean getHasLog() {
		return hasLog;
	}

	public void setHasLog(Boolean hasLog) {
		this.hasLog = hasLog;
	}
	
	/**
	 * @return duration in seconds
	 */
	public Long getDuration() {
		return (finishDate - startDate) / 1000;
	}

	// TODO Convert to Date object. Formated string ignores user's time zone settings
	public String getFormattedStartDate() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(startDate));
	}

	public void setFormattedStartDate(String formattedStartDate) {
		this.formattedStartDate = formattedStartDate;
	}
}
