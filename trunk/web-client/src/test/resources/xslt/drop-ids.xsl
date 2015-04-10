<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes"/>
  <xsl:strip-space elements="*"/>

  <!-- Remove id columns to simplify comparisons via the ExpectedDataBase
       annotation of Spring Test DBUnit. -->
  <xsl:template match="@aparamsid"/>
  <xsl:template match="@aparamvalid"/>
  <xsl:template match="@algorithmid"/>
  <xsl:template match="@ascengenid"/>
  <xsl:template match="@userid"/>
  <xsl:template match="@componentid"/>
  <xsl:template match="@datarelid"/>
  <xsl:template match="@decisionvarid"/>
  <xsl:template match="@extparamid"/>
  <xsl:template match="@extparamvalid"/>
  <xsl:template match="@extparamvalsetid"/>
  <xsl:template match="@id"/>
  <xsl:template match="@inputid"/>
  <xsl:template match="@scendefinitionid"/>
  <xsl:template match="@metid"/>
  <xsl:template match="@metricvalid"/>
  <xsl:template match="@modelparamid"/>
  <xsl:template match="@obtfunctionid"/>
  <xsl:template match="@optfunctionid"/>
  <xsl:template match="@optconstid"/>
  <xsl:template match="@optid"/>
  <xsl:template match="@optsearchconstid"/>
  <xsl:template match="@optscenid"/>
  <xsl:template match="@outvarid"/>
  <xsl:template match="@prjid"/>
  <xsl:template match="@scenid"/>
  <xsl:template match="@scengenid"/>
  <xsl:template match="@scenmetricid"/>
  <xsl:template match="@sgobfunctionid"/>
  <xsl:template match="@sgoptconstraintid"/>
  <xsl:template match="@scid"/>
  <xsl:template match="@modelid"/>
  <xsl:template match="@simresid"/>
  <xsl:template match="@tseriesid"/>
  <xsl:template match="@tseriesvalid"/>
  <xsl:template match="@typeid"/>
  <xsl:template match="@unitid"/>
  <xsl:template match="@usergroupid"/>
  <xsl:template match="@usergroupprojectid"/>

  <!-- Tables with only id fields cannot be compared now, so drop them. -->
  <xsl:template match="extparamvalset"/>
  <xsl:template match="extparamvalsetcomp"/>
  <xsl:template match="scenariometrics"/>
  <xsl:template match="timeseries"/>
  <xsl:template match="simulationresult"/>
  <xsl:template match="extparamval"/>
  <xsl:template match="scengenoptconstraint"/>
  <xsl:template match="scengenobjectivefunction"/>

  <!-- Also remove generated attributes that vary on every run. -->
  <xsl:template match="scenario/@name"/>
  <xsl:template match="scenario/@createdon"/>
  <xsl:template match="scenario/@updatedon"/>
  <xsl:template match="scenario/@runstart"/>
  <xsl:template match="scenario/@runend"/>
  <xsl:template match="scenario/@log"/>
  <xsl:template match="scenariogenerator/@log"/>
  <xsl:template match="inputparamval/@createdon"/>

  <!-- inputparamval elements are ordered by inputid then value. -->
  <xsl:template match="inputparamval"/>
  <xsl:template match="inputparamval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- modelparameter elements are ordered by value. -->
  <xsl:template match="modelparameter"/>
  <xsl:template match="modelparameter" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- metricval elements are ordered by value. -->
  <xsl:template match="metricval"/>
  <xsl:template match="metricval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- timeseriesval elements are ordered by time then value. -->
  <xsl:template match="timeseriesval"/>
  <xsl:template match="timeseriesval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="dataset">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:apply-templates select="inputparamval" mode="sort">
        <xsl:sort select="concat(@inputid,@value)"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="modelparameter" mode="sort">
        <xsl:sort select="@value"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="metricval" mode="sort">
        <xsl:sort select="@value"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="timeseriesval" mode="sort">
        <xsl:sort select="concat(@time,@value)"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
