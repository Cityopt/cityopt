<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.DTO.InputParamValDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="componentInputParamVal" type="com.cityopt.DTO.ComponentInputParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt set multi-scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td valign="top">
			<div style="overflow:scroll;height:600px;width:800px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td></td>
					<td height="80">
						<h2><spring:message code="set_multi_scenario"/></h2>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<table>
							<col style="width:500px">
							<tr>
								<td>
									<!-- Multi-scenarios -->
									<b><spring:message code="multi_scenarios"/></b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<col style="width:250px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Name -->
											<th><spring:message code="name"/></th>
										</tr>
										
										<c:forEach items="${components}" var="component">
										<c:if test="${selectedComponent.componentid == component.componentid}">
											<tr style="background-color: #D4D4D4"><td>
											<spring:message code="selected"/></td>
										</c:if>
										<c:if test="${selectedComponent.componentid != component.componentid}">
											<tr>
											<td><a href="<c:url value='scenarioparameters.html?selectedcompid=${component.componentid}'/>">
											<spring:message code="select"/></a></td>
										</c:if>
											<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<a href="createmultiscenario.html"><button style="width:100px" type="button" value="Create">
									<spring:message code="create"/></button></a>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Multi variables -->
									<b><spring:message code="multi_variables"/></b>									
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<col style="width:250px">
										<tr>
											<!--Select-->
											<th><spring:message code="select"/></th>
											<!-- Name -->
											<th><spring:message code="name"/></th>
										</tr>
										
										<c:forEach items="${components}" var="component">
										<c:if test="${selectedComponent.componentid == component.componentid}">
											<tr style="background-color: #D4D4D4"><td>
											<spring:message code="selected"/></td>
										</c:if>
										<c:if test="${selectedComponent.componentid != component.componentid}">
											<tr>
											<td><a href="<c:url value='scenarioparameters.html?selectedcompid=${component.componentid}'/>">
											<spring:message code="select"/></a></td>
										</c:if>
											<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
							    <td>
									<a href="createmultivariable.html"><button style="width:100px" type="button" value="Create">
									<spring:message code="create"/></button></a>
								</td>
								<!-- Close -button -->
								<td align="right">
									<a href="editscenario.html"><button type="button">
									<spring:message code="close"/></button></a>
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
</body>
</html>