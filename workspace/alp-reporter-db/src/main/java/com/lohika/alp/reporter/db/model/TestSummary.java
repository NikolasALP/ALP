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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "TestSummary", entities = @EntityResult(entityClass = TestSummary.class))
// TODO rewrite from MySQL to HQL
@NamedNativeQuery(name = "TestSummaryQuery", resultSetMapping = "TestSummary", query = "select T.ID AS TESTID, COUNT(TM.ID) AS TOTAL, SUM(IF(TM.STATUS='SUCCESS',1,0)) AS PASSED, SUM(IF(TM.STATUS='FAILURE',1,0)) AS FAILED, SUM(IF(TM.STATUS='SKIP',1,0)) AS SKIPPED from TEST_METHODS AS TM RIGHT JOIN TEST_INSTANCES AS TI ON TM.TEST_INSTANCE_ID=TI.ID RIGHT JOIN TESTS AS T ON T.ID=TI.TEST_ID where T.ID=:id group by T.ID;")
public class TestSummary {

	private Long testId;
	private int total;
	private int passed;
	private int failed;
	private int skipped;

	@Id
	@Column
	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	@Column
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Column
	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

	@Column
	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	@Column
	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

}
