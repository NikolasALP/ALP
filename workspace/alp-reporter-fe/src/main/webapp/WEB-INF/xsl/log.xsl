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
	xmlns:t="http://alp.lohika.com/log/schema" xmlns:ns2="http://alp.lohika.com/log/elements/schema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xslFormatting="urn:xslFormatting">

	<xsl:output method="html" indent="yes" />
	<xsl:include href="log-extra.xsl" />

	<xsl:param name="contextPath" />
	<xsl:param name="backButtonUrl" />

	<xsl:template match="/t:log">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<link rel="stylesheet" type="text/css" href="{$contextPath}/static/css/log.css" />
				<title>Test Method Details</title>
				<script type="text/javascript" src="{$contextPath}/static/js/log.js" />
			</head>
			<body>
				<!-- Content layer -->
				<div id="content">

					<!-- Write options at the top of the page -->
					<xsl:call-template name="writeOptionsTable" />
					<!-- Main table with log entries -->
					<div id="mainlogtable">
						<table class="table" id="log">
							<tbody>
								<!-- Header of the table -->
								<tr class="tableheader">
									<td class="tableheadertext">Level</td>
									<td class="tableheadertext">Time</td>
									<td class="tableheadertext logger" style="height:auto;">Logger</td>
									<td class="tableheadertext thread">Thread</td>
									<td class="tableheadertext">Message</td>
								</tr>

								<!-- Event block. Detects each event and writes corresponded entry 
									into a log table -->
								<xsl:for-each select="t:event">
									<xsl:element name="tr">
										<!-- If entry is hightlighted then make it of 'hightlight' class -->
										<xsl:if test="ns2:comment/@type='highlight'">
											<xsl:attribute name="class">highlight</xsl:attribute>
										</xsl:if>
										<!-- If entry is default then make it of 'default' class -->
										<xsl:if test="ns2:comment/@type='default'">
											<xsl:attribute name="class">default</xsl:attribute>
										</xsl:if>

										<xsl:if test="@level='ERROR'">
											<xsl:attribute name="class">error</xsl:attribute>
										</xsl:if>

										<xsl:if test="@level='WARN'">
											<xsl:attribute name="class">warn</xsl:attribute>
										</xsl:if>

										<!-- Write log level of log entry -->
										<td class="tablecelltext level">
											<xsl:value-of select="@level" />
										</td>
										<!-- Write timestamp of log entry -->
										<td class="tablecelltext">
											<!-- Translation from timestamp to time is performed by javascript -->
											<script type="text/javascript">
												document.write(getTimeFromTimestamp(
												<xsl:value-of select="@timestamp" />
												));
											</script>
										</td>

										<!-- Write loggername of a log entry -->
										<xsl:element name="td">
											<xsl:attribute name="class">tablecelltext logger</xsl:attribute>
											<xsl:attribute name="style">height:auto;</xsl:attribute>
											<!-- Write full logger path in popup hint (title tag) -->
											<xsl:attribute name="title"><xsl:value-of
												select="@logger" /></xsl:attribute>
											<!-- In a log table only short name of logger is displayed -->
											<!-- Template adapts full logger name into short one -->
											<xsl:call-template name="getClassName">
												<xsl:with-param name="classname" select="@logger" />
											</xsl:call-template>
										</xsl:element>

										<!-- Write the thread into log entry -->
										<td class="tablecelltext thread">
											<xsl:value-of select="@thread" />
										</td>

										<!-- Write the description into log entry -->
										<td class="tablecelltext description">
											<div class="">
												<!-- Create table inside description cell of log table -->
												<table class="descriptiontable">
													<xsl:call-template name="writeLogElement" />
												</table>
											</div>
										</td>
									</xsl:element>
								</xsl:for-each>
							</tbody>
						</table>
						<br />
						<div class="noentiresfoundmessage" id="nomessages"></div>
					</div>
				</div>
				<script type="text/javascript">
					window.onload=collapseByClass('thread');
					window.onload=levelHander('info');
				</script>
			</body>
		</html>
	</xsl:template>

	<!-- This template makes short logger name from full one e.g. makes 'LogExampleTest' 
		from 'com.lohika.alp.log.LogExampleTest' -->
	<xsl:template name="getClassName">
		<xsl:param name="classname" />
		<xsl:choose>
			<xsl:when test="contains($classname, '.')">
				<xsl:call-template name="getClassName">
					<xsl:with-param name="classname"
						select="substring-after($classname, '.')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$classname" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Writes an action into the log table -->
	<xsl:template name="writeAction">
		<tr>
			<!-- Writes an icon into the first cell of description table -->
			<td class="imagecol">
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="$contextPath" />/static/images/log<xsl:value-of
						select="ns2:webelement/@type" />.gif</xsl:attribute>
				</xsl:element>
			</td>
			<!-- Writes a text into the second cell of description table -->
			<!-- The format is "email -> type 'email@google.com'" -->
			<td class="descriptioncol">
				<xsl:value-of select="ns2:webelement/@name" />
				<xsl:text> </xsl:text>-&gt;
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:if test="ns2:arg">
					<xsl:text> </xsl:text>
					'
					<xsl:value-of select="ns2:arg/text()" />
					'
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- Writes an textarea into the log table -->
	<xsl:template name="writeTextarea">
		<tr>
			<!-- Writes an icon into the first cell of description table -->
			<td class="imagecol">
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="$contextPath" />/static/images/logtextarea.gif</xsl:attribute>
				</xsl:element>
			</td>
			<!-- Write content of the text area -->
			<td class="descriptioncol">
				<xsl:variable name="id" select="generate-id()" />
				<!-- A link to collapse/expand textarea content -->
				<xsl:element name="a">
					<xsl:attribute name="href">javascript:collapse('<xsl:copy-of
						select="$id" />')</xsl:attribute>
					<xsl:value-of select="@name" />
				</xsl:element>
				<!-- Textarea content -->
				<xsl:element name="div">
					<xsl:attribute name="id">
				<xsl:copy-of select="$id" />
				</xsl:attribute>
					<xsl:attribute name="style">display:none; height:auto;</xsl:attribute>
					<xsl:element name="textarea">
						<xsl:attribute name="readonly">true</xsl:attribute>
						<xsl:attribute name="wrap">off</xsl:attribute>
						<xsl:attribute name="cols">100</xsl:attribute>
						<xsl:attribute name="rows">20</xsl:attribute>
						<xsl:value-of select="ns2:content" />
					</xsl:element>
				</xsl:element>
			</td>
		</tr>
	</xsl:template>

	<!-- Writes link into the log table -->
	<xsl:template name="writeLink">
		<tr>
			<!-- A link to collapse/expand textarea content -->
			<td class="imagecol">
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="$contextPath" />/static/images/loglink.gif</xsl:attribute>
				</xsl:element>
			</td>
			<!-- Links to open screenshot -->
			<td class="descriptioncol">
				<a href="{@url}">
					<xsl:if test="@description">
						<xsl:value-of select="@description" />
					</xsl:if>
					<xsl:if test="not(@description)">
						<xsl:value-of select="@url" />
					</xsl:if>
				</a>
			</td>
		</tr>
	</xsl:template>

	<!-- Writes screenshot into the log table -->
	<xsl:template name="writeScreenshot">
		<tr>
			<!-- A link to collapse/expand textarea content -->
			<td class="imagecol">
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="$contextPath" />/static/images/logimage.gif</xsl:attribute>
				</xsl:element>
			</td>
			<!-- Links to open screenshot -->
			<td class="descriptioncol">
				<a href="{@url}">
					<xsl:value-of select="@description" />
				</a>
			</td>
		</tr>
	</xsl:template>

	<!-- Writes web element into the log table -->
	<xsl:template name="writeWebelement">
		<tr>
			<td class="imagecol">
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="$contextPath" />/static/images/log<xsl:value-of
						select="@type" />.gif</xsl:attribute>
				</xsl:element>
			</td>
			<td class="descriptioncol">
				<xsl:value-of select="@name" />
			</td>
		</tr>
	</xsl:template>

	<!-- Writes plain text into the log table -->
	<xsl:template name="writeSimpletext">
		<tr>
			<td class="imagecol">
				<!-- <xsl:element name="img"> <xsl:attribute name="src"><xsl:value-of 
					select="$contextPath" />/static/images/logcomment.gif</xsl:attribute> </xsl:element> -->
			</td>
			<td class="descriptioncol">
				<xsl:call-template name="insertBreaks" />
				<!-- <xsl:value-of select="self::text()" /> -->
			</td>
		</tr>
	</xsl:template>

	<!-- Writes complex comment entry into the log table -->
	<xsl:template name="writeComment">
		<!-- Select children of comment entry -->
		<!-- The union of text nodes excluding the nodes with whitespaces -->
		<!-- and all the child elements -->
		<xsl:for-each select="node()[self::text()[normalize-space()]]|*">
			<xsl:choose>
				<xsl:when test="contains(name(.),'action')">
					<xsl:call-template name="writeAction" />
				</xsl:when>
				<xsl:when test="contains(name(.),'link')">
					<xsl:call-template name="writeLink" />
				</xsl:when>
				<xsl:when test="contains(name(.),'screenshot')">
					<xsl:call-template name="writeScreenshot" />
				</xsl:when>
				<xsl:when test="contains(name(.),'textarea')">
					<xsl:call-template name="writeTextarea" />
				</xsl:when>
				<xsl:when test="contains(name(.),'webelement')">
					<xsl:call-template name="writeWebelement" />
				</xsl:when>
				<xsl:when test="self::text()">
					<xsl:call-template name="writeSimpletext" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="External" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- Detects the type of log entry and writes it into log table -->
	<xsl:template name="writeLogElement" match='//t:event'>
		<xsl:for-each select='node()[self::text()[normalize-space()]]|*'>
			<xsl:choose>
				<xsl:when test="contains(name(.),'action')">
					<xsl:call-template name="writeAction" />
				</xsl:when>
				<xsl:when test="contains(name(.),'link')">
					<xsl:call-template name="writeLink" />
				</xsl:when>
				<xsl:when test="contains(name(.),'screenshot')">
					<xsl:call-template name="writeScreenshot" />
				</xsl:when>
				<xsl:when test="contains(name(.),'textarea')">
					<xsl:call-template name="writeTextarea" />
				</xsl:when>
				<xsl:when test="contains(name(.),'webelement')">
					<xsl:call-template name="writeWebelement" />
				</xsl:when>
				<xsl:when test="contains(name(.),'comment')">
					<xsl:call-template name="writeComment" />
				</xsl:when>
				<xsl:when test="self::text()">
					<xsl:call-template name="writeSimpletext" />
				</xsl:when>
				<!-- Call external template to process custom log elements -->
				<xsl:otherwise>
					<xsl:call-template name="External" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="writeOptionsTable">

		<div id="optionsbar" class="options">
			<!-- user options table -->
			<br />
			<table class="optiontable">
				<tr>
					<!-- Back button in the top -->
					<td class="optiontablecell">
					<xsl:if test="string-length($backButtonUrl)>0">
					<xsl:element name="div">
						<xsl:element name="input">
						<xsl:attribute name="type">button</xsl:attribute>
						<xsl:attribute name="value">Back</xsl:attribute>
						<xsl:attribute name="onClick">
							window.location.href="<xsl:value-of select="$backButtonUrl" />"
						</xsl:attribute>
						</xsl:element>
					</xsl:element>
					</xsl:if>
					</td>
					<!-- logging level to display -->
					<td class="optiontablecell">
						<select name="menu"
							onchange="levelHander(this.options[this.selectedIndex].value);">
							<option value="trace">trace</option>
							<option value="debug">debug</option>
							<option value="info" selected="true">info</option>
							<option value="warn">warn</option>
							<option value="error">error</option>
							<option value="fatal">fatal</option>
						</select>
						Logging Level
					</td>
					<td width="5%"></td>
					<!-- Show/hide logger column -->
					<td class="optiontablecell">
						<input type="checkbox" value="logger" checked=""
							onclick="javascript:collapseByClass('logger')" />
						Logger
						<br />
						<input type="checkbox" value="thread"
							onclick="javascript:collapseByClass('thread')" />
						Thread
					</td>
				</tr>
			</table>
		</div>
		<br />
	</xsl:template>

	<!-- Convert NEWLINE into BR -->
	<xsl:template name="insertBreaks" match="text()">
		<xsl:param name="pText" select="." />

		<xsl:choose>
			<xsl:when test="not(contains($pText, '&#xA;'))">
				<xsl:copy-of select="$pText" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring-before($pText, '&#xA;')" />
				<br />
				<xsl:call-template name="insertBreaks">
					<xsl:with-param name="pText"
						select="substring-after($pText, '&#xA;')" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
