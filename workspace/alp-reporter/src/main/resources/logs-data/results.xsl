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
<xsl:stylesheet version="2.0"
	xmlns:ns2="http://alp.lohika.com/testng/results/schema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xslFormatting="urn:xslFormatting">

	<xsl:output method="html" indent="yes" />

	<xsl:param name="relative" />

	<xsl:template match="/ns2:results">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title>Suite Run Report</title>
				<xsl:call-template name="writeCss" />
				<xsl:call-template name="writeJs" />
			</head>
			<body onclick='javascript:closeAllPopups();'>
				<!-- create navigation bar -->
				<div class="main">
					<xsl:call-template name="writeNavigationBar" />
				</div>
				<!-- create all the content: summary diagram; table with test suite summary; all the tables with test details -->
				<div class="content">
					<div class="summarycontent">
						<xsl:call-template name="writeSummaryDiagram" />
						<xsl:call-template name="writeTestSuitesSummary" />
					</div>
					<xsl:call-template name="writeTestDetails" />
				</div>
				<script type="text/javascript">
					window.onload=adaptLogs();
					window.onload=adaptTime();
				</script>
			</body>

		</html>
	</xsl:template>

	<!-- Write tables with test details: for each testinstance a table will be created -->
	<xsl:template name="writeTestDetails">
		<xsl:for-each select="//ns2:test">
			<!-- Look into each suite -->
			<div class="testdetails invisible">
				<!-- Look into each testinstance of the suite -->
				<!-- For each test instance create a table with method results -->
				<xsl:for-each select="ns2:test-instance">
					<div class="testinstance">
						<!-- The name of the test instance -->
						<div class="testinstancename">
							<div class="testinstancenametext">
								<xsl:value-of select="@class" />
							</div>
						</div>
						<!-- The table with method results -->
						<table class="methodsresults">
							<tbody>
								<!-- table header -->
								<tr class="methodsresultsheader">
									<td>test method</td>
									<td>start time</td>
									<td>duration</td>
									<td>exception</td>
									<td>show log</td>
								</tr>
								<!-- table content -->
								<xsl:for-each select="ns2:test-method">
									<!-- variable used for generation exception pop-up div ids-->
									<xsl:variable name="id" select="generate-id()" />
									<!-- create table entry with classname which has 'success', 'failure' or 'skip' work -->
									<!-- here translate function makes the same as toLowerCase() function in javascript. Just for good looks-->
									<tr class="methodsresultsentry {translate(@status, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')}">
										<!-- first column: testmethod name -->
										<td>
											<xsl:value-of select="@name" />
										</td>
										<!-- second column: method start time -->
										<td>
											<div class="logtimestart">
												<xsl:value-of select="@started-at" />
											</div>
											<!-- This 'if' is here to make pop-up div with exception text if it exists-->
											<xsl:if test='exception'>
												<xsl:element name="div">
													<xsl:attribute name="class">popup</xsl:attribute>
													<xsl:attribute name="id"><xsl:copy-of
														select="$id" /></xsl:attribute>
													<xsl:element name="span">
														<xsl:attribute name="class">close</xsl:attribute>
														<xsl:element name="a">
															<xsl:attribute name="href">javascript:closeAllPopups();</xsl:attribute>
															<xsl:attribute name="style">text-decoration: none</xsl:attribute>
															<xsl:element name="strong">
																<xsl:copy-of select="'Hide'" />
															</xsl:element>
														</xsl:element>
													</xsl:element>
													<xsl:element name="p">
														<xsl:copy-of select="exception/full-stacktrace" />
													</xsl:element>
												</xsl:element>
											</xsl:if>
										</td>
										<!-- third column: duration -->
										<td>
											<xsl:value-of select="@duration-ms" />
											<xsl:text> ms</xsl:text>
										</td>
										<!-- fourth column: exception -->
										<td class="exception">
											<xsl:if test='exception'>
												<!-- if exception has message write it here -->
												<!-- otherwise write exception class -->
												<div>
													<xsl:if test='exception/message'>
														<xsl:value-of select="exception/message" />
													</xsl:if>
													<xsl:if test='not(exception/message)'>
														<xsl:value-of select="exception/@class" />
													</xsl:if>
												</div>
												<!-- create a link to pop-up div -->
												<div class="exceptiondetails">
													<xsl:element name="a">
														<xsl:attribute name="href">javascript:setVisible('<xsl:copy-of
															select="$id" />', true)</xsl:attribute>
														<xsl:value-of select="'Details'" />
													</xsl:element>
												</div>

											</xsl:if>
										</td>
										<!-- fifth column: a link to log -->
										<td>
											<xsl:if test='logfile'>
												<xsl:element name="a">
													<xsl:attribute name="class">loglink</xsl:attribute>
													<xsl:attribute name="href">
														<xsl:value-of select="logfile/@path" />
													</xsl:attribute>
													<xsl:attribute name="target">_blank</xsl:attribute>
													<xsl:text>log</xsl:text>
												</xsl:element>
											</xsl:if>
										</td>
									</tr>
								</xsl:for-each>
							</tbody>
						</table>
					</div>
				</xsl:for-each>
			</div>
		</xsl:for-each>
	</xsl:template>

	<!-- Creates navigation bar -->
	<xsl:template name="writeNavigationBar">
		<div class="navigation">
			<!-- write summary item at the top-->
			<div class="summary selected"
				onclick="selectSummary(this);">
				<xsl:value-of select="'Summary'" />
			</div>
			<!-- Look into each suite -->
			<xsl:for-each select="ns2:suite">
				<!-- write a name of the suite -->
				<div class="navigation-suite-name">
					<xsl:value-of select="@name" />
				</div>
				<!-- write all the tests of this suite -->
				<table class="navigation-suite">
					<xsl:for-each select="ns2:test">
						<tr>
							<td>
								<!-- if test has no failures write a green rectangle near the test name -->
								<xsl:if test="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])=0">
									<div class="suitestatussuccess" />
								</xsl:if>
								<!-- if test has failures write a red rectangle near the test name -->
								<xsl:if test="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])>0">
									<div class="suitestatusfailure" />
								</xsl:if>
							</td>
							<!-- write test case name -->
							<td class="testcase" onmouseover="mouseOver(this);" onclick="select(this);">
								<xsl:value-of select="@name" />
							</td>
							<!-- write quick numbers of the passed, failed and skipped methods -->
							<td class="success">
								<xsl:value-of
									select="count(ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
							</td>
							<td class="failure">
								<xsl:value-of
									select="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
							</td>
							<td class="skip">
								<xsl:value-of select="count(ns2:test-instance/ns2:test-method[@status='SKIP'])" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:for-each>
		</div>
	</xsl:template>

	<!-- write summary tables with testcases statistics -->
	<xsl:template name="writeTestSuitesSummary">
		<div class="testsuitessummary">Test suites summary</div>
		<xsl:for-each select="//ns2:suite">
			<table class="summarytable">
				<!-- header -->
				<tr class="summarytableheader">
					<td class="summarytablecell summarysuite">
						<div>
							<!-- if test has no failures create green rectangle -->
							<xsl:if
								test="count(ns2:test/ns2:test-instance/ns2:test-method[@status='FAILURE'])=0">
								<div class="suiteStatusSuccess"></div>
							</xsl:if>
							<!-- if test has failures create red rectangle -->
							<xsl:if
								test="count(ns2:test/ns2:test-instance/ns2:test-method[@status='FAILURE'])>0">
								<div class="suiteStatusFailure"></div>
							</xsl:if>
						</div>
						<!-- write testname -->
						<xsl:value-of select="@name" />
					</td>
					<!-- sum of all passed methods for the test-->
					<td class="summarytablecell success">
						<xsl:value-of
							select="count(ns2:test/ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
					</td>
					<!-- sum of all failed methods for the test-->
					<td class="summarytablecell failure">
						<xsl:value-of
							select="count(ns2:test/ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
					</td>
					<!-- sum of all skipped methods for the test-->
					<td class="summarytablecell skip">
						<xsl:value-of
							select="count(ns2:test/ns2:test-instance/ns2:test-method[@status='SKIP'])" />
					</td>
					<!-- the sum of all testmethods -->
					<td class="summarytablecell">
						<xsl:value-of select="count(ns2:test/ns2:test-instance/ns2:test-method)" />
					</td>
					<!-- percentage of passed testmethods-->
					<td class="summarytablecell">
						<xsl:value-of
							select="round(count(ns2:test/ns2:test-instance/ns2:test-method[@status='SUCCESS']) div count(ns2:test/ns2:test-instance/ns2:test-method) * 100)" />
						<xsl:text>%</xsl:text>
					</td>
					<!-- duration of suite run -->
					<td class="summarytablecell summarytablecelltime">
						<script type="text/javascript">
							document.write(durationToHumanFormat(
							<xsl:value-of select="sum(ns2:test/ns2:test-instance/ns2:test-method/@duration-ms)" />
							));
						</script>
					</td>
				</tr>
				<!-- look at each test and extract the following info -->
				<xsl:for-each select="ns2:test">
					<tr class="summarytableentry">
						<!-- name of the test -->
						<td class="summarytablecell summarysuite">
							<xsl:value-of select="@name" />
						</td>
						<!-- sum of all passed methods for the test -->
						<td class="summarytablecell">
							<xsl:value-of select="count(ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
						</td>
						<!-- sum of all failed methods for the test-->
						<td class="summarytablecell">
							<xsl:value-of select="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
						</td>
						<!-- sum of all skipped methods for the test -->
						<td class="summarytablecell">
							<xsl:value-of select="count(ns2:test-instance/ns2:test-method[@status='SKIP'])" />
						</td>
						<!-- summ of all methods for the test -->
						<td class="summarytablecell">
							<xsl:value-of select="count(ns2:test-instance/ns2:test-method)" />
						</td>
						<!-- per-centage of passed methods for the test -->
						<td class="summarytablecell">
							<xsl:value-of
								select="round(count(ns2:test-instance/ns2:test-method[@status='SUCCESS']) div count(ns2:test-instance/ns2:test-method) * 100)" />
							<xsl:text>%</xsl:text>
						</td>
						<!-- duration -->
						<td class="summarytablecell summarytablecelltime">
							<script>
								document.write(durationToHumanFormat(
								<xsl:value-of select="sum(ns2:test-instance/ns2:test-method/@duration-ms)" />
								));
							</script>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:for-each>
	</xsl:template>

	<!-- Creates Summary diagram for all the suites -->
	<xsl:template name="writeSummaryDiagram">
		<div class="summaryDiagram">
			<div class="testsuitessummary">Summary Diagram</div>
			<!-- create summary diagram -->
			<canvas width="300" height="300" id="summaryDiagram">
				Your browser does not support the canvas tag.
				<script type="text/javascript">
					drawSummaryDiagram(
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
					,
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
					,
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='SKIP'])" />
					);
				</script>
			</canvas>

			<!-- create legend with percentages of passed, failed and skipped methods -->
			<canvas width="170" height="300" id="legendDiagram">
				<script type="text/javascript">
					drawLegend(
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
					,
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
					,
					<xsl:value-of
						select="count(//ns2:suite/ns2:test/ns2:test-instance/ns2:test-method[@status='SKIP'])" />
					);
				</script>
			</canvas>
		</div>
	</xsl:template>

	<!-- Write embedded CSS -->
	<xsl:template name="writeCss">
		<style type="text/css">
			.main{
			font: 12px lucida, sans-serif;
			}
			.navigation{
			position:fixed;
			overflow:auto;
			float:left;
			width:30%;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top: 1px;
			height:100%;

			}
			.navigation-suite{
			border: 1pt solid #ADD8E6;
			font: 12px lucida,
			sans-serif;
			margin-bottom: 0px;
			margin-right: 0px;
			margin-left: 0px;
			margin-top: 0px;
			width: 100%;
			}

			.navigation-suite-name{
			border-width: 1pt medium
			medium 1pt;
			background: #ADD8E6;
			font: 14px lucida, sans-serif;
			}

			.content{
			float: right;
			width:69%;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top: 1px;
			}

			.testinstance{
			border: 1pt solid
			#ADD8E6;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top: 1px;
			}

			.methodsresultsheader {
			border-width: 1pt medium
			medium 1pt;
			background: #ADD8E6;
			padding: 0in 5.4pt;
			text-align:center;
			}

			.methodsresults {
			margin-left: auto;
			margin-right: auto;
			border-collapse: collapse;
			border: medium none;
			width: 99%;
			vertical-align: middle;
			text-align:center;
			border: 1pt solid #ADD8E6;
			font: 12px lucida, sans-serif;
			margin-top: 0.5%;
			margin-left: 0.5%;
			margin-right: 0.5%;
			margin-bottom: 0.5%;
			}

			.methodsresultsentry
			{
			border:
			1pt solid #ADD8E6;
			}

			.testinstancename
			{
			font: 14px lucida, sans-serif;
			border-width: 1pt medium medium 1pt;
			background: #ADD8E6;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top:
			1px;
			}

			.testinstancenametext {
			margin-right: 2px;
			margin-left: 2px;
			}

			.testcase {
			border: 1pt solid #FFFFFF;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top: 1px;
			}

			.summary {
			font: 20px lucida, sans-serif;
			border: 1pt solid #ADD8E6;
			margin-bottom: 1px;
			margin-top: 1px;
			text-align:center;
			}

			.mouseovered {
			border: 1pt solid
			#ADD8E6;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top: 1px;
			}

			.selected {
			background: #ADD8E6;
			border: 1pt solid
			#ADD8E6;
			}

			.invisible {
			display:none;
			}

			.testdetails{
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left: 1px;
			margin-top:
			1px;
			}

			.success {
			background-color: #CEF6CE;
			text-align:center;
			}

			.failure
			{
			background-color: #F6CECE;
			text-align:center;
			}

			.skip {
			background-color: #E6E6E6;
			text-align:center;
			}

			.suitestatussuccess {
			height: 13px;
			margin-bottom: 2px;
			margin-left: 3px;
			margin-right: 4px;
			margin-top: 2px;
			padding-right: 10px;
			width: 6px;
			background-color:
			#58FA58;
			float:left;
			}

			.suitestatusfailure {
			height: 13px;
			margin-bottom:
			2px;
			margin-left: 3px;
			margin-right: 4px;
			margin-top: 2px;
			padding-right: 10px;
			width: 6px;
			background-color: #FE2E2E;
			float:left;
			}

			.summarycontent{
			border:
			1pt solid #ADD8E6;
			margin-bottom: 1px;
			margin-right: 1px;
			margin-left:
			1px;
			margin-top: 1px;
			}

			.testsuitessummary{
			background: #ADD8E6;
			text-align:center;
			font: 14px lucida, sans-serif;
			}

			.summarytableheader {
			border-width: 1pt medium medium 1pt;
			background: #ADD8E6;
			padding: 0in 5.4pt;
			text-align:center;
			}

			.summarytable {
			margin-left: auto;
			margin-right: auto;
			border-collapse: collapse;
			border: 1pt solid #ADD8E6;
			width: 99%;
			vertical-align: middle;
			text-align:center;
			font: 12px lucida,
			sans-serif;
			margin-top: 0.5%;
			margin-left: 0.5%;
			margin-right: 0.5%;
			margin-bottom: 0.5%;
			}

			.summarytableentry {
			border: 1pt solid #ADD8E6;
			}

			.summarytablecell {
			border: 1pt solid #ADD8E6;
			width:5%;
			}

			.summarytablecelltime {
			border: 1pt solid #ADD8E6;
			width:15%;
			}

			.summarysuite{
			border: 1pt solid #ADD8E6;
			text-align:left;
			width:60%
			}

			.summaryDiagram {
			vertical-align:middle;
			text-align:center;
			}

			.popup {
			position: absolute;
			visibility: hidden;
			width: 700px;
			height: 300px;
			background-color: #FFFFFF;
			border: 1px solid #ADD8E6;
			padding: 10px;
			z-index:1000;
			text-align: left;
			overflow: auto;
			}

			.close {
			float: right;
			}

			.exception {
			text-align:left;
			width:50%;
			}

			.exceptiondetails {
			float:
			left;
			}
		</style>
	</xsl:template>

	<!-- write embedded javascript into the document -->
	<xsl:template name="writeJs">
		<script language='JavaScript' type='text/JavaScript'>
