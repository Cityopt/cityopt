<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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

  <!-- Also remove generated attributes that vary on every run. -->
  <xsl:template match="scenario/@description"/>
  <xsl:template match="scenario/@createdon"/>
  <xsl:template match="scenario/@updatedon"/>
  <xsl:template match="scenario/@runstart"/>
  <xsl:template match="scenario/@runend"/>
  <xsl:template match="scenario/@log"/>
  <xsl:template match="scenariogenerator/@log"/>
  <xsl:template match="inputparamval/@createdon"/>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
