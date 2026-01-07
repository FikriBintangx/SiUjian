<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wix="http://schemas.microsoft.com/wix/2006/wi">
  <xsl:output indent="yes" method="xml"/>

  <!-- Identity transform: Copy everything as is by default -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Match every Component element and add Win64="yes" -->
  <xsl:template match="wix:Component">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="Win64">yes</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