//handles mouseover event. Adds class 'mouseovered' to the object classname
function mouseOver(testElementNavigationMenu)	{
	clearAllMouseOvering();
	
	if (testElementNavigationMenu.className.search("mouseovered") == -1)	{
		testElementNavigationMenu.className = testElementNavigationMenu.className + " mouseovered";
	}
}

//returns the array of the elements with the specified classname
function getElementsByClassName(clsName){
    var retVal = new Array();
    var elements = document.getElementsByTagName("*");
    for (var i = 0; i &lt; elements.length; i++) {
        if (elements[i].className.indexOf(" ") >= 0) {
            var classes = elements[i].className.split(" ");
            for (var j = 0; j &lt; classes.length; j++) {
                if (classes[j] == clsName) 
                    retVal.push(elements[i]);
            }
        }
        else 
            if (elements[i].className == clsName) 
                retVal.push(elements[i]);
    }
    return retVal;
}

//clear all the results of the mouse overing
function clearAllMouseOvering()	{
	var elements = getElementsByClassName("mouseovered");
	for (i in elements) {
		elements[i].className = elements[i].className.replace(" mouseovered","");
	}
}

//mark clicked element selected. Adds 'selected' to the element's classname
function select(testElementNavigationMenu)	{
	clearAllSelections();
	
	if (testElementNavigationMenu.className.search("selected") == -1)	{
		testElementNavigationMenu.className = testElementNavigationMenu.className + " selected";
	}
	
	hide("summarycontent");
	hide("testdetails");

	showByIndex("testdetails", getIndexOfSelectedTest());
}

