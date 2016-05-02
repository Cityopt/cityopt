<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.SearchConstraintDTO"--%>
<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="outputVar" type="eu.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="create_constraint"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createconstraint.html?action=create" modelAttribute="constraint">
<table cellspacing="0" cellpadding="0">
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
								<td><spring:message code="create_constraint"/></td>
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
							<tr>
								<td>
									<table>						
										<col style="width:600px">
										<col style="width:240px">
										<tr>
											<td class="infosmall"><spring:message code="name"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="name"/></td>
											<td></td>
										</tr>
										<tr height=10></tr>
										<tr>
											<!-- Lower bound -->
											<td class="infosmall"><spring:message code="lower_bound"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
											<td></td>
										</tr>
										<tr height=10></tr>
										<tr>
											<!-- Expression -->
											<td class="infosmall"><spring:message code="expression"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="expression"/></td>
											<td></td>
										</tr>
										<tr height=10></tr>
										<tr>
											<td class="infosmall"><spring:message code="upper_bound"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="upperbound"/></td>
											<td></td>
										</tr>
										<tr>
											<td></td>
											<!-- Ok and Cansel submit -buttons -->
											<td align=left><input type="submit" value="<spring:message code="ok"/>"/>
											<a href="editoptimizationset.html"><button type="button">
											<spring:message code="cancel"/></button></a></td>
										</tr>					
									</table>
								</td>
							</tr>
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