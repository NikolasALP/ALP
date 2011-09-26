<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet version="1.0"
    xmlns:t="http://alp.lohika.com/log/schema" 
	xmlns:ns2="http://alp.lohika.com/log/elements/schema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xslFormatting="urn:xslFormatting">

 <xsl:template name="External">
     <xsl:choose>
     <xsl:when test="ns2:nothandledaction1">
	     <td class="description">
              <div class="{ns2:nothandledaction1/ns2:self/@type}">
                 <label class="newlogstring">
                 <xsl:value-of select="ns2:nothandledaction1/ns2:self/@name"/> -> <xsl:value-of select="ns2:action/@name"/> it is not handled action 1<xsl:if test="ns2:action/ns2:arg"> '<xsl:value-of select="ns2:action/ns2:arg/text()"/>'</xsl:if></label>
              </div>
          </td>
     </xsl:when>
     <xsl:when test="ns2:nothandledaction2">
	     <td class="description">
              <div class="{ns2:nothandledaction2/ns2:self/@type}">
                 <label class="newlogstring">
                 <xsl:value-of select="ns2:nothandledaction2/ns2:self/@name"/> -> <xsl:value-of select="ns2:action/@name"/> it is not handled action 2<xsl:if test="ns2:action/ns2:arg"> '<xsl:value-of select="ns2:action/ns2:arg/text()"/>'</xsl:if></label>
              </div>
          </td>
     </xsl:when>
     </xsl:choose>
 </xsl:template>
</xsl:stylesheet>