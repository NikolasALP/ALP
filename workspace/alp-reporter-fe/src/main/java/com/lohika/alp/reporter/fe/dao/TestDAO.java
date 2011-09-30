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
package com.lohika.alp.reporter.fe.dao;

import java.util.List;
import java.util.Map;

import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestSummary;
import com.lohika.alp.reporter.fe.form.TestFilter;

public interface TestDAO {
	
	public Test getTest(long id);

	public List<Test> listTest(TestFilter filter);
	
	public List<Test> listTest(TestFilter filter, Suite suite);
	
	public TestSummary getTestSummary(Test test);
	
	public Map<Test, TestSummary> getTestSummaryMap(List<Test> tests);
	
}
