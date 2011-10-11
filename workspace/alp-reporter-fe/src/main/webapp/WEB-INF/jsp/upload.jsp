<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jstl/core_rt"
	xmlns:form="http://www.springframework.org/tags/form" version="2.0">
	<jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" />
	<jsp:text>
		<![CDATA[ <?xml version="1.0" encoding="UTF-8" ?> ]]>
	</jsp:text>
	<jsp:text>
		<![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
	</jsp:text>
	<html xmlns="http://www.w3.org/1999/xhtml">
	<!--
Copyright 2011 Lohika .  This file is part of ALP.

    ALP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ALP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ALP.  If not, see <http://www.gnu.org/licenses/>.
   -->	
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Upload File</title>
	</head>
	<body>

	<form:form modelAttribute="uploadItem" method="post"
		enctype="multipart/form-data">
		<fieldset><legend>Upload Fields</legend>
		<p>
			<form:label for="name" path="name">Name</form:label><br />
			<form:input path="name" />
		</p>
		<p>
			<form:label for="fileData" path="fileData">File</form:label><br />
			<form:input path="fileData" type="file" />
		</p>
		<p>
			<input type="submit" />
		</p>
		</fieldset>
	</form:form>

	</body>
	</html>
</jsp:root>