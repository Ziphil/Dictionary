<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="head-font-family" select="'Source Han Serif'"/>
  <xsl:param name="font-family" select="'Source Han Serif'"/>
  <xsl:param name="head-font-size" select="'10pt'"/>
  <xsl:param name="font-size" select="'8pt'"/>
  <xsl:param name="color" select="'#0A5B5B'"/><!--180°,80%,20%-->
  <xsl:param name="light-color" select="'#B2E5E5'"/><!--180°,50%,80%-->
  <xsl:param name="line-height" select="1.4"/>
  <xsl:param name="border-width" select="'0.2mm'"/>
  <xsl:param name="inner-space" select="'0.5mm'"/>
  <xsl:param name="inner-margin" select="'1mm'"/>
  <xsl:param name="punctuation" select="', '"/>

  <xsl:template match="/">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="body" page-height="29.7cm" page-width="21.0cm" margin="2cm">
          <fo:region-body column-count="2" column-gap="5mm"/>
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
    <fo:block space-before="3mm"
              space-before.minimum="2mm"
              space-before.maximum="5mm"
              space-after="3mm"
              space-after.minimum="2mm"
              space-after.maximum="5mm"
              border="{$border-width} {$color} solid">
      <xsl:call-template name="name"/>
      <xsl:call-template name="equivalents"/>
      <xsl:call-template name="leader"/>
      <xsl:call-template name="informations"/>
    </fo:block>
  </xsl:template>

  <xsl:template name="name">
    <fo:block font-family="{$head-font-family}"
              font-size="{$head-font-size}"
              font-weight="bold"
              line-height="{$line-height}"
              color="#FFFFFF"
              background-color="{$color}">
      <fo:inline padding="0mm 1.5mm 0mm 1.5mm">
        <xsl:value-of select="name"/>
      </fo:inline>
    </fo:block>
  </xsl:template>

  <xsl:template name="equivalents">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="equivalents/equivalent">
        <fo:block font-family="{$font-family}"
                  font-size="{$font-size}"
                  line-height="{$line-height}">
          <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                     space-end="0.8mm"
                     font-size="0.8em"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
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

  <xsl:template name="leader">
    <xsl:if test="count(informations/information) > 0">
      <fo:block space-before="{$inner-space}"
                space-after="{$inner-space}"
                border-bottom="{$border-width} {$color} solid">
      </fo:block>
    </xsl:if> 
  </xsl:template>

  <xsl:template name="informations">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="informations/information">
        <fo:block font-family="{$font-family}"
                  font-size="{$font-size}"
                  line-height="{$line-height}">
          <fo:block margin-left="-{$inner-margin}"
                    margin-bottom="0.2mm"
                    line-height="1">
            <fo:inline padding="0mm 3mm 0mm 0.8mm"
                       font-size="0.8em"
                       color="{$color}"
                       border-bottom="{$border-width} {$color} solid">
              <xsl:value-of select="title"/>
            </fo:inline>
          </fo:block>
          <fo:block>
            <xsl:value-of select="text"/>
          </fo:block>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>  

</xsl:stylesheet>