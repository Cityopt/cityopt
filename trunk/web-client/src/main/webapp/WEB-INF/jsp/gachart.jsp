<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="genetic_optimization_chart"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("gachart_info.html", '<spring:message code="genetic_optimization_chart" /> info', 'width=600, height=600');
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
                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="genetic_optimization_chart"/>
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
								<td>
									<table width="990px">
										<col style="width:220px">
										<col style="width:20px">
										<col style="width:750px">
										<tr>
											<td valign="top">
												<table>	
													<tr>
														<td class="infosmall"><spring:message code="genetic_optimization_chart_instructions"/></td>
													</tr>
													<tr class="spacerowbig"></tr>
													
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table>
													<tr>
														<td valign="top" style="width: 750px; height: 400px; border-style: solid">
															${usersession.getGAChartImageMap()}
															<img src="assets/img/${usersession.getGAChartFile()}" usemap="#chart">
														</td>
													</tr>
													<tr>
														<td>
															<table width="100%">
																<tr>
																	<td align="right">
																	</td>
																	<td align="right">
																		<table>
																			<tr>
																				<td>
																					<a href="drawgachart.html">
																						<button type="button" style="width: 200px">
																							<spring:message code="draw_chart"/></button>
																					</a>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<a href="gachart.html?action=selectall"><button type="button" style="width: 200px">
																					<spring:message code="select_all"/></button></a>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<a href="gachart.html?action=selectpareto"><button type="button" style="width: 200px">
																					<spring:message code="select_pareto_optimal"/></button></a>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<!-- Remove selections -->
																					<a href="gachart.html?action=removeall"><button type="button" style="width: 200px">
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
							<tr>
								<td>
									<table>
										<tr>
											<td>
												<table>
													<tr>
														<td>
															<!-- Scenarios --><b><spring:message code="scenarios"/></b>
														</td>
													</tr>
													<tr>
														<td>
															<div style="overflow:scroll;height:300px;width:380px;overflow:auto">
															<table class="tablestyle" width="360">
																<col style="width:120px">
																<col style="width:180px">	
																<col style="width:60px">	
																																					
																<tr height="20">
																	<!-- Select --><th><spring:message code="select"/></th>
																    <!-- Name --> <th><spring:message code="name"/></th>
																    <th>Pareto</th>
																</tr>
																				
																<c:forEach items="${scenarioInfos}" var="scenarioInfo">
																<tr>
																	<c:choose>
																		<c:when test="${usersession.hasSelectedGAScenarioId(scenarioInfo.id)}">
																			<tr style="background-color: #D4D4D4">													
																			<td>
																				<!-- Remove button -->
																				<spring:message code="added"/> 
																				<a href="gachart.html?action=remove&scenarioid=${scenarioInfo.id}">
																					<button type="button"><spring:message code="remove"/></button>
																				</a>
																			</td>
																		</c:when>
																		<c:otherwise>
																			<tr>
																				<!-- Add -button -->
																				<td>
																					<a href="gachart.html?action=add&scenarioid=${scenarioInfo.id}">
																						<button type="button"><spring:message code="add"/></button>
																					</a>
																				</td>
																		</c:otherwise>
																	</c:choose>
																	<td>${scenarioInfo.name}</td>
																	<c:choose>
																		<c:when test="${scenarioInfo.pareto}">
																			<td align="center">X</td>
																		</c:when>
																		<c:otherwise>
																			<td></td>
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
											<td valign="top">
												<table>
													<tr>
														<td>
															<!-- Objective functions -->
															<b><spring:message code="objective_functions"/></b>
														</td>
													</tr>
													<tr>
														<td valign="top">
															<table class="tablestyle" style="width:360px">
																<col style="width:120px">
																<col style="width:240px">
																<tr>
																	<!-- Select --><th><spring:message code="select"/></th>
																	<!-- Objective function --><th>Objective function</th>
																</tr>
																<c:forEach items="${objFuncs}" var="objFunc">
																	<c:choose>
																		<c:when test="${usersession.hasSelectedGAObjFuncId(objFunc.obtfunctionid)}">
																			<tr style="background-color: #D4D4D4">
																			<td><spring:message code="selected"/> <a href="gachart.html?action=remove&objfuncid=${objFunc.obtfunctionid}"><button type="button"><spring:message code="remove"/></button></a></td>
																		</c:when>
																		<c:otherwise>
																			<tr>
																				<td><a href="gachart.html?action=add&objfuncid=${objFunc.obtfunctionid}"><button type="button"><spring:message code="select"/></button></a></td>
																		</c:otherwise>
																	</c:choose>
																	
																	<td>${objFunc.name}</td>
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
					</td>
				</tr>	
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>