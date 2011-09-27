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
package com.lohika.alp.reporter.fe.query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Translates abstract {@link Query} tree to Hibernate {@link Criterion} tree
 * 
 */
public class QueryTranslator {

	private QueryParser parser = new QueryParser();

	private Criterion translate(String propertyName, Query exp)
			throws QueryTranslatorException {

		if (exp instanceof Word) {
			String value = "%" + ((Word) exp).getValue() + "%";
			return Restrictions.like(propertyName, value);
		} else if (exp instanceof SQLLike) {
			String value = ((SQLLike) exp).getValue();
			return Restrictions.like(propertyName, value);
		} else if (exp instanceof Or) {
			Criterion left = translate(propertyName, ((Or) exp).getLeft());
			Criterion right = translate(propertyName, ((Or) exp).getRight());

			return Restrictions.or(left, right);
		}

		throw new QueryTranslatorException("Unknown query type: "
				+ propertyName + ", " + exp.getClass());
	}

	public Criterion translate(String propertyName, String query)
			throws QueryTranslatorException {
		Query exp = parser.parse(query);

		if (exp == null)
			throw new QueryTranslatorException("Cannot parse query: "
					+ propertyName + ", " + query);

		return translate(propertyName, exp);
	}

}
