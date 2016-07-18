<%--@elvariable id="inputParamForm" type="eu.cityopt.web.InputParamForm"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="create_input_parameter"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
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
								<td><spring:message code="create_input_parameter"/></td>
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
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
						<form:form method="post" action="createinputparameter.html?selectedcompid=${selectedcompid}" modelAttribute="inputParamForm">
						
						<table align="center">
							<col style="width:150px">
							<col style="width:80px">
							<col style="width:80px">
							<tr>
								<td>
									<!-- Name -->
									<spring:message code="name"/>*
								</td>
								<td>
									<form:input style="width:300px" type="text" path="name"/>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Default value -->
									<spring:message code="default_value"/>
								</td>
								<td>
									<form:input style="width:300px" type="text" path="value"/>
								</td>
							</tr>
							<tr>
								<td>					
									<spring:message code="unit"/>
								</td>
								<td>					
									<form:select path="unit">
										<option value="${inputParam.unit.name}" selected>${inputParam.unit.name}</option>
										<c:forEach items="${units}" var="unit">																																
											<option value="${unit.name}">${unit.name}</option>
										</c:forEach>
									</form:select>				
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td></td>
								<!-- Create & Cancel button -->
								<td align="right"><input style="width:100px" type="submit" value="<spring:message code="create"/>"/>
								<input style="width:100px" type="submit" name="cancel" value="<spring:message code="cancel"/>"></td>
							</tr>
						</table>
						</form:form>
						</div>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>