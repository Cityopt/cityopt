<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="inputParamVal" type="eu.cityopt.DTO.InputParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="optimization_results" /></title>
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
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="optimization_results_small"/></td>
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
									<table>
										<tr>
											<td valign="top" class="infosmall"><spring:message code="scenario_name"/></td>
										</tr>
										<tr>	
											<td valign="top">
												<table class="tablestyle" width="200">
													<tr height="30">
														<td>${scenario.name}</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>	
											<td class="infosmall"><spring:message code="description"/></td>
										</tr>
										<tr>
											<td>
												<table class="tablestyle" width="300">
													<tr height="80">
														<td>${scenario.description}</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr height="10">
											<td></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablegroup">
										<col style="width:250px">
										<col style="width:30px">
										<col style="width:330px">
										<col style="width:30px">
										<col style="width:250px">
			
										<tr>
											<!-- Components -->
											<td><b><spring:message code="components"/></b></td>
											<td></td>
											<!-- Input parameters -->
											<td><b><spring:message code="input_parameters"/></b></td>
											<td></td>
											<!-- Output variables -->
											<td><b><spring:message code="output_variables"/></b></td>
										</tr>
										<tr>
											<td valign="top">
												<div style="overflow:scroll;height:300px;width:250px;overflow:auto">
												<table class="tablestyle">
													<col style="width:60px">
													<col style="width:190px">
													<tr>
														<!--Select -->
														<th><spring:message code="select"/></th>
														<!--Component -->
														<th><spring:message code="component"/></th>
													</tr>
										
													<c:forEach items="${components}" var="component">
													<tr>
														<c:if test="${selectedcompid == component.componentid}">
															<tr style="background-color: #D4D4D4"><td><spring:message code="selected"/></td>
														</c:if>
														<c:if test="${selectedcompid != component.componentid}">
															<tr>
															<td>
																<a href="<c:url value='showresults.html?selectedcompid=${component.componentid}&scenarioid=${scenario.scenid}'/>">
																	<button type="button"><spring:message code="select"/></button>
																</a>
															</td>
														</c:if>
															<td>${component.name}</td>
												   	</tr>
													</c:forEach>
												</table>
												</div>
											</td>
											<td></td>
											<td valign="top">
												<div style="overflow:scroll;height:300px;width:330px;overflow:auto">
												<table class="tablestyle">
													<col style="width:230px">
													<col style="width:100px">
													<tr>
														<!--Input parameter-->
														<th><spring:message code="input_parameter"/></th>
														<!--Value-->
														<th><spring:message code="value"/></th>
													</tr>
								
													<c:forEach items="${inputParamVals}" var="inputParamVal">
													<tr>
														<td>${inputParamVal.inputparameter.name}</td>
														<td>${inputParamVal.value}</td>
													</tr>
													</c:forEach>
												</table>
												</div>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:250px">
													<tr>
														<!-- Output variable -->
														<th><spring:message code="output_variable"/></th>
													</tr>
								
													<c:forEach items="${outputVars}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
													</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table>
										<tr height="30">
										</tr>
										<tr>
											<!-- Metrics -->
											<td><b><spring:message code="metrics"/></b></td>
										</tr>
										<tr>
											<td>
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
										<tr height="10"></tr>
										<tr>						
											<!-- Back -button -->
											<td align="right"><a href="editoptimizationset.html"><button type="button">
											<spring:message code="back"/></button></a></td>
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