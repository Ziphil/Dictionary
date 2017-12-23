<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="head-font-family" select="'Source Han Serif'"/>
  <xsl:param name="caption-font-family" select="'Source Han Serif'"/>
  <xsl:param name="font-family" select="'Source Han Serif'"/>
  <xsl:param name="head-font-size" select="'10pt'"/>
  <xsl:param name="caption-font-size" select="'20pt'"/>
  <xsl:param name="font-size" select="'8pt'"/>
  <xsl:param name="color" select="'#0A5B5B'"/><!--180°,80%,20%-->
  <xsl:param name="light-color" select="'#B2E5E5'"/><!--180°,50%,80%-->
  <xsl:param name="leader-color" select="'#9DBDBD'"/>
  <xsl:param name="line-height" select="1.4"/>
  <xsl:param name="border-width" select="'0.2mm'"/>
  <xsl:param name="leader-border-width" select="'0.1mm'"/>
  <xsl:param name="caption-border-width" select="'0.5mm'"/>
  <xsl:param name="inner-space" select="'0.5mm'"/>
  <xsl:param name="inner-margin" select="'1mm'"/>
  <xsl:param name="punctuation" select="', '"/>
  <xsl:param name="variation-marker" select="'→'"/>
  <xsl:param name="relation-marker" select="'cf:'"/>

  <xsl:template match="/">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="body"
                               page-width="21.0cm"
                               page-height="29.7cm"
                               margin="10mm 15mm 10mm 15mm">
          <fo:region-body margin="13mm 5mm 13mm 5mm"
                          column-count="2"
                          column-gap="5mm"/>
          <fo:region-before extent="5mm" precedence="true"/>
          <fo:region-after extent="5mm" precedence="true"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <xsl:call-template name="bookmark"/>
      <fo:page-sequence master-reference="body">
        <fo:static-content flow-name="xsl-region-before">
          <fo:block-container height="5mm" display-align="after">
            <fo:block font-family="{$font-family}"
                      font-size="{$font-size}"
                      text-align-last="justify"
                      border-bottom="{$border-width} #000000 solid">
              <fo:inline padding="0mm 1mm 0mm 1mm">
                <fo:retrieve-marker retrieve-class-name="name"
                                    retrieve-position="first-including-carryover" 
                                    retrieve-boundary="page-sequence"/>
              </fo:inline>
              <fo:leader leader-pattern="space"/>
              <fo:inline padding="0mm 1mm 0mm 1mm">
                <fo:retrieve-marker retrieve-class-name="name"
                                    retrieve-position="last-starting-within-page" 
                                    retrieve-boundary="page-sequence"/>
              </fo:inline>
            </fo:block>
          </fo:block-container>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block-container height="5mm" display-align="before">
            <fo:block font-family="{$font-family}"
                      font-size="{$font-size}"
                      text-align="center">
              <fo:inline>
                <xsl:text>— </xsl:text>
                <fo:page-number/>
                <xsl:text> —</xsl:text>
              </fo:inline>
            </fo:block>
          </fo:block-container>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <xsl:apply-templates/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template name="bookmark">
    <fo:bookmark-tree>
      <fo:bookmark internal-destination="caption-{words/caption[1]}"
                   starting-state="hide"> 
        <fo:bookmark-title font-weight="bold">目次</fo:bookmark-title>
        <xsl:for-each select="words/caption">
          <fo:bookmark internal-destination="caption-{.}">
            <fo:bookmark-title>
              <xsl:value-of select="."/>
            </fo:bookmark-title>
          </fo:bookmark>
        </xsl:for-each>
      </fo:bookmark>
    </fo:bookmark-tree>
  </xsl:template>

  <xsl:template match="words/caption">
    <fo:block space-before="3mm"
              space-before.minimum="2mm"
              space-before.maximum="5mm"
              space-after="3mm"
              space-after.minimum="2mm"
              space-after.maximum="5mm"
              text-align="center">
      <xsl:attribute name="id">
        <xsl:text>caption-</xsl:text>
        <xsl:value-of select="."/>
      </xsl:attribute>
      <fo:inline-container width="50%">
        <fo:block padding="{$caption-border-width}"
                  border="{$caption-border-width} {$color} solid">
          <fo:block font-family="{$caption-font-family}"
                    font-size="{$caption-font-size}"
                    font-weight="bold"
                    line-height="1.8"
                    color="#FFFFFF"
                    background-color="{$color}"
                    text-align="center">
            <xsl:value-of select="."/>
          </fo:block>
        </fo:block>
      </fo:inline-container>
    </fo:block>
  </xsl:template>

  <xsl:template match="words/word">
    <fo:block space-before="3mm"
              space-before.minimum="2mm"
              space-before.maximum="5mm"
              space-after="3mm"
              space-after.minimum="2mm"
              space-after.maximum="5mm"
              border="{$border-width} {$color} solid">
      <fo:marker marker-class-name="name">
        <xsl:value-of select="name"/>
      </fo:marker>
      <xsl:call-template name="name"/>
      <xsl:call-template name="equivalents"/>
      <xsl:if test="count(informations/information) > 0">
        <xsl:call-template name="leader"/>
      </xsl:if>
      <xsl:if test="count(informations/information) > 0">
        <xsl:call-template name="informations"/>
      </xsl:if>
      <xsl:if test="count(variations/variation) > 0 or count(relations/relation) > 0">
        <xsl:call-template name="leader"/>
      </xsl:if>
      <xsl:if test="count(variations/variation) > 0">
        <xsl:call-template name="variations"/>
      </xsl:if>
      <xsl:if test="count(relations/relation) > 0">
        <xsl:call-template name="relations"/>
      </xsl:if>
    </fo:block>
  </xsl:template>

  <xsl:template name="name">
    <fo:block font-family="{$head-font-family}"
              font-size="{$head-font-size}"
              font-weight="bold"
              line-height="{$line-height}"
              color="#FFFFFF"
              background-color="{$color}"
              keep-with-next.within-column="always"
              keep-with-next.within-page="always">
      <xsl:attribute name="id">
        <xsl:text>word-</xsl:text>
        <xsl:value-of select="id"/>
      </xsl:attribute>
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
                     font-size="0.8em"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
            <xsl:value-of select="title"/>
          </fo:inline>
          <fo:inline>
            <xsl:text> </xsl:text>
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
                    line-height="1"
                    keep-with-next.within-column="always"
                    keep-with-next.within-page="always">
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

  <xsl:template name="variations">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="variations/variation">
        <fo:block font-family="{$font-family}"
                  font-size="{$font-size}"
                  line-height="{$line-height}">
          <fo:inline space-end="0.2mm"
                     font-size="0.8em"
                     color="{$color}">
            <xsl:value-of select="$variation-marker"/>
          </fo:inline>
          <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                     font-size="0.8em"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
            <xsl:value-of select="title"/>
          </fo:inline>
          <fo:inline>
            <xsl:text> </xsl:text>
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

  <xsl:template name="relations">
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              margin-left="{$inner-margin}"
              margin-right="{$inner-margin}">
      <xsl:for-each select="relations/relation">
        <fo:block font-family="{$font-family}"
                  font-size="{$font-size}"
                  line-height="{$line-height}">
          <fo:inline space-end="0.2mm"
                     font-size="0.8em"
                     color="{$color}">
            <xsl:value-of select="$relation-marker"/>
          </fo:inline>
          <fo:inline padding="0.2mm 0.5mm 0.2mm 0.5mm"
                     font-size="0.8em"
                     border="{$border-width} {$color} solid"
                     background-color="{$light-color}">
            <xsl:value-of select="title"/>
          </fo:inline>
          <fo:inline>
            <xsl:text> </xsl:text>
          </fo:inline>
          <fo:inline>
            <xsl:for-each select="entries/entry">
              <fo:basic-link internal-destination="word-{id}">
                <xsl:value-of select="name"/>
              </fo:basic-link>
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
    <fo:block space-before="{$inner-space}"
              space-after="{$inner-space}"
              border-bottom="{$border-width} {$leader-color} dotted"
              keep-with-previous.within-column="always"
              keep-with-previous.within-page="always">
    </fo:block>
  </xsl:template>

</xsl:stylesheet>