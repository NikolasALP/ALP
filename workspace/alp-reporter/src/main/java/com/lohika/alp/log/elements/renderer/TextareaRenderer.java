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

import com.lohika.alp.log.elements.schema.Textarea;

public class TextareaRenderer implements ObjectRenderer {

	@Override
	public String doRender(Object o) {
		
		if (!(o instanceof Textarea))
			return null;
		
		Textarea textarea = (Textarea) o;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("TextArea" + " -> " + textarea.getName());
		
		return builder.toString();
	}

}