//if the summary if selected, mark it as selected, 
//hide all testdetails selected before and show summary content 
function selectSummary(testElementNavigationMenu)	{
	clearAllSelections();
	
	if (testElementNavigationMenu.className.search("selected") == -1)	{
		testElementNavigationMenu.className = testElementNavigationMenu.className + " selected";
	}
	
	hide("testdetails");
	show("summarycontent");
}

//clears all selections
function clearAllSelections()	{
	var elements = getElementsByClassName("selected");
	for (i in elements) {
		elements[i].className = elements[i].className.replace(" selected","");
	}
}

//counts which test was selected and returns the its number
function getIndexOfSelectedTest()	{
	var elements = getElementsByClassName("testcase");
	
	for (i in elements) {
		if (elements[i].className.search("selected") != -1)	{
			return i;
		}
	}
}

//transforms the pathes to xml logs into the pathes to html logs
function adaptLogs(){
	var elements = getElementsByClassName("loglink");
	for (i in elements) {
		elements[i].href = elements[i].href.replace("logs", "logs-html") + ".html";
	}
}

//transforms time into human-readable format
function adaptTime()	{
	var elements = getElementsByClassName("logtimestart");
	
	for (i in elements) {
		rawDate = elements[i].textContent;
		
		var dateEndIndex = rawDate.indexOf("T");
		var timeEndIndex = rawDate.indexOf("+");
		
		var date = rawDate.substring(0, dateEndIndex);
		var time = rawDate.substring(dateEndIndex+1, timeEndIndex);
		
		elements[i].textContent = date + " " + time;
	}
}

