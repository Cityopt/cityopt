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

<script>
    function openInfoWindow() {
    	   window.open("createproject_info.html",'Info: Create Project','width=600,height=800');
    }
    function openSimulationInfoWindow() {
 	   window.open("simulationinfo.html",'Simulation info','width=600,height=600');
	}
</script>


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
			<div style="overflow:scroll;height:100%;width:1100px;overflow:auto">
			<table>
				<tr>
					<td>
					<!-- Edit Scenario -->
						<h2><spring:message code="edit_scenario"/></h2>
					</td>
					
				<td align="right"><div class="round-button">
						<div class="round-button-circle" onclick="openInfoWindow()">
							<a>?</a>		
						</div> 
					</div></td>
				</tr>
				
				<tr>
					<td>
						<table>
							<form:form method="post" action="editscenario.html?action=update" modelAttribute="scenario">
							<tr>
								<!-- Scenario name -->
								<c:set var="tooltip_name"><spring:message code="tooltip_edit_scenario_name"/></c:set>
								<td><spring:message code="scenario_name"/>:</td>
								<td><form:input type="text" path="name" title="${tooltip_name}" style="width:200px"/></td>
								
								<!-- Update Scenario -->
								<c:set var="tooltip_update_scenario"><spring:message code="tooltip_update_scenario"/></c:set>
								<td><input type="submit" value="<spring:message code="update_scenario"/>" title="${tooltip_update_scenario}" style="width:150px"></td>
							</tr>
							<tr>
								<!-- Description -->						
								<td><spring:message code="description"/>:</td>
								<c:set var="tooltip_description"><spring:message code="tooltip_edit_scenario_description"/></c:set>
								<td><form:textarea type="text" rows="3" title="${tooltip_description}" path="description" style="width:200px"/></td>
								
								<!-- Export simulation results file (CSV) -->
								<td valign="top">
									<table>
										<tr>
											<td>
												<a href="exportsimulationresults.html"><button style="width: 150px" type="button">
														<spring:message code="export_simulation_results"/>
													</button></a>
											</td>
										</tr>
										<tr>
											<td>
												<!-- Simulate scenario -->
												<c:set var="tooltip_simulate_scenario"><spring:message code="tooltip_simulate_scenario"/></c:set>
												<a href="runscenario.html"><button type="button" title="${tooltip_simulate_scenario}" style="width:150px">								
												<spring:message code="simulate_scenario"/></button></a>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							</form:form>							
							<tr height="15"></tr>
							<tr>
								<!-- Input parameter & set button -->						
								<td><spring:message code="input_parameters"/>:</td>
								<c:set var="tooltip_scenario_parameters"><spring:message code="tooltip_edit_scenario_parameters"/></c:set>
								<td><a href="scenarioparameters.html"><button type="button" title="${tooltip_scenario_parameters}" style="width:100px">
								<spring:message code="set"/></button></a></td>
							</tr>
							<tr>
								<!-- External parameters & set button -->						
								<td><spring:message code="external_parameters"/>:</td>
								<c:set var="tooltip_scenario_externalparameters"><spring:message code="tooltip_edit_scenario_externalparameters"/></c:set>
								<td><a href="scenariovariables.html"><button type="button" title="${tooltip_scenario_externalparameters}" style="width:100px">
								<spring:message code="set"/></button></a></td>
							</tr>
							<tr height="15"></tr>
							<tr>
								<!-- Scenario simulation status -->
								<td><spring:message code="scenario_simulation_status"/>:</td>
								<td>${status} (<spring:message code="refresh_by_reloading_page"/>)</td>
							</tr>
							<tr>
								<td><spring:message code="simulation_info"/>:</td>
								<td>
									<button type="button" style="width: 150px" onclick="openSimulationInfoWindow()"><spring:message code="show_simulation_info"/></button>		
								</td>
							</tr>
							<tr>
								<!-- Remaining simulation time -->
								<!-- ToD0 Must make any kind loading screen! -->
								<td><spring:message code="remaining_simulation_time"/>:</td>
								<td></td>
							</tr>
							<form:form method="post" action="setsimulationdate.html">
							<tr>
								<!--Dynamic simulation period from-->
								<c:set var="tooltip_simulation_start"><spring:message code="tooltip_dynamicsimulation_start"/></c:set>
								<c:set var="tooltip_simulation_end"><spring:message code="tooltip_dynamicsimulation_end"/></c:set>
								<td><spring:message code="dynamic_simulation_period_from"/></td>
								<td><input name="simstart" title="${tooltip_simulation_start}" type="text" style="width:180px" value="${simStart}"/>
								<spring:message code="to"/>
								<input name="simend" title="${tooltip_simulation_end}" type="text" style="width:180px" value="${simEnd}" /></td>
							</tr>
							<tr>
								<td></td>
								<!-- Save dates -->
								<c:set var="tooltip_savedates"><spring:message code="tooltip_savedates"/></c:set>
								<td><input type="submit" title="${tooltip_savedates}" value="<spring:message code="save_dates"/>" style="width:150px"/></td>
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
										<col style="width:50px">
										
										<!-- Parameter names & values -->
										<tr>			
											<th><spring:message code="parameter_name"/></th>
											<th><spring:message code="value"/></th>
											<th><spring:message code="unit"/></th>
										</tr>
										<c:forEach items="${inputParamVals}" var="inputParamVal">
										<tr>
											<td>${inputParamVal.inputparameter.name}</td>
									    	<td>${inputParamVal.value}</td>
									    	<td>${inputParamVal.inputparameter.unit.name}</td>
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