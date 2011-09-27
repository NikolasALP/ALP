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
package com.lohika.alp.log.elements;

import java.util.List;
// check commit

public interface LogElementsFactory {

	public Object textArea(String name, String content);
	
	public Object link(String url);
	
	public Object link(String url, String description);

	public Object screenshot(String url, String description);

	public Object comment(String comment);

	public Object comment(String comment, LogStyle style);

	public Object comment(List<Object> comment);

	public Object comment(Object... comment);

	public Object comment(List<Object> comment, LogStyle style);

}
