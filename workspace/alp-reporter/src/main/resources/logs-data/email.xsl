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
			</head>
			<body>
				<xsl:call-template name="showTests" />
			</body>
		</html>
	</xsl:template>

	<!-- Creates navigation bar -->
	<xsl:template name="showTests">
		<div>
			<p>See more detailed logs in attached archive file.</p>
			<!-- Look into each suite -->
			<xsl:for-each select="ns2:suite">
				<!-- write a name of the suite -->
				<h2>
					<xsl:value-of select="@name" />
				</h2>
				<!-- write all the tests of this suite -->
				<xsl:for-each select="ns2:test">
				<table style="font: 16px lucida,sans-serif;">
				<tbody>
					<tr style="background-color:black; color:white;">
						<td>Test Name</td>
						<td>Passed</td>
						<td>Failed</td>
						<td>Skipped</td>
					</tr>
					<xsl:element name="tr">
					<xsl:choose>
						<xsl:when test="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])=0">
							<xsl:attribute name="style">background:#CEF6CE;</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="style">background:#F6CECE;</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
						
						<!-- write test case name -->
						<td style="padding: 0 5;">
							<xsl:value-of select="@name" />
						</td>
						<!-- write quick numbers of the passed, failed and skipped methods -->
						<td style="padding: 0 5;">
							<xsl:value-of
								select="count(ns2:test-instance/ns2:test-method[@status='SUCCESS'])" />
						</td>
						<td style="padding: 0 5;">
							<xsl:value-of
								select="count(ns2:test-instance/ns2:test-method[@status='FAILURE'])" />
						</td>
						<td style="padding: 0 5;">
							<xsl:value-of select="count(ns2:test-instance/ns2:test-method[@status='SKIP'])" />
						</td>
					</xsl:element>
				</tbody>
				</table>
				<xsl:for-each select="ns2:test-instance">
					<div style="border: 1pt solid #ADD8E6;">
						<div style="background: none repeat scroll 0 0 #ADD8E6;border-width: 1pt medium medium 1pt; font: 14px lucida,sans-serif; margin: 1px;"><xsl:value-of select="@class" /></div>
						<!-- The table with method results -->
						<table styel="border: 1pt solid #ADD8E6;border-collapse: collapse;font: 12px lucida,sans-serif;margin: 0.5%; text-align: center; vertical-align: middle;">
							<tbody>
								<!-- table header -->
								<tr style="background:#ADD8E6; text-align:center;border-width: 1pt medium medium 1pt;">
									<td>test method</td>
									<td>groups</td>
									<td>start time</td>
									<td>duration</td>
									<td>exception</td>
								</tr>
								<!-- table content -->
								<xsl:for-each select="ns2:test-method">
									<!-- create table entry with classname which has 'success', 'failure' or 'skip' work -->
									<xsl:element name="tr">
									<xsl:choose>
										<xsl:when test="@status='FAILURE'">
											<xsl:attribute name="style">background:#F6CECE;</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="style">background:#CEF6CE;</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
										<!-- first column: testmethod name -->
										<td style="padding: 0 5;">
											<xsl:value-of select="@name" />
										</td>
										<!-- second column: groups of test method -->
										<td style="padding: 0 5;">
										<xsl:if test='groups'>
											<xsl:for-each select="groups/group">
												<xsl:if test="position()!=1" xml:space="preserve">, </xsl:if>
												<xsl:value-of select="current()" />
											</xsl:for-each>
										</xsl:if>
										</td>
										<!-- third column: method start time -->
										<td style="padding: 0 5;">
											<div>
												<xsl:value-of select="format-dateTime(@started-at, '[M01]/[D01]/[Y0001] [H01]:[m01]:[s01]', 'en', (), 'gb')" />
											</div>
										</td>
										<!-- fourth column: duration -->
										<td style="padding: 0 5;">
											<xsl:value-of select="@duration-ms" />
											<xsl:text> ms</xsl:text>
										</td>
										<!-- exception -->
										<td style="padding: 0 5;">
											<xsl:if test='exception'>
												<!-- write exception class and exception messate -->
												<div>
													<xsl:if test='exception/@class'>
														<xsl:value-of select="exception/@class" />
													</xsl:if>
													<xsl:if test='exception/message'>
														(<xsl:value-of select="exception/message" />)
													</xsl:if>
												</div>
											</xsl:if>
										</td>
									</xsl:element>
								</xsl:for-each>
							</tbody>
						</table>
					</div>
				<!-- test instance -->
				</xsl:for-each>
				<!-- test -->
				</xsl:for-each>
			<!-- suite -->
			</xsl:for-each>
			<hr/>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
