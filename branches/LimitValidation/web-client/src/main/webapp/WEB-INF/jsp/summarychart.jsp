<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="summary_chart" /></title>

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
			<table class="maintableextrawide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="summary_chart"/>
								</td>
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
							<table width="1100px">
								<col style="width:300px">
								<col style="width:30px">
								<col style="width:800px">
								<tr>
									<td valign="top">
										<table>
											<c:choose>
												<c:when test="${error != null && !error.isEmpty()}">
													<tr>
														<td><i><spring:message code="error_in_simulation" />: ${error}</i></td>
													</tr>
												</c:when>
												<c:otherwise>
												</c:otherwise>
											</c:choose>
											<tr>
												<td>
													<table width="100%">
														<col style="width:130px">
														<col style="width:170px">
														<tr>
															<td class="infosmall">
																<!-- Scenario (simulation name) simulation status: (Status)  -->
																<spring:message code="active_scenario"/>: 
															</td>
															<td>
																<table class="tablestyle" width="100%">
																	<tr>
																		<td>${scenario.name}</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="infosmall">
																<spring:message code="simulation_status"/>: 
															</td>
															<td>
																<table class="tablestyle" width="100%">
																	<tr>
																		<td>${status}</td>
																	</tr>
																</table>	
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="infosmall">
													<p><spring:message code="summary_chart_instructions_1"/></p>
													<p><spring:message code="summary_chart_instructions_2"/></p>
												</td>
											</tr>
								
											<tr>
												<td>
													<!-- Scenarios --><b><spring:message code="scenarios"/></b>
												</td>
											</tr>
											
											<tr>
												<td>
													<table class="tablestyle" width="300">
														<col style="width:50px">
														<col style="width:200px">	
														<col style="width:50px">	
																																			
														<tr height="20">
															<!-- Select --><th><spring:message code="select"/></th>
														    <!-- Name --> <th><spring:message code="name"/></th>
														    <th><spring:message code="simulation"/></th>
														</tr>
																		
														<c:forEach items="${scenarios}" var="scenario">
														<tr>
															<c:choose>
																<c:when test="${usersession.hasScenarioId(scenario.scenid)}">
																	<tr style="background-color: #D4D4D4">													
																	<td>
																	<!-- Remove button -->
																	<spring:message code="added"/> 
																	(<a href="summarychart.html?action=remove&scenarioid=${scenario.scenid}"><spring:message code="remove"/></a>)
																	</td>
																</c:when>
																<c:otherwise>
																	<tr>
																	<!-- Add -button -->
																	<td><a href="summarychart.html?action=add&scenarioid=${scenario.scenid}">
																	<spring:message code="add"/></a></td>
																</c:otherwise>
															</c:choose>
															<td>${scenario.name}</td>
															<td>${scenario.status}</td>
													   	</tr>
														</c:forEach>				
													</table>
												</td>
											</tr>
											<tr height="10">
											</tr>
											<tr>
												<td>
													<!-- Project metrics -->
													<b><spring:message code="project_metrics"/></b>
												</td>
											</tr>
											<tr>
												<td>	
													<table class="tablestyle" style="width:300px" border="1">
													<col style="width:100px">
													<col style="width:200px">
														
													<tr height="20">
														<th><spring:message code="draw"/></th>
													    <th><spring:message code="name"/></th>
													</tr>
													
													<c:forEach items="${metrics}" var="metric">
														<c:choose>
															<c:when test="${usersession.hasMetric(metric.metid)}">
																<tr style="background-color: #D4D4D4">
																<td><spring:message code="added" /> (<a href="summarychart.html?action=remove&metricid=${metric.metid}"><spring:message code="remove" /></a>)</td>
															</c:when>
															<c:otherwise>
																<tr>
																<td><a href="summarychart.html?action=add&metricid=${metric.metid}"><spring:message code="add_to_chart" /></a></td>
															</c:otherwise>
														</c:choose>
														<td>${metric.name}</td>
													   	</tr>
													</c:forEach>
													</table>
												</td>
											</tr>
										</table>
									</td>
									<td></td>
									<td valign="top">
										<table>
											<tr>
												<td valign="top" style="width: 750px; height: 400px; border-style: solid; border: 1">
													<img src="summarychart.png">
												</td>
											</tr>
											<tr>
												<td>
													<table width="100%">
														<col style="width:180px">	
														<tr>
															<td valign="top">
																<table>
																	<tr>
																		<td>
																			<!-- Select chart type -->	
															 				<b><spring:message code="select_chart_type"/></b>
														 				</td>
													 				</tr>
												 				</table>
											 				</td>
											 				<td valign="top">
											 					<table>
													 				<tr>
											 							<td>
											 							<c:choose>
																			<c:when test="${charttype == 1}">
																				<b><a href="summarychart.html?charttype=1">
														 						<spring:message code="scatter_plot"/></a></b>
																			</c:when>
																			<c:otherwise>
																				<a href="summarychart.html?charttype=1">
																				<spring:message code="scatter_plot"/></a>
																			</c:otherwise>
																		</c:choose>
															 			</td>
													 				</tr>
													 				<tr>
													 					<td>
													 					<c:choose>
																			<c:when test="${charttype == 2}">
														 						<b><a href="summarychart.html?charttype=2">
														 						<spring:message code="bar_chart"/></a></b>
																			</c:when>
																			<c:otherwise>
																				<a href="summarychart.html?charttype=2">
																				<spring:message code="bar_chart"/></a>
																			</c:otherwise>
																		</c:choose>
															 			</td>
														 			</tr>
														 			<tr>
																		<td>
																		<c:choose>
																			<c:when test="${charttype == 3}">
														 						<b><a href="summarychart.html?charttype=3">
														 						<spring:message code="pie_chart"/></a></b>
																			</c:when>
																			<c:otherwise>
																				<a href="summarychart.html?charttype=3">
																				<spring:message code="pie_chart"/></a>
																			</c:otherwise>
																		</c:choose>
															 			</td>
														 			</tr>
													 			</table>
															</td>
															<td align="right">
																<table>
																	<tr>
																		<td>
																			<!-- Remove selections -->
																			<a href="summarychart.html?action=removeall"><button type="button" style="width: 150px">
																			<spring:message code="remove_selection"/></button></a>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<!-- Refresh chart -->
																			<a href="summarychart.html?action=refreshchart">
																			<button type="button" style="width: 150px">
																			<spring:message code="refresh_chart"/></button></a>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<!--Open chart window -->
																			<a href="summarychart.html?action=openwindow"><button type="button" style="width: 150px">
																			<spring:message code="open_chart_window"/></button></a>
																		</td>
																	</tr>
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
					
			</div>
		</td>
	</tr>
</table>
</body>
</html>