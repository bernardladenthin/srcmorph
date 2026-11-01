<?xml version="1.0" encoding="UTF-8"?>
<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
<!-- Renders a monthly account statement as an HTML page: a header, one row per booking, a total. -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:st="urn:example:statement">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:param name="currencySymbol" select="'EUR'"/>
    <xsl:param name="highlightAbove" select="1000"/>

    <xsl:key name="bookingsByDay" match="st:booking" use="st:valueDate"/>

    <xsl:template match="/st:statement">
        <html>
            <head><title>Statement <xsl:value-of select="@period"/></title></head>
            <body>
                <xsl:call-template name="statementHeader"/>
                <table>
                    <xsl:apply-templates select="st:booking"/>
                </table>
                <xsl:call-template name="statementTotal"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="statementHeader">
        <h1>Account <xsl:value-of select="st:accountNumber"/></h1>
        <p>Period <xsl:value-of select="@period"/></p>
    </xsl:template>

    <xsl:template match="st:booking">
        <tr>
            <xsl:if test="number(st:amount) &gt; $highlightAbove">
                <xsl:attribute name="class">large</xsl:attribute>
            </xsl:if>
            <td><xsl:value-of select="st:valueDate"/></td>
            <td><xsl:value-of select="st:purpose"/></td>
            <td><xsl:value-of select="format-number(st:amount, '#,##0.00')"/> <xsl:value-of select="$currencySymbol"/></td>
        </tr>
    </xsl:template>

    <xsl:template name="statementTotal">
        <p>Total: <xsl:value-of select="format-number(sum(st:booking/st:amount), '#,##0.00')"/></p>
    </xsl:template>
</xsl:stylesheet>
