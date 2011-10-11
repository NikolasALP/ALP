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
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/page.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/table_jui.css"/>
	<link rel="stylesheet" type="text/css" href="${contextPath}/static/css/jui_themes/smoothness/jquery-ui-1.8.14.custom.css"/>
	
	<script type="text/javascript" src="${contextPath}/static/js/jquery.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery.dataTables.num-html.js">//</script>
	<script type="text/javascript" src="${contextPath}/static/js/jquery-ui.min.js">//</script>
	<script type="text/javascript">

	$(document).ready(function() {

		$('#suites').dataTable({
			"bProcessing" : true,
			"bServerSide": true,
			"sAjaxSource" : "/alp-reporter-fe/results_a/test/ajax",
			
			"aoColumns": [
			  			{ "mDataProp": "id" },
			  			{ "mDataProp": "suite", "bSortable": false },
			  			{ "mDataProp": "section", "bSortable": false },
			  			{ "mDataProp": "total", "bSortable": false },
			  			{ "mDataProp": "failed", "bSortable": false },
			  			{ "mDataProp": "skipped", "bSortable": false },
			  			{ "mDataProp": "test id", "bSortable": false }
			  		],
			
			"bJQueryUI" : true,
			"sPaginationType": "full_numbers"
		});
	});
	
	</script>

	<title>Suite Results</title>
	</head>
	
	<body id="dt_results">			
	<table id="suites" class="display" cellspacing="0" cellpadding="0" border="0">
		<thead>
			<tr>
				<th width="5%">ID</th>
				<th>Suite</th>
				<th>Section</th>
				<th width="5%">Total</th>
				<th width="5%">Failed</th>
				<th width="5%">Skipped</th>
				<th width="5%">Tests</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
	</body>
	</html>
</jsp:root>