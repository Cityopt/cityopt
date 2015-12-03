<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="selectedOutputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="listOutputVarVal" type="Double"--%>
<%--@elvariable id="listOutputVarTime" type="String"--%>
<%--@elvariable id="value" type="Double"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt time series table</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:600px;width:1130px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:1100px">	
				<tr>
					<td colspan="2" height="80">
						<!-- View table -->
						<h2>Time series table</h2>
					</td>
				</tr>
				<tr>
					<!-- Scenario (simulation name) simulation status: (Status)  -->
					<td><i><spring:message code="scenario"/> ${scenario.name} <spring:message code="simulation_status"/>: ${status}</i></td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<table width="1100px">
										<col style="width:350px">
										<col style="width:25px">
										<col style="width:350px">
										<col style="width:25px">
										<col style="width:350px">
										<tr>
											<td>
												<b><spring:message code="components"/></b>
											</td>
											<td></td>
											<td>
												<b><spring:message code="output_parameters"/></b>
											</td>
											<td></td>
											<td>Metric values</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:100px">
													<col style="width:200px">
													<col style="width:50px">
													<tr>
														<!-- Select -->
														<th><spring:message code="select"/></th>
														<!-- Component -->
														<th><spring:message code="component"/></th>
														<!-- Id -->
														<th><spring:message code="id"/></th>
													</tr>
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: rgb(140, 200, 200)">
														<td><a href="viewtable.html?selectedcompid=${component.componentid}">Selected</a></td>
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
														<td><a href="viewtable.html?selectedcompid=${component.componentid}">Select</a></td>
													</c:if>
														<td>${component.name}</td>
												    	<td>${component.componentid}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:100px">
													<col style="width:150px">
													<tr>
														<!-- Draw -->
														<th><spring:message code="draw"/></th>
														<!-- Output variable -->
														<th><spring:message code="output_variable"/></th>
													</tr>
													<c:forEach items="${outputVars}" var="outputVar">
														<c:choose>
															<c:when test="${outputVar.outvarid == selectedOutputVar.outvarid}">
																<tr style="background-color: rgb(140, 200, 200)">
																<td>Shown (<a href="writetable.html?action=remove&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Remove</a>)</td>
															</c:when>
															<c:otherwise>
																<tr>
																<td><a href="writetable.html?action=add&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Show</a></td>
															</c:otherwise>
														</c:choose>
													
														<td>${outputVar.name}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle" width="250px">
													<col style="width: 150px">
													<col style="width: 100px">
						
													<tr height="20">
													<!-- Name -->
													    <th><spring:message code="name"/></th>
													<!-- Value -->
													     <th>Value</th>
													</tr>
													
													<c:forEach items="${metricVals}" var="metricVal">
													<tr>
														<td>${metricVal.metric.name}</td>
												    	<td>${metricVal.value}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
										<tr height="20"><td></td></tr>
										<tr>
											<td>
												<table>
													<tr>
														<!-- Selected variable: -->
														<td><spring:message code="selected_variable"/>: ${selectedOutputVar.name}</td>
													</tr>
													<tr>
														<td>
															<table class="tablestyle" width="200">
																<col style="width:200px">
																		
																<tr height="20">
																	<!-- Time -->
																    <th><spring:message code="time"/></th>
																</tr>
																
																<c:forEach items="${listOutputVarTimes}" var="listOutputVarTime">
																<tr>
																	<td>${listOutputVarTime}</td>
															   	</tr>
																</c:forEach>
															</table>
														</td>
														<td>
															<table class="tablestyle" width="100">
																<col style="width:100px">
																		
																<tr height="20">
																	<!-- Value -->
																    <th><spring:message code="value"/></th>
																</tr>
																
																<c:forEach items="${listOutputVarVals}" var="listOutputVarVal">
																<tr>
																	<td>${listOutputVarVal}</td>
															   	</tr>
																</c:forEach>
															</table>
														</td>
													</tr>
												</table>
											</td>
											<td></td>
											<td>
											</td>
											<td></td>
											<td>
												<table width="100%">
													<tr>
														<td></td>
														<td align="right">
															<!-- <a href="writetable.html"><button type="button">View table</button></a>-->
														</td>
													</tr>
												</table>
											</td>
										</tr>										
									</table>
								</td>
							</tr>
							<tr height="50">
								<td>
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