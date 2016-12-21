<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.SearchConstraintDTO"--%>
<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="outputVar" type="eu.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="edit_constraint_small"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editconstraint.html" modelAttribute="constraint">
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="edit_constraint_small"/></td>
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
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>						
							<col style="width:30px">
							<col style="width:150px">
							<col style="width:400px">
							<col style="width:240px">
							<tr>
								<td></td>
								<!-- Name -->
								<td><spring:message code="name"/>*</td>
								<td><form:input style="width:400px" type="text" path="name"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!--Lower bound-->
								<td><spring:message code="lower_bound"/>*</td>
								<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!--Expression-->
								<td><spring:message code="expression"/>*</td>
								<td><form:input style="width:400px" type="text" path="expression"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!--Upper bound-->
								<td><spring:message code="upper_bound"/>*</td>
								<td><form:input style="width:400px" type="text" path="upperbound"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<!-- Ok submit and Cancel -button-->
								<td align=right>
									<button class="activebutton" type="submit"><spring:message code="ok"/></button>
									<a href="editoptimizationset.html">
										<button type="button">
											<spring:message code="cancel"/>
										</button>
									</a>
								</td>
							</tr>	
							<form:hidden path="optconstid" />
						</table>
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