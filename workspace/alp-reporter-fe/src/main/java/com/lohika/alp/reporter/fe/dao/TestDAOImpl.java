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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestSummary;
import com.lohika.alp.reporter.fe.form.TestFilter;
import com.lohika.alp.reporter.fe.query.QueryTranslator;
import com.lohika.alp.reporter.fe.query.QueryTranslatorException;

@Repository
@SuppressWarnings("unchecked")
public class TestDAOImpl implements TestDAO {

	private QueryTranslator querytr = new QueryTranslator();

	private HibernateTemplate hibernateTemplate;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	private DetachedCriteria getFilteredCriteria(TestFilter filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Test.class);

		criteria.createAlias("suite", "suite");

		if (filter.getFrom() != null && filter.getTill() != null) {
			Calendar c = Calendar.getInstance();

			c.setTime(filter.getFrom());
			c.set(Calendar.HOUR_OF_DAY,
					c.getActualMinimum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND,
					c.getActualMinimum(Calendar.MILLISECOND));
			Date from = c.getTime();

			c.setTime(filter.getTill());
			c.set(Calendar.HOUR_OF_DAY,
					c.getActualMaximum(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
			c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
			c.set(Calendar.MILLISECOND,
					c.getActualMaximum(Calendar.MILLISECOND));
			Date till = c.getTime();

			criteria.add(Restrictions.between("startDate", from, till));
		}

		if (filter.getTest() != "" && filter.getTest() != null) {
			try {
				criteria.add(querytr.translate("name", filter.getTest()));
			} catch (QueryTranslatorException e) {
				// TODO Put QueryTranslatorException into log
				e.printStackTrace();
			}
		}

		if (filter.getSuite() != "" && filter.getSuite() != null) {
			try {
				criteria.add(querytr.translate("suite.name", filter.getSuite()));
			} catch (QueryTranslatorException e) {
				// TODO Put QueryTranslatorException into log
				e.printStackTrace();
			}
		}
		return criteria;
	}

	@Override
	public Test getTest(long id) {
		return hibernateTemplate.get(Test.class, id);
	}

	@Override
	public List<Test> listTest(TestFilter filter) {
		DetachedCriteria criteria = getFilteredCriteria(filter);

		List<Test> result = hibernateTemplate.findByCriteria(criteria);
		return result;
	}

	@Override
	public List<Test> listTest(TestFilter filter, Suite suite) {
		DetachedCriteria criteria = getFilteredCriteria(filter);

		criteria.add(Expression.eq("suite", suite));
		return hibernateTemplate.findByCriteria(criteria);
	}

	@Override
	public TestSummary getTestSummary(Test test) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();

		List<TestSummary> result = session.getNamedQuery("TestSummaryQuery")
				.setParameter("id", test.getId()).list();

		return result.get(0);
	}

	@Override
	public Map<Test, TestSummary> getTestSummaryMap(List<Test> tests) {
		Map<Test, TestSummary> result = new HashMap<Test, TestSummary>();

		for (Test test : tests) {
			result.put(test, getTestSummary(test));
		}

		return result;
	}

}