//hides all the elements with the specified classname
function hide(classname)	{
	var elements = getElementsByClassName(classname);
	
	for (i in elements) {
		if (elements[i].className.search("invisible") == -1)	{
			elements[i].className = elements[i].className + " invisible";
		}
	}
}

//shows all the elements with the specified classname
function show(classname)	{
	var elements = getElementsByClassName(classname);
	for (i in elements) {
		elements[i].className = elements[i].className.replace(" invisible","");
	}
}

//show the test details with the index specified
function showByIndex(classname, index)	{
	var elements = getElementsByClassName(classname);
	elements[index].className = elements[index].className.replace(" invisible", "");
}

//transforms duration to human format
function durationToHumanFormat(durationms)	{
	var result = "";
	if (durationms>1000*60*60*24)	{
		result = result + Math.floor(durationms / (1000*60*60*24)) + " d ";
		durationms = durationms % (1000*60*60*24);
	} 
	if (durationms>1000*60*60)	{
		result = result + Math.floor(durationms / (1000*60*60)) + " h ";
		durationms = durationms % (1000*60*60);
	} 
	if (durationms>1000*60)	{
		result = result + Math.floor(durationms / (1000*60)) + " m ";
		durationms = durationms % (1000*60);
	} 
	if (durationms>1000)	{
		result = result + Math.floor(durationms / 1000) + " s ";
		durationms = durationms % 1000;
	} 

	result = result + durationms + " ms";
	
	return result;
}

