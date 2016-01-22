<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="charttype" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt time series chart</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="20"></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1130px;overflow:auto">
			<table width="1100px">
				<col style="width:300px">
				<col style="width:30px">
				<col style="width:800px">
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<h2><spring:message code="time_series_chart"/></h2>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Scenario (simulation name) simulation status: (Status)  -->
									<spring:message code="active_scenario"/>: 
								</td>
								<td>${scenario.name}</td>
							</tr>
							<tr>
								<td>
									<spring:message code="simulation_status"/>: 
								</td>
								<td>
									${status}
								</td>
							</tr>
						</table>
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td><p><spring:message code="time_series_chart_instructions"/></p>
								</td>
								
							</tr>
							<c:choose>
								<c:when test="${error != null && !error.isEmpty()}">
									<tr>
										<td><i>Error in simulation: ${error}</i></td>
									</tr>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>
				
							<tr height="10">
							</tr>
							
							<tr>
								<td>
									<table class="tablegroup">
								
										<tr>
											<td>
												<!-- Components -->
												<b><spring:message code="components"/></b>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table class="tablestyle" style="width:300px">
													<col style="width:60px">
													<col style="width:240px">
													<tr>
														<!-- Select --><th><spring:message code="select"/></th>
														<!-- Component--><th><spring:message code="component"/></th>
													</tr>
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: #D4D4D4">
														<td><a href="timeserieschart.html?selectedcompid=${component.componentid}">Selected</a></td>
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
														<td><a href="timeserieschart.html?selectedcompid=${component.componentid}">Select</a></td>
													</c:if>
														<td>${component.name}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>		
										<tr height="10">
										</tr>
										<tr>
											<td>
												<b><spring:message code="output_parameters"/></b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle" style="width:300px">
													<col style="width:100px">
													<col style="width:200px">
													<tr>
														<th><spring:message code="draw"/></th>
														<th><spring:message code="output_variable"/></th>
													</tr>
													<c:forEach items="${outputVars}" var="outputVar">
														<c:choose>
															<c:when test="${usersession.hasOutputVar(outputVar.outvarid)}">
																<tr style="background-color: #D4D4D4">
																<td>Added (<a href="timeserieschart.html?action=remove&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Remove</a>)</td>
															</c:when>
															<c:otherwise>
																<tr>
																<td><a href="timeserieschart.html?action=add&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Add to chart</a></td>
															</c:otherwise>
														</c:choose>
													
														<td>${outputVar.name}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr height="20">
								<td>
									<!-- External parameters -->
									<b><spring:message code="external_parameters"/></b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle" style="width:300px">
										<col style="width:100px">
										<col style="width:200px">
												
										<tr height="20">
										    <th><spring:message code="draw"/></th>
										    <th><spring:message code="name"/></th>
										</tr>
										
										<c:forEach items="${extParamVals}" var="extParamVal">
										<tr>
											<c:choose>
												<c:when test="${usersession.hasExtParam(extParamVal.extparamvalid)}">
													<tr style="background-color: #D4D4D4">
													<td>Added (<a href="timeserieschart.html?action=remove&extparamid=${extParamVal.extparamvalid}">Remove</a>)</td>
												</c:when>
												<c:otherwise>
													<tr>
													<td><a href="timeserieschart.html?action=add&extparamid=${extParamVal.extparamvalid}">Add to chart</a></td>
												</c:otherwise>
											</c:choose>
											
											<td>${extParamVal.extparam.name}</td>
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
								<td valign="top" style="width: 750px; height: 400px; border-style: solid; border="1">
									<img src="timeserieschart.png">
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td align="right">
												<table>
													<tr>
														<td>
															<!-- Select chart type -->	
											 				<b><spring:message code="select_chart_type"/></b>
										 				</td>
									 				</tr>
									 				<tr>	
														<td>
									 					<c:choose>
															<c:when test="${charttype == 0}">
																<!-- Time series -->
										 						<b><a href="timeserieschart.html?charttype=0">
										 						<spring:message code="time_series"/></a></b>
															</c:when>
															<c:otherwise>
																<a href="timeserieschart.html?charttype=0">
																<spring:message code="time_series"/></a>
															</c:otherwise>
														</c:choose>
								 						</td>
							 						</tr>
							 						<tr>
							 							<td>
								 							<c:choose>
																<c:when test="${charttype == 1}">
																	<b><a href="timeserieschart.html?charttype=1">
											 						<spring:message code="scatter_plot"/></a></b>
																</c:when>
																<c:otherwise>
																	<a href="timeserieschart.html?charttype=1">
																	<spring:message code="scatter_plot"/></a>
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
															<a href="timeserieschart.html?action=removeall"><button type="button" style="width: 150px">
															<spring:message code="remove_selection"/></button></a>
														</td>
													</tr>
													<tr>
														<td>
															<!-- Refresh chart -->
															<a href="timeserieschart.html?action=refreshchart">
															<button type="button" style="width: 150px">
															<spring:message code="refresh_chart"/></button></a>
														</td>
													</tr>
													<tr>
														<td>
															<!--Open chart window -->
															<a href="timeserieschart.html?action=openwindow"><button type="button" style="width: 150px">
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
</body>
</html>