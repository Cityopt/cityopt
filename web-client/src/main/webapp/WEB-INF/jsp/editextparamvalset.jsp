<%--@elvariable id="epv" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="edit_external_parameter_set"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form modelAttribute="extparamvalsetform" method="post" action="editextparamvalset.html">
<form:input type="hidden" path="extParamValSet.extparamvalsetid"/>
<form:input type="hidden" path="extParamValSet.name"/>
<input type="hidden" name="context" value="${context}"/>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="edit_external_parameter_value_set_small"/></td>
								<td align="left" width="40">
									<div class="round-button">
										<div class="round-button-circle">
											<a href="" onclick="openInfoWindow()">?</a>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
              <tr>
	              <td>
	                <spring:message code="external_parameter_set"/>: ${extparamvalset.name}
	              </td>
              </tr>
              <tr height="10"></tr>
              <tr>
                <td>
	              <table class="tablestyle" style="width: 450px">
                      <col>
                      <col align="right">
                      <col align="right">
	                  <tr>
	                      <th><spring:message code="parameter"/></th>
                          <th><spring:message code="value"/></th>
	                      <th><spring:message code="comment"/></th>
	                  </tr>
	
	                  <c:forEach items="${extparamvals}" var="epv">
	                  <tr>
	                      <td>${epv.extparam.name}</td>
                          <td>
                             <form:input align="right" style="width:200px" type="text" path="valueByParamId[${epv.extparam.extparamid}]"/>
                          </td>
                          <td>
                            <form:input style="width:200px" type="text" path="commentByParamId[${epv.extparam.extparamid}]"/>
                          </td>
	                  </tr>
	                  </c:forEach>
	              </table>
                </td>
              </tr>
              <tr height="10"></tr>
              <tr>
                  <td align="right">
                    <button class="activebutton" type="submit"><spring:message code="ok"/></button>
                    <a href="${context}"><button type="button" style="width: 100px"><spring:message code="cancel"/></button></a>
                  </td>
              </tr>   
			</table>
		</div>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>