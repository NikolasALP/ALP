<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jstl/core_rt"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	version="2.0">
	
	<jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" />
	<jsp:text>
		<![CDATA[ <?xml version="1.0" encoding="UTF-8" ?> ]]>
	</jsp:text>
	<jsp:text>
		<![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> ]]>
	</jsp:text>
	
	<c:set var="contextPath" scope="page" value="${pageContext.request.contextPath}" />
	
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
	<script type="text/javascript" src="${contextPath}/static/js/jquery.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.num-html.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/testmethods.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery-ui.min.js">//</script>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/table_jui.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/jui_themes/smoothness/jquery-ui-1.8.14.custom.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/page.css"/>

	<title>
	<c:choose>
		<c:when test="${suiteId != null}">Results - Suite ${suiteId }</c:when>
		<c:when test="${testId != null}">Results - Test ${testId }</c:when>
		<c:when test="${testInstanceId != null}">Results - Test Instance ${testInstanceId }</c:when>
		<c:otherwise>Results - Tests</c:otherwise>		
	</c:choose>
	</title>
	</head>	
	<body id="dt_results">
	
	<div>
		<span id="nav_info">
		<c:choose>
			<c:when test="${suiteId != null}">Results - Suite ${suiteId }</c:when>
			<c:when test="${testId != null}">Results - Test ${testId }</c:when>
			<c:when test="${testInstanceId != null}">Results - Test Instance ${testInstanceId }</c:when>
			<c:otherwise>Results - Tests</c:otherwise>		
		</c:choose>
		</span>
		<div id="nav_links">
			<a href="${contextPath}/results/test">Suites</a>
			<span> / </span>
			<a href="${contextPath}/results/test-method">Tests</a>
		</div>
	</div>
	
	<script type="text/javascript">
		$(function() {
			$(".datepicker").datepicker({
				minDate : new Date(2009, 1, 1),
				maxDate : new Date(),
				dateFormat : 'yy-mm-dd'
			});
			
			$("#filter button").button({
	            icons: {
	                primary: "ui-icon-search"
	            },
	            text: false
			});
		});
	</script>
	<div id="filter">
		<form:form modelAttribute="testMethodFilter" method="GET" action="test-method">
			<button id="filter_button" type="submit" value="Filter" style="float: right;">Filter</button>
			<form:label path="cl" style="float: left;" >Class: <form:input path="cl" /></form:label>			
			<form:label path="gr" style="float: left;">Group: <form:input path="gr" /></form:label>			
			<div style="float: left;">
				<form:label path="from">From: <form:input path="from" class="datepicker" size="10" /></form:label>				
				<form:label path="till">Till: <form:input path="till" class="datepicker" size="10" /></form:label>
			</div>
		</form:form>
	</div>
	
	<div id="group_by">
		Group by
		<select name="group_by" onchange="groupBy(this)">
			<option value="disable">disable</option>
			<option value="id" selected="selected">id</option>
		</select>
	</div>
			
	<table id="testmethods" class="display" cellspacing="0" cellpadding="0" border="0">
		<thead>
			<tr>
				<th>Id</th>
				<!-- Class full name -->
				<th>Class</th>
				<!-- Class short name -->
				<th>Class</th>
				<th>Method</th>
				<th>Description</th>
				<th>Input</th>
				<th>Groups</th>
				<th width="20%">Error</th>
				<th width="15%">Start</th>
				<th width="5%">Duration</th>
				<th width="5%">Log</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${testMethods }" var="method">
				<c:choose>
	    			<c:when test="${method.status == 'FAILURE'}">
	        			<c:set var="statusStyle" value="gradeX" />
	    			</c:when>
	    			<c:when test="${method.status == 'SKIP'}">
	        			<c:set var="statusStyle" value="gradeU" />
	    			</c:when>
	    			<c:otherwise>
	       				<c:set var="statusStyle" value="gradeA" />
	    			</c:otherwise>
				</c:choose>	
			
				<tr class="${statusStyle }">
					<td>${method.testInstance.id }</td>
					<!-- Class short name -->
					<td><a href="${contextPath}/results/test-instance/${method.testInstance.id }/test-method">${method.testInstance.testClass.name }</a></td>
					<!-- Class full name -->
					<td><a href="${contextPath}/results/test-instance/${method.testInstance.id }/test-method">${method.testInstance.testClass.shortName }</a></td>
					<td>${method.name }</td>
					<td>${method.description }</td>
					<td></td>
					<td>
						<c:forEach items="${method.groups }" var="group">
						<div>${group.name }</div>
						</c:forEach>										
					</td>
					<td>
						<div class="exception">
							<a class="className">${method.exception.className }</a>
							<div class="message">${fn:escapeXml(method.exception.message) }</div>
							<div class="fullStacktrace" style="display:none">${method.exception.fullStacktrace }</div>
						</div>
					</td>
					<td class="center">${method.formattedStartDate }</td>
					<td class="center">${method.duration }</td>
					<td class="center">
					<c:if test="${method.hasLog}">
						<a class="ui-icon ui-icon-carat-1-e" style="margin: auto;" href="${contextPath}/results/test-method/${method.id }/log/" />
					</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	</body>
	</html>
</jsp:root>
