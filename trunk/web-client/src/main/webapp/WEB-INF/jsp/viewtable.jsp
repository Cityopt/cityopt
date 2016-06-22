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
<title>CityOpt <spring:message code="time_series_table"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("table_info.html",'<spring:message code="time_series_table" /> info','width=600,height=600');
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
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="time_series_table"/>
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
						<table style="width:950px">	
							<col style="width:130px">
							<col style="width:820px">
							<tr>
								<td><spring:message code="active_scenario"/></td>
								<td>
									<table class="tablestyle" width="200">
										<tr>
											<td>
												${scenario.name}
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="spacerowbig"></tr>
							<tr>
								<td colspan="2">
									<table>
										<tr>
											<td>
												<table width="950px">
													<col style="width:725px">
													<col style="width:25px">
													<col style="width:200px">
													<tr>
														<td>
															<table class="tablegroup">
																<tr>
																	<td>
																		<b><spring:message code="components"/></b>
																	</td>
																	<td></td>
																	<td>
																		<b><spring:message code="output_parameters"/></b>
																	</td>
																</tr>
																<tr>						
																	<td valign="top">
																		<div style="overflow:scroll;height:350px;width:320px;overflow:auto">
																		<table class="tablestyle">
																			<col style="width:80px">
																			<col style="width:220px">
																			<tr>
																				<th><spring:message code="select"/></th>
																				<th><spring:message code="component"/></th>
																			</tr>
																			<c:forEach items="${components}" var="component">
																			<c:if test="${selectedcompid == component.componentid}">
																				<tr style="background-color: #D4D4D4">
																					<td><a href="viewtable.html?selectedcompid=${component.componentid}"><b><spring:message code="selected"/></b></a></td>
																					<td><b>${component.name}</b></td>
																			</c:if>
																			<c:if test="${selectedcompid != component.componentid}">
																				<tr>
																					<td><a href="viewtable.html?selectedcompid=${component.componentid}">
																						<button type="button"><spring:message code="select"/></button></a></td>
																					<td>${component.name}</td>
																			</c:if>
																		   	</tr>
																			</c:forEach>
																		</table>
																		</div>
																	</td>
																	<td></td>
																	<td valign="top">
																		<table class="tablestyle">
																			<col style="width:130px">
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
																							<td><b><spring:message code="shown"/></b> <a href="writetable.html?action=remove&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">
																								<button type="button"><spring:message code="remove"/></button></a>
																							</td>
																							<td><b>${outputVar.name}</b></td>
																					</c:when>
																					<c:otherwise>
																						<tr>
																							<td><a href="writetable.html?action=add&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">
																								<button type="button"><spring:message code="show"/></button></a></td>
																							<td>${outputVar.name}</td>
																					</c:otherwise>
																				</c:choose>
																		   	</tr>
																			</c:forEach>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
														<td></td>
														<td valign="top"><b><spring:message code="metric_values"/></b>
															<table class="tablestyle" width="250px">
																<col style="width: 150px">
																<col style="width: 100px">
									
																<tr height="20">
																<!-- Name -->
																    <th><spring:message code="name"/></th>
																<!-- Value -->
																     <th><spring:message code="value"/></th>
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
																	<td>
																		<table>
																			<col style="width: 200px">
																			<col style="width: 250px">
																			<tr>
																				<!-- Selected variable: -->
																				<td colspan="2">
																					<table width="500px">
																						<col style="width: 150px">
																						<col style="width: 350px">
																						<tr>
																							<td>
																								<spring:message code="selected_variable"/>: &nbsp;
																							</td>
																							<td align="right">
																								<table class="tablestyle" width="350px" height="20px">
																									<tr>
																										<td>
																											${selectedOutputVar.getQualifiedName()}
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
																	<td>
																		<div style="overflow:scroll;height:350px;width:520px;overflow:auto">
																		<table width="500px">
																			<col style="width: 250px">
																			<col style="width: 250px">
																			<tr>
																			<tr>
																				<td>
																					<table class="tablestyle">
																						<col style="width:250px">
																								
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
																					</div>
																				</td>
																				<td align="left">
																					<table class="tablestyle" width="250px">
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
																		</div>
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
					</td>
				</tr>
			</table>	
			</div>
		</td>
	</tr>
</table>
</body>
</html>