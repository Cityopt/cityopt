<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes"/>
  <xsl:strip-space elements="*"/>

  <!-- Remove id columns to simplify comparisons via the ExpectedDataBase
       annotation of Spring Test DBUnit. -->
  <xsl:template match="@algorithmid"/>
  <xsl:template match="@aparamsid"/>
  <xsl:template match="@aparamvalid"/>
  <xsl:template match="@ascengenid"/>
  <xsl:template match="@componentid"/>
  <xsl:template match="@createdby"/>
  <xsl:template match="@datarelid"/>
  <xsl:template match="@decisionvarid"/>
  <xsl:template match="@decvarresultid"/>
  <xsl:template match="@extparamid"/>
  <xsl:template match="@extparamvalid"/>
  <xsl:template match="@extparamvalsetid"/>
  <xsl:template match="@id"/>
  <xsl:template match="@inputid"/>
  <xsl:template match="@metid"/>
  <xsl:template match="@metricvalid"/>
  <xsl:template match="@modelid"/>
  <xsl:template match="@modelparamid"/>
  <xsl:template match="@obtfunctionid"/>
  <xsl:template match="@objectivefunctionresultid"/>
  <xsl:template match="@optconstresultid"/>
  <xsl:template match="@optfunctionid"/>
  <xsl:template match="@optconstid"/>
  <xsl:template match="@optid"/>
  <xsl:template match="@optsearchconstid"/>
  <xsl:template match="@optscenid"/>
  <xsl:template match="@outvarid"/>
  <xsl:template match="@prjid"/>
  <xsl:template match="@scendefinitionid"/>
  <xsl:template match="@scenid"/>
  <xsl:template match="@scengenid"/>
  <xsl:template match="@scengenresultid"/>
  <xsl:template match="@scenmetricid"/>
  <xsl:template match="@scid"/>
  <xsl:template match="@sgobfunctionid"/>
  <xsl:template match="@sgoptconstraintid"/>
  <xsl:template match="@simresid"/>
  <xsl:template match="@tseriesid"/>
  <xsl:template match="@tseriesvalid"/>
  <xsl:template match="@typeid"/>
  <xsl:template match="@unitid"/>
  <xsl:template match="@userid"/>
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
  <xsl:template match="simulationmodel/@createdon"/>
  <xsl:template match="simulationmodel/@modelblob"/>

  <!-- Elements whose insertion order varies are removed,
       and then output separately in sorted order. -->
  <xsl:template match="inputparamval"/>
  <xsl:template match="inputparamval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="modelparameter"/>
  <xsl:template match="modelparameter" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="metricval"/>
  <xsl:template match="metricval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="timeseriesval"/>
  <xsl:template match="timeseriesval" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="scengenresult"/>
  <xsl:template match="scengenresult" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="decisionvariableresult"/>
  <xsl:template match="decisionvariableresult" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="objectivefunctionresult"/>
  <xsl:template match="objectivefunctionresult" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="optconstraintresult"/>
  <xsl:template match="optconstraintresult" mode="sort">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- The contents of the top-level dataset element are processed
       first normally, and then selected elements are output
       in sorted order, with the mode set as "sort". -->
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
      <xsl:apply-templates select="scengenresult" mode="sort">
        <xsl:sort select="@feasible"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="decisionvariableresult" mode="sort">
        <xsl:sort select="@value"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="objectivefunctionresult" mode="sort">
        <xsl:sort select="@value"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="optconstraintresult" mode="sort">
        <xsl:sort select="@infeasibility"/>
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
