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

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lohika.alp.reporter.fe.form.TestFilter;
import com.lohika.alp.reporter.fe.form.TestMethodFilter;

/**
 * Trivial parser for queries of {@link TestFilter} and {@link TestMethodFilter}
 * <p>
 * Rules:
 * <ul>
 * <li>text inside double quotes is {@link SQLLike}
 * <li>single word outside double quotes is {@link Word}
 * <li>spaces between words outside double quotes are {@link Or} operators
 * </ul>
 * 
 */
public class QueryParser {

	Pattern p = Pattern.compile("\".*?\"|\\S+");

	LinkedList<String> buildTokens(String query) {
		Matcher m = p.matcher(query);

		LinkedList<String> tokens = new LinkedList<String>();

		while (m.find())
			tokens.push(query.substring(m.start(), m.end()));

		return tokens;
	}

	Query buildExpression(String token) {
		if (token.startsWith("\"") && token.endsWith("\""))
			return new SQLLike(token.substring(1, token.length() - 1));
		else
			return new Word(token);
	}

	Query buildAST(Query rightmost,
			ListIterator<String> itr) {
		if (rightmost == null) {
			rightmost = buildExpression(itr.next());
			itr.remove();
		}

		if (itr.hasNext()) {
			Query exp = new Or(buildExpression(itr.next()),
					rightmost);
			itr.remove();

			return buildAST(exp, itr);
		} else
			return rightmost;
	}

	public Query parse(String query) {
		LinkedList<String> tokens = buildTokens(query);

		if (tokens.size() == 0)
			return null;

		return buildAST(null, tokens.listIterator());
	}

}
