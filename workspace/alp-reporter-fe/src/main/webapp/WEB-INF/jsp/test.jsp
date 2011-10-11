<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jstl/core_rt"
	xmlns:form="http://www.springframework.org/tags/form" 
	version="2.0">
		<jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" />
	<jsp:text>
		<![CDATA[ <?xml version="1.0" encoding="UTF-8" ?> ]]>
	</jsp:text>	
	<jsp:text>
		<![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
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
	<script type="text/javascript" src="${contextPath}/static/js/jquery.cookie.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.num-html.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/tests.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery-ui.min.js">//</script>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/table_jui.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/jui_themes/smoothness/jquery-ui-1.8.14.custom.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/page.css"/>	
	<title>
	<c:choose>
		<c:when test="${suiteId == null}">Results - Suites</c:when>
		<c:otherwise>Results - Suite ${suiteId }</c:otherwise>		
	</c:choose>
	</title>
	</head>
	<body id="dt_results">
		
	<div>
		<span id="nav_info">
		<c:choose>
			<c:when test="${suiteId == null}">Results - Suites</c:when>
			<c:otherwise>Results - Suite ${suiteId }</c:otherwise>		
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
			$("#filter .datepicker").datepicker({
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
		<form:form modelAttribute="testFilter" method="GET" action="test">
			<button id="filter_button" type="submit" value="Filter" style="float: right;" onClick='$.cookie("from", $("#from").val());$.cookie("till", $("#till").val());'>Filter</button>			
			<form:label path="suite" style="float: left;">Suite: <form:input path="suite" /></form:label>				
			<form:label path="test" style="float: left;">Suite Section: <form:input path="test" /></form:label>
			<div style="float: left;">
				<form:label path="from">From: <form:input path="from" class="datepicker" size="10" /></form:label>				
				<form:label path="till">Till: <form:input path="till" class="datepicker" size="10" /></form:label>
			</div>
		</form:form>
	</div>
	<div id="view" style="float:right; margin-right: 16px;">
	<label>View:
	<select id="view_select">
	<option>Columns</option>
	<option selected="selected">Chart</option>
	</select>
	</label>
	</div>
				
	<table id="tests" class="display" cellspacing="0" cellpadding="0" border="0">
		<thead>
			<tr>
				<th>ID</th>
				<th>Suite</th>
				<th>Details</th>
				<th>Suite Section</th>
				<th width="5%">Total</th>
				<th width="5%">Failed</th>
				<th width="5%">Skipped</th>
				<th width="160">Status</th>
				<th width="5%">Tests</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${tests}" var="test">
				<c:set var="summary" value="${summaryMap[test]}" />
				<c:choose>
	    			<c:when test="${summary.failed > 0}">
	        			<c:set var="statusStyle" value="gradeX" />
	    			</c:when>
	    			<c:when test="${summary.skipped > 0}">
	        			<c:set var="statusStyle" value="gradeU" />
	    			</c:when>
	    			<c:otherwise>
	       				<c:set var="statusStyle" value="gradeA" />
	    			</c:otherwise>
				</c:choose>			
			<tr class="${statusStyle }">
				<td>${test.suite.id }</td>
				<td><a href="${contextPath}/results/suite/${test.suite.id }/test">${test.suite.name }</a></td>
				<td><a class="ui-icon ui-icon-carat-1-e" style="margin: auto;" href="${contextPath}/results/suite/${test.suite.id }/test-method" /></td>
				<td>${test.name }</td>
				<td>${summary.total }</td>
				<td>${summary.failed }</td>
				<td>${summary.skipped }</td>
				<td>${summary.total - summary.failed - summary.skipped}</td>
				<td class="center"><a class="ui-icon ui-icon-carat-1-e" style="margin: auto;" href="${contextPath}/results/test/${test.id }/test-method" /></td>
			</tr>			
			</c:forEach>
		</tbody>
	</table>
	
	</body>
	</html>
</jsp:root>