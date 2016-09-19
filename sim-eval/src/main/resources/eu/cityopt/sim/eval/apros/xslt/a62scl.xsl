<?xml version="1.0"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="text"/>
  
  <xsl:template match="/">
    <xsl:text>/* Start of XSLT generated part */

data Configuration = Node String String [Configuration]
                   | Set String a

applyConfiguration :: Configuration -> AprosSequence ()
applyConfiguration (Node _ _ children) = fork
    $ fmap ignore
    $ mapM applyConfiguration children
applyConfiguration (Set name value) = set name $ toDynamic value

ucData :: Configuration
ucData = let
    true = True
    false = False
  in
</xsl:text>
    <xsl:apply-templates select="node">
      <xsl:with-param name="indent" select="'  '"/>
    </xsl:apply-templates>
    <xsl:text>
setupUCs :: AprosSequence ()
setupUCs = applyConfiguration ucData

/* End of XSLT generated part. */
</xsl:text>
  </xsl:template>

  <xsl:template name="node">
    <xsl:text>Node "</xsl:text><xsl:value-of select="@name"
    />" "<xsl:value-of select="@moduleName"/><xsl:text>" [
</xsl:text>
  </xsl:template>

  <xsl:template match="node[@isUC = 'False']"  priority="1">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/>
    <xsl:call-template name="node"/>
    <xsl:apply-templates select="property" mode="set">
      <xsl:with-param name="indent" select="concat('  ', $indent)"/>
    </xsl:apply-templates>
    <xsl:value-of select="$indent"/>]<xsl:if
      test="position() != last()">,</xsl:if><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="node">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/>
    <xsl:if test="property">
      <xsl:text>let
</xsl:text>
      <xsl:apply-templates select="property" mode="let">
        <xsl:with-param name="indent" select="concat('  ', $indent)"/>
      </xsl:apply-templates>
      <xsl:value-of select="$indent"/><xsl:text>in </xsl:text>
    </xsl:if>
    <xsl:call-template name="node"/>
    <xsl:apply-templates select="node">
      <xsl:with-param name="indent" select="concat('  ', $indent)"/>
    </xsl:apply-templates>
    <xsl:value-of select="$indent"/>]<xsl:if
      test="position() != last()">,</xsl:if><xsl:text>
</xsl:text>
  </xsl:template>
  
  <xsl:template match="property" mode="set">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/>Set "<xsl:value-of
    select="../@moduleName"/>#<xsl:value-of select="@name"
    />" (<xsl:value-of select="@value"/>)<xsl:if
      test="position() != last()">,</xsl:if><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="property[@name != @value]" mode="let">
    <xsl:param name="indent"/>
    <xsl:value-of select="$indent"/><xsl:value-of select="@name"
    /> = (<xsl:value-of select="@value"/><xsl:text>)
</xsl:text>
  </xsl:template>
</xsl:stylesheet>
