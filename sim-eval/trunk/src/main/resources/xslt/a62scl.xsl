<?xml version="1.0"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="text"/>
  
  <xsl:template match="/">setupUCs :: AprosSequence ()
setupUCs = let node n m x = x
           in <xsl:apply-templates select="node"/>
  </xsl:template>

  <xsl:template match="node">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/>node "<xsl:value-of select="@name"
    />" "<xsl:value-of select="@moduleName"/>" mdo {<xsl:text>
</xsl:text>
    <xsl:apply-templates select="property|node">
      <xsl:with-param name="indent" select="concat('  ', $indent)"/>
    </xsl:apply-templates>
    <xsl:copy-of select="$indent"/>}<xsl:if
    test="position() != last()">;</xsl:if>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template
      match="property[@type='expression' and ../@isUC = 'False']"
      priority="1">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/>set "<xsl:value-of
    select="../@moduleName"/>#<xsl:value-of select="@name"
    />" (<xsl:value-of select="@value"/>)<xsl:if
    test="position() != last()">;</xsl:if>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="property" priority="0">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/><xsl:value-of select="@name"
    /> = (<xsl:value-of select="@value"/>)<xsl:if
    test="position() != last()">;</xsl:if>
    <xsl:text>
</xsl:text>
  </xsl:template>

</xsl:stylesheet>
