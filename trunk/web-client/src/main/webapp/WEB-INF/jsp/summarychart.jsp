<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="summary_chart" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("summarychart_info.html",'<spring:message code="summary_chart" /> info','width=600,height=600');
    }
</script>

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
                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="summary_chart"/>
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
						<table>
							<tr>
								<td valign="top">
									<table width="1000px">
										<col style="width:250px">
										<col style="width:30px">
										<col style="width:750px">
										<tr>
											<td valign="top">
												<table>
													<tr>
														<td class="infosmall">
															<spring:message code="summary_chart_instructions_1"/>
															<br><br>
															<spring:message code="summary_chart_instructions_2"/>
														</td>
													</tr>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table>
													<tr>
														<td valign="top" style="width: 750px; height: 400px; border-style: solid; border="1">
															${usersession.getSummaryImageMap()}
															<img src="assets/img/${usersession.getSummaryFile()}" usemap="#chart">
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
																					<a href="drawsummarychart.html">
																						<button type="button" style="width: 150px">
																						<spring:message code="draw_chart"/></button>
																					</a>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<!-- Remove selections -->
																					<a href="summarychart.html?action=removeall"><button type="button" style="width: 150px">
																					<spring:message code="remove_selection"/></button></a>
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
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td>
						<table>
							<tr>
								<td valign="top">
									<table>
										<tr>
											<td>
												<!-- Scenarios --><b><spring:message code="scenarios"/></b>
											</td>
										</tr>
										
										<tr>
											<td valign="top">
												<div style="overflow:scroll;height:250px;width:520px;overflow:auto">
												<table class="tablestyle" width="500">
													<col style="width:150px">
													<col style="width:300px">	
													<col style="width:50px">	
																																		
													<tr>
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
																<a href="summarychart.html?action=remove&scenarioid=${scenario.scenid}"><button type="button"><spring:message code="remove"/></button></a>
																</td>
															</c:when>
															<c:otherwise>
																<tr>
																<!-- Add -button -->
																<td>
																	<a href="summarychart.html?action=add&scenarioid=${scenario.scenid}">
																		<button type="button"><spring:message code="add"/></button>
																	</a>
																</td>
															</c:otherwise>
														</c:choose>
														<td>${scenario.name}</td>
														<td>${scenario.status}</td>
												   	</tr>
													</c:forEach>				
												</table>
												</div>
											</td>
										</tr>
									</table>
								</td>
								<td valign="top">	
									<table>
										<tr>
											<td>
												<!-- Project metrics -->
												<b><spring:message code="project_metrics"/></b>
											</td>
										</tr>
										<tr>
											<td>	
												<table class="tablestyle" style="width:430px" border="1">
												<col style="width:130px">
												<col style="width:300px">
													
												<tr>
													<th><spring:message code="draw"/></th>
												    <th><spring:message code="name"/></th>
												</tr>
												
												<c:forEach items="${metrics}" var="metric">
													<c:choose>
														<c:when test="${usersession.hasMetric(metric.metid)}">
															<tr style="background-color: #D4D4D4">
															<td><spring:message code="added" /> <a href="summarychart.html?action=remove&metricid=${metric.metid}"><button type="button"><spring:message code="remove" /></button></a></td>
														</c:when>
														<c:otherwise>
															<tr>
															<td><a href="summarychart.html?action=add&metricid=${metric.metid}"><button type="button"><spring:message code="add_to_chart" /></button></a></td>
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
							</tr>
						</table>
					</td>
				</tr>
				<tr style="height: 100%"><td></td></tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>