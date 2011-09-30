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
package com.lohika.alp.log.elements.renderer;

import org.apache.log4j.or.ObjectRenderer;

import com.lohika.alp.log.elements.schema.Action;
import com.lohika.alp.log.elements.schema.Webelement;

public class ActionRenderer implements ObjectRenderer {

	@Override
	public String doRender(Object o) {
		if (!(o instanceof Action))
			return null;

		Action action = (Action) o;

		Webelement self = action.getWebelement();

		StringBuilder builder = new StringBuilder();

		builder.append(self.getType() + " " + self.getName());

		builder.append(" -> " + action.getName());

		String arg = action.getArg().size() > 0 ? " "
				+ action.getArg().toString() : "";

		builder.append(arg);

		return builder.toString();
	}

}
