<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="charttype" type="int"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="time_series_chart"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("timeserieschart_info.html",'<spring:message code="time_series_chart" /> info','width=510,height=600');
    }
</script>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; width: 1200px; overflow: auto;">
			<table class="maintableextrawide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="time_series_chart_small"/>
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
							<col style="width:230px">
							<col style="width:800px">
							<tr>
								<td valign="top">
									<table>
										<tr>
											<td class="infosmall"><spring:message code="active_scenario"/></td>
										</tr>
										<tr>	
											<td class="activeline">${scenario.name}</td>
										</tr>
										<tr>
											<td class="infosmall"><spring:message code="simulation_status"/></td>
										</tr>
										<tr>	
											<td class="status">${status}</td>
										</tr>
										<tr height="10"></tr>
										<tr>
											<td valign="bottom">
												<table>
													<tr>
														<td class="infosmall"><spring:message code="time_series_chart_instructions"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>	
										<tr class="spacerowbig"><td></td></tr>
										<tr>
											<td>
												<b><spring:message code="selected_parameters"/></b>
											</td>
										</tr>
										<tr>
											<td valign="bottom">
												<table class="tablestyle" style="width: 100%">
													<tr>
														<th><spring:message code="parameter"/></th>
													</tr>
													<c:forEach items="${selectedParams}" var="selectedParam">
														<tr>
															<td>${selectedParam}</td>
														</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
									</table>
								</td>
								<td valign="top">
									<table>
										<tr>
											<td valign="top" style="width: 750px; height: 400px; border-style: solid; border="1">
												${usersession.getTimeSeriesImageMap()}
												<img src="assets/img/${usersession.getTimeSeriesFile()}" usemap="#chart">
											</td>
										</tr>
										<tr>
											<td>
												<table width="100%">
													<tr>
														<td valign="top" align="left" style="width: 180px">
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
																		<a href="drawtimeserieschart.html">
																			<button type="button" style="width: 150px">
																			<spring:message code="draw_chart"/></button>
																		</a>
																	</td>
																</tr>
																<tr>
																	<td>
																		<!-- Remove selections -->
																		<a href="timeserieschart.html?action=removeall"><button type="button" style="width: 150px">
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
						<table>	
							<tr>
								<td>
									<table>
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
															<div style="overflow:scroll;height:250px;width:415px;overflow:auto">
															<table class="tablestyle" style="width:400px">
																<col style="width:60px">
																<col style="width:340px">
																<tr>
																	<th><spring:message code="select"/></th>
																	<th><spring:message code="component"/></th>
																</tr>
																<c:forEach items="${components}" var="component">
																<c:if test="${selectedcompid == component.componentid}">
																	<tr style="background-color: #D4D4D4">
																		<td><a href="timeserieschart.html?selectedcompid=${component.componentid}"><b><spring:message code="selected"/></b></a></td>
																		<td><b>${component.name}</b></td>
																</c:if>
																<c:if test="${selectedcompid != component.componentid}">
																	<tr>
																		<td>
																			<a href="timeserieschart.html?selectedcompid=${component.componentid}">
																				<button type="button"><spring:message code="select"/></button>
																			</a>
																		</td>
																		<td>${component.name}</td>
																</c:if>
															   	</tr>
																</c:forEach>
															</table>
															</div>
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
															<div style="overflow:scroll;height:120px;width:400px;overflow:auto">
															<table class="tablestyle" style="width:400px">
																<col style="width:130px">
																<col style="width:270px">
																<tr>
																	<th><spring:message code="draw"/></th>
																	<th><spring:message code="output_variable"/></th>
																</tr>
																<c:forEach items="${outputVars}" var="outputVar">
																	<c:choose>
																		<c:when test="${usersession.hasOutputVar(outputVar.outvarid)}">
																			<tr style="background-color: #D4D4D4">
																				<td><b><spring:message code="added"/></b> <a href="timeserieschart.html?action=remove&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">
																					<button type="button"><spring:message code="remove"/></button>
																					</a>
																				</td>
																				<td><b>${outputVar.name}</b></td>
															   			</c:when>
																		<c:otherwise>
																			<tr>
																				<td><a href="timeserieschart.html?action=add&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}"><button type="button"><spring:message code="add_to_chart"/></button></a></td>
																				<td>${outputVar.name}</td>
															   			</c:otherwise>
																	</c:choose>
																
																</tr>
																</c:forEach>
															</table>
															</div>
														</td>
													</tr>
												</table>
											</td>
											<td width="20"></td>
											<td valign="top">
												<table>
													<tr>
														<td>
															<!-- External parameters -->
															<b><spring:message code="external_parameters"/></b>
														</td>
													</tr>
													<tr>
														<td valign="top">
															<div style="overflow:scroll;height:250px;width:420px;overflow:auto">
															<table class="tablestyle" style="width:400px">
																<col style="width:120px">
																<col style="width:280px">
																		
																<tr height="20">
																    <th><spring:message code="draw"/></th>
																    <th><spring:message code="name"/></th>
																</tr>
																
																<c:forEach items="${extParamVals}" var="extParamVal">
																<tr>
																	<c:choose>
																		<c:when test="${usersession.hasExtParam(extParamVal.extparamvalid)}">
																			<tr style="background-color: #D4D4D4">
																				<td><b><spring:message code="added"/></b> <a href="timeserieschart.html?action=remove&extparamid=${extParamVal.extparamvalid}">
																					<button type="button"><spring:message code="remove"/></button></a>
																				</td>
																				<td><b>${extParamVal.extparam.name}</b></td>
																		</c:when>
																		<c:otherwise>
																			<tr>
																				<td><a href="timeserieschart.html?action=add&extparamid=${extParamVal.extparamvalid}">
																					<button type="button"><spring:message code="add_to_chart"/></button></a></td>
																				<td>${extParamVal.extparam.name}</td>
																		</c:otherwise>
																	</c:choose>
															   	</tr>
																</c:forEach>
															</table>
															</div>
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