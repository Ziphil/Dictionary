<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="head-name-font-family" select="'Arial, IPAゴシック'"/>
  <xsl:param name="font-family" select="'Times New Roman, IPA明朝'"/>
  <xsl:param name="punctuation" select="', '"/>

  <xsl:template match="/">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="body" page-height="29.7cm" page-width="21.0cm" margin="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="body">
        <fo:flow flow-name="xsl-region-body">
          <xsl:apply-templates/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template match="words/word">
    <fo:block space-before="5mm" space-after="5mm">
      <xsl:call-template name="name"/>
      <xsl:call-template name="equivalents"/>
    </fo:block>
  </xsl:template>

  <xsl:template name="name">
    <fo:block font-family="{$head-name-font-family}" font-size="12pt">
      <xsl:value-of select="name"/>
    </fo:block>
  </xsl:template>

  <xsl:template name="equivalents">
    <fo:block>
      <xsl:for-each select="equivalents/equivalent">
        <fo:block font-family="{$font-family}" font-size="10pt">
          <fo:inline font-size="0.8em" border="0.2mm #888888 solid" padding="0.2mm 0.5mm 0.2mm 0.5mm" space-end="1mm">
            <xsl:value-of select="title"/>
          </fo:inline>
          <fo:inline>
            <xsl:for-each select="names/name">
              <xsl:value-of select="."/>
              <xsl:if test="position() != last()">
                <xsl:value-of select="$punctuation"/>
              </xsl:if>
            </xsl:for-each>
          </fo:inline>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>