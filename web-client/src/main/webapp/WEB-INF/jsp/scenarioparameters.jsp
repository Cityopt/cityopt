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
<title>CityOpt Scenario parameters</title>

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
						<!-- Scenario parameters -->
						<h2><spring:message code="scenario_parameters"/></h2>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:50px">
							<col style="width:300px">	
							<tr>
								<td>
									<!-- Components -->
									<b><spring:message code="components"/></b>
								</td>
								<td></td>
								<td>
									<!-- Input parameter values -->
									<b><spring:message code="input_parameter_values"/></b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:80px">
										<col style="width:150px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Component -->
											<th><spring:message code="component"/>Component</th>
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
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:150px">
										<col style="width:60px">
										<col style="width:60px">
										<tr>
											<!-- Input parameter -->
											<th><spring:message code="input_parameters"/></th>
											<!-- Value -->
											<th><spring:message code="value"/></th>
											<!-- Edit -->
											<th><spring:message code="edit"/></th>
										</tr>
										
										<c:forEach items="${inputParamVals}" var="inputParamVal">
										<tr>
											<td>${inputParamVal.inputparameter.name}</td>
									    	<td>${inputParamVal.value}</td>
											<td>
												<a href="<c:url value='editinputparamvalue.html?inputparamvalid=${inputParamVal.inputparamvalid}'/>">
													<button align="right" type="button" value="Edit">
													<spring:message code="edit"/></button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td align="right">
									<!-- Close -button -->
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