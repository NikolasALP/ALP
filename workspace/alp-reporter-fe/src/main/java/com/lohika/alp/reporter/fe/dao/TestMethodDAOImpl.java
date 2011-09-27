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
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.lohika.alp.reporter.db.model.Suite;
import com.lohika.alp.reporter.db.model.Test;
import com.lohika.alp.reporter.db.model.TestInstance;
import com.lohika.alp.reporter.db.model.TestMethod;
import com.lohika.alp.reporter.fe.form.TestMethodFilter;
import com.lohika.alp.reporter.fe.query.QueryTranslator;
import com.lohika.alp.reporter.fe.query.QueryTranslatorException;

@SuppressWarnings("unchecked")
@Repository
public class TestMethodDAOImpl implements TestMethodDAO {

	private QueryTranslator querytr = new QueryTranslator();

	private HibernateTemplate hibernateTemplate;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	private DetachedCriteria getFilteredCriteria(TestMethodFilter filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TestMethod.class);

		criteria.createAlias("testInstance", "testInstance").
				createAlias("testInstance.testClass", "testClass").
				createAlias("testInstance.test", "test");

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

			criteria.add(Restrictions.between("startDate", from.getTime(),
					till.getTime()));
		}

		if (filter.getCl() != "" && filter.getCl() != null) {
			try {
				criteria.add(querytr.translate("testClass.name", filter.getCl()));
			} catch (QueryTranslatorException e) {
				// TODO Put QueryTranslatorException into log
				e.printStackTrace();
			}
		}

		if (filter.getGr() != "" && filter.getGr() != null) {
			criteria.createAlias("groups", "groups");

			try {
				criteria.add(querytr.translate("groups.name", filter.getGr()));
			} catch (QueryTranslatorException e) {
				// TODO Put QueryTranslatorException into log
				e.printStackTrace();
			}
		}

		return criteria;
	}

	@Override
	public List<TestMethod> listTestMethod(TestMethodFilter filter) {

		DetachedCriteria criteria = getFilteredCriteria(filter);

		return hibernateTemplate.findByCriteria(criteria);
	}

	@Override
	public List<TestMethod> listTestMethod(TestMethodFilter filter,
			TestInstance testInstance) {

		DetachedCriteria criteria = getFilteredCriteria(filter);

		criteria.add(Expression.eq("testInstance", testInstance));

		return hibernateTemplate.findByCriteria(criteria);
	}

	@Override
	public List<TestMethod> listTestMethod(TestMethodFilter filter, Test test) {

		DetachedCriteria criteria = getFilteredCriteria(filter);

		criteria.add(Expression.eq("testInstance.test", test));

		return hibernateTemplate.findByCriteria(criteria);
	}	

	@Override
	public List<TestMethod> listTestMethod(TestMethodFilter filter, Suite suite) {
		DetachedCriteria criteria = getFilteredCriteria(filter);

		criteria.add(Expression.eq("test.suite", suite));

		return hibernateTemplate.findByCriteria(criteria);
	}

	@Override
	public TestMethod getTestMethod(long id) {
		return hibernateTemplate.get(TestMethod.class, id);
	}

	@Override
	public void saveTestMethod(TestMethod testMethod) {
		hibernateTemplate.saveOrUpdate(testMethod);
	}

}
