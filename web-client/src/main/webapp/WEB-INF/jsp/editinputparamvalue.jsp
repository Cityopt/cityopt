<%--@elvariable id="inputParamVal" type="eu.cityopt.DTO.InputParamValDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="edit_input_parameter_value"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<form:form method="post" action="editinputparamvalue.html?inputparamvalid=${inputParamVal.inputparamvalid}" modelAttribute="inputParamVal">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="edit_objective_function_small"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<!-- Name -->
						<spring:message code="name"/>*						
					</td>
					<td>
						${inputParamVal.inputparameter.name}
					</td>
				</tr>
				<tr>
					<td>
						<!-- Value -->
						<spring:message code="value"/>						
					</td>
					<td>
						<!-- Input field -->
						<c:set var="tooltip_change"><spring:message code="tooltip_changevalue_parameter"/></c:set>
						<form:input style="width:300px" type="text" path="value" title="${tooltip_change}"/>
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<c:set var="tooltip_update"><spring:message code="tooltip_update"/></c:set>
					<c:set var="tooltip_cancel"><spring:message code="tooltip_cancel"/></c:set>
					<td align="right">
						<button class="activebutton" style="width:100px" title="${tooltip_update}"  type="submit"><spring:message code="update"/></button>
						<a href="scenarioparameters.html"><button style="width:100px"  title="${tooltip_cancel}" type="button" value="Cancel"><spring:message code="cancel"/></button></a></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>