//draws summary diagram
function drawSummaryDiagram(passed, failed, skipped)	{
	var initAngle = 0;
	var passedAngle = initAngle + (passed/(passed+failed+skipped))*2*Math.PI;
	var failedAngle = passedAngle + (failed/(passed+failed+skipped))*2*Math.PI;
	var skippedAngle = failedAngle + (skipped/(passed+failed+skipped))*2*Math.PI;
	var centerX = 150;
	var centerY = 150;
	var radius = 150;
	
	var radiusPassed = radius - Math.round(radius*0.1*(passed/(passed+failed+skipped)));
	var radiusFailed = radius - Math.round(radius*0.1*(failed/(passed+failed+skipped)));
	var radiusSkipped = radius - Math.round(radius*0.1*(skipped/(passed+failed+skipped)));
	
	var canvas=document.getElementById('summaryDiagram');
	var ctx=canvas.getContext('2d');
	ctx.strokeStyle = '#000000';
	ctx.lineWidth   = 1;

	//passed piece
	if (passed!=0)	{
	ctx.beginPath();
	ctx.moveTo(centerX,centerY);
	ctx.arc(centerX,centerY,radiusPassed,initAngle,passedAngle, false);
	ctx.closePath();
	ctx.fillStyle = "#A9F5A9";
	ctx.fill();
	ctx.stroke();
	}
	
	if (failed!=0)	{
	//failed piece
	ctx.beginPath();
	ctx.moveTo(centerX,centerY);
	ctx.arc(centerX,centerY,radiusFailed,passedAngle,failedAngle, false);
	ctx.closePath();
	ctx.fillStyle = "#F5A9A9";
	ctx.fill();
	ctx.stroke();
	}
	
	if (skipped!=0)	{
	//skipped piece
	ctx.beginPath();
	ctx.moveTo(centerX,centerY);
	ctx.arc(centerX,centerY,radiusSkipped,failedAngle,skippedAngle, false);
	ctx.closePath();
	ctx.fillStyle = "#D8D8D8";
	ctx.fill();
	ctx.stroke();
	}
}

//draws legend for the diagram
function drawLegend(passed, failed, skipped)	{
	var canvas=document.getElementById('legendDiagram');
	var ctx=canvas.getContext('2d');
	var centerX = 120;
	var centerY = 150;
	ctx.lineWidth  = 1;
	ctx.strokeStyle="black";
	ctx.font = "12pt Arial";
	
	var passedPercentage = Math.round(100*passed/(passed+failed+skipped));
	var failedPercentage = Math.round(100*failed/(passed+failed+skipped));
	var skippedPercentage = (100-passedPercentage-failedPercentage);

	//passed legend
	if (passed!=0)	{
	ctx.fillStyle = "#A9F5A9";
	ctx.fillRect(centerX-100, centerY-50, 30, 30);
	ctx.strokeRect(centerX-100, centerY-50, 30, 30);
	ctx.fillStyle = "#000000";
	ctx.fillText("Passed: " + passedPercentage + " %", centerX-60, centerY-30);
	}

	//failed legend
	if (failed!=0)	{
	ctx.fillStyle = "#F5A9A9";
	ctx.fillRect(centerX-100, centerY-15, 30, 30);
	ctx.strokeRect(centerX-100, centerY-15, 30, 30);
	ctx.fillStyle = "#000000";
	ctx.fillText("Failed: " + failedPercentage  + " %", centerX-60, centerY+5);
	}
	
	//skipped legend
	if (skipped!=0)	{
	ctx.fillStyle = "#D8D8D8";
	ctx.fillRect(centerX-100, centerY+20, 30, 30);
	ctx.strokeRect(centerX-100, centerY+20, 30, 30);
	ctx.fillStyle = "#000000";
	ctx.fillText("Skipped: " + skippedPercentage  + " %", centerX-60, centerY+40);
	}
}

function setVisible(obj)
{
	closeAllPopups();
	obj = document.getElementById(obj);
	obj.style.visibility = (obj.style.visibility == 'visible') ? 'hidden' : 'visible';
}

function closeAllPopups()	{
	var elements = getElementsByClassName('popup');
	for (i in elements) {
		elements[i].style.visibility = 'hidden';
	}
}
		</script>
	</xsl:template>
</xsl:stylesheet>
