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

import com.lohika.alp.log.elements.schema.Comment;
import com.lohika.alp.log.elements.schema.Screenshot;
import com.lohika.alp.log.elements.schema.Textarea;

public class CommentRenderer implements ObjectRenderer {

	@Override
	public String doRender(Object o) {
		
		if (!(o instanceof Comment))
			return null;
		
		Comment comment = (Comment) o;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Comment" + " -> ");
		
		for (Object c : comment.getContent())	{
			if(c instanceof String)	{
				builder.append((String)c);
			}
			if(c instanceof Textarea)	{
				builder.append(((Textarea)c).getName());
			}
			if(c instanceof Screenshot)	{
				builder.append("Screenshot" + " [" + ((Screenshot)c).getUrl() + "]");
			}
			builder.append("; ");
		}
		
		return builder.toString();
	}

}
