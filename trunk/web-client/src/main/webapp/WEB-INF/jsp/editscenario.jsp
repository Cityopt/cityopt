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
<title>CityOpt <spring:message code="edit_scenario"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<script>
    function openInfoWindow() {
    	   window.open("editscenario_info.html",'<spring:message code="edit_scenario_info"/>','width=600,height=600,scrollbars=yes');
    }
    function openSimulationInfoWindow() {
 	   window.open("simulationinfo.html",'<spring:message code="simulation_info"/>','width=1400,height=600,scrollbars=yes');
	}
</script>


</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 820px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="edit_scenario"/></td>
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
						<table>
							<tr>
								<td>
									<table width="700">
										<tr>
											<td>
												<table>
													<form:form method="post" action="editscenario.html?action=update" modelAttribute="scenario">
													<tr>
														<c:set var="tooltip_name"><spring:message code="tooltip_edit_scenario_name"/></c:set>
														<td class="infosmall"><spring:message code="scenario_name"/>*</td>
													</tr>
													<tr>
														<td><form:input type="text" path="name" title="${tooltip_name}" style="width:400px"/></td>
													</tr>
													<tr>
														<!-- Description -->						
														<td class="infosmall"><spring:message code="description"/></td>
													</tr>
													<tr>
														<c:set var="tooltip_description"><spring:message code="tooltip_edit_scenario_description"/></c:set>
														<td><form:textarea type="text" rows="3" title="${tooltip_description}" path="description" style="width:400px"/></td>
													</tr>
													</form:form>
												</table>
											</td>
											<td valign="top" align="right">	
												<table width="100%">
													<tr height="5"></tr>
													<tr>
														<td>
															<c:set var="tooltip_update_scenario"><spring:message code="tooltip_update_scenario"/></c:set>
															<input type="submit" value="<spring:message code="update_scenario"/>" title="${tooltip_update_scenario}" style="width:150px">
														</td>
													</tr>
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
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table>						
										<tr height="15"></tr>
										<tr>
											<!-- Scenario simulation status -->
											<td class="infosmall"><spring:message code="scenario_simulation_status"/></td>
											<td class="regular">${status} (<spring:message code="refresh_by_reloading_page"/>)</td>
										</tr>
										<tr>
											<td class="infosmall"><spring:message code="simulation_info"/></td>
											<td>
												<c:if test="${scenario != null && scenario.getLog() != null}">
													<button type="button" style="width: 150px" onclick="openSimulationInfoWindow()"><spring:message code="show_simulation_info"/></button>
												</c:if>		
											</td>
										</tr>
										<tr height="15"></tr>
										<form:form method="post" action="setsimulationdate.html">
										<tr>
											<!--Dynamic simulation period from-->
											<c:set var="tooltip_simulation_start"><spring:message code="tooltip_dynamicsimulation_start"/></c:set>
											<c:set var="tooltip_simulation_end"><spring:message code="tooltip_dynamicsimulation_end"/></c:set>
											<td class="infosmall"><spring:message code="simulation_period_from"/></td>
											<td class="infosmall"><input name="simstart" title="${tooltip_simulation_start}" type="text" style="width:170px" value="${simStart}"/>
												<spring:message code="to"/>
												<input name="simend" title="${tooltip_simulation_end}" type="text" style="width:170px" value="${simEnd}" />
											</td>
										</tr>
										<tr>
											<td></td>
											<!-- Save dates -->
											<c:set var="tooltip_savedates"><spring:message code="tooltip_savedates"/></c:set>
											<td><input type="submit" title="${tooltip_savedates}" value="<spring:message code="save_dates"/>" style="width:150px"/></td>
										</tr>
										</form:form>
										<tr height="15"></tr>
										<tr>
											<!-- Input parameter & set button -->						
											<td  class="infosmall"><spring:message code="input_parameters"/></td>
											<c:set var="tooltip_scenario_parameters"><spring:message code="tooltip_edit_scenario_parameters"/></c:set>
											<td><a href="scenarioparameters.html"><button type="button" title="${tooltip_scenario_parameters}" style="width:100px">
											<spring:message code="set"/></button></a></td>
										</tr>
										<tr>
											<!-- External parameters & set button -->						
											<td class="infosmall"><spring:message code="external_parameters"/></td>
											<c:set var="tooltip_scenario_externalparameters"><spring:message code="tooltip_edit_scenario_externalparameters"/></c:set>
											<td><a href="scenariovariables.html"><button type="button" title="${tooltip_scenario_externalparameters}" style="width:100px">
											<spring:message code="show"/></button></a></td>
										</tr>
										
										<tr height="10"></tr>
										<tr>
											<!-- Components -->
											<td><b><spring:message code="components"/></b></td>
											<!-- Input parameter values -->
											<td><b><spring:message code="input_parameter_values"/></b></td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<tr>
														<th><spring:message code="component"/></th>
													</tr>
			
													<c:forEach items="${components}" var="component">
													<tr>
														<td>${component.name}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:200px">
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