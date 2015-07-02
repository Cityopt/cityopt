<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.DTO.InputParamValDTO"--%>
<%--@elvariable id="simStart" type="java.lang.String"--%>
<%--@elvariable id="simEnd" type="java.lang.String"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit scenario</title>
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
			<h2 class="error">${errorMessage}</h2>
			<div style="overflow:scroll;height:800px;width:1100px;overflow:auto">
			<table>
				<tr>
					<td>
					<!-- Edit Scenario -->
						<h2><spring:message code="edit_scenario"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<form:form method="post" action="editscenario.html?action=update" modelAttribute="scenario">
							<tr>
								<!-- Scenario name -->
								<td><spring:message code="scenario_name"/>:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
								<!-- Update Scenario -->
								<td><input type="submit" value="<spring:message code="update_scenario"/>" style="width:150px"></td>
							</tr>
							<tr>
								<!-- Description -->						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width:200px"/></td>
								<!-- Simulate scenario -->
								<td valign="top"><a href="runscenario.html"><button type="button" style="width:150px">
								<spring:message code="simulate_scenario"/></button></a></td>
							</tr>
							<tr height="15"></tr>
							<tr>
								<!-- Input parameter & set button -->						
								<td><spring:message code="input_parameters"/>:</td>
								<td><a href="scenarioparameters.html"><button type="button" style="width:100px">
								<spring:message code="set"/></button></a></td>
							</tr>
							<tr>
								<!-- External parameters & set button -->						
								<td><spring:message code="external_parameters"/>:</td>
								<td><a href="scenariovariables.html"><button type="button" style="width:100px">
								<spring:message code="set"/></button></a></td>
							</tr>
							<tr>
								<!-- Multi scenarios -->					
								<td>Multi-scenarios:</td>
								<td><a href="setmultiscenario.html"><button type="button" style="width:100px">
								<spring:message code="set"/></button></a></td>
							</tr>
							<tr height="15"></tr>
							<tr>
								<!-- Scenario simulation status -->
								<td><spring:message code="scenario_simulation_status"/>:</td>
								<td>${scenario.status}</td>
							</tr>
							<tr>
								<!-- Remaining simulation time -->
								<td><spring:message code="remaining_simulation_time"/>:</td>
								<td></td>
							</tr>
							</form:form>
							
							<form:form method="post" action="setsimulationdate.html">
							<tr>
								<!--Dynamic simulation period from-->
								<td><spring:message code="dynamic_simulation_period_from"/></td>
								<td><input name="simstart" type="text" style="width:180px" value="${simStart}"/>
								<spring:message code="to"/>
								<input name="simend" type="text" style="width:180px" value="${simEnd}" /></td>
							</tr>
							<tr>
								<td></td>
								<!-- Save dates -->
								<td><input type="submit" value="<spring:message code="save_dates"/>" style="width:100px"/></td>
							</tr>
							</form:form>
							
							<tr height="10"></tr>
							<tr>
								<!-- Components -->
								<td><b><spring:message code="components"/></b></td>
								<!-- Input parameter values -->
								<td><b><spring:message code="input_parameter_values"/></b></td>
							</tr>
							<tr>						
								<td valign="top">
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:50px">
										<tr>
											<!-- Component & Id -->
											<th><spring:message code="component"/></th>
											<th><spring:message code="id"/></th>
										</tr>

										<c:forEach items="${components}" var="component">
										<tr>
											<td>${component.name}</td>
									    	<td>${component.componentid}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td valign="top">
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:50px">
										
										<!-- Parameter names & values -->
										<tr>			
											<th><spring:message code="parameter_name"/></th>
											<th><spring:message code="value"/></th>
										</tr>
										<c:forEach items="${inputParamVals}" var="inputParamVal">
										<tr>
											<td>${inputParamVal.inputparameter.name}</td>
									    	<td>${inputParamVal.value}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table>
							<tr><td></td></tr>
							<tr><td></td></tr>
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