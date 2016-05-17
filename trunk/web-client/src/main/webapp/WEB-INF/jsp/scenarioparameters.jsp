<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.DTO.InputParamValDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="componentInputParamVal" type="com.cityopt.DTO.ComponentInputParamDTO"--%>
<%--@elvariable id="disableEdit" type="boolean"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="input_parameters" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<script>
    function openInfoWindow() {
    	   window.open("scenarioparameters_info.html",'<spring:message code="scenario_parameters" /> info','width=600,height=600');
    }
</script>
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
								<td><spring:message code="input_parameters"/></td>
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
					<td class="successful">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<c:choose>
					    		<c:when test="${disableEdit}">
					    			<tr>
										<td class="regular"><spring:message code="scenario_simulated_info"/></td>
									</tr>
									<tr height="10"></tr>
					    		</c:when>
					    		<c:otherwise>
						    	</c:otherwise>
					    	</c:choose>
							
							<tr>
								<td>
									<form:form modelAttribute="scenarioParamForm" method="post" action="scenarioParam.html?selectedcompid=${selectedcompid}">
									<table>
										<col style="width:250px">
										<col style="width:50px">
										<col style="width:300px">	
										<tr>
											<td>
												<!-- Components -->
												<b><spring:message code="components"/></b>
											</td>
											<td></td>
											<td>
												<!-- Input parameter values -->
												<b><spring:message code="input_parameter_values"/></b>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:80px">
													<col style="width:150px">
													<tr>
														<!-- Select -->
														<th><spring:message code="select"/></th>
														<!-- Component -->
														<th><spring:message code="component"/></th>
													</tr>
													<!-- Tooltips -->
													<c:set var="tooltip_select"><spring:message code="tooltip_select"/></c:set>
													<c:set var="tooltip_selected"><spring:message code="tooltip_selected"/></c:set>
													
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedComponent.componentid == component.componentid}">
														<tr title="${tooltip_selected}" style="background-color: #D4D4D4">
															<td><b><spring:message code="selected"/></b></td>
															<td><b>${component.name}</b></td>
													</c:if>
													<c:if test="${selectedComponent.componentid != component.componentid}">
														<tr>
															<td><a href="<c:url value='scenarioparameters.html?selectedcompid=${component.componentid}'/>" title="${tooltip_select}">
																	<button type="button"><spring:message code="select"/></button>
																</a>
															</td>
															<td>${component.name}</td>
													</c:if>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:60px">
													<col style="width:60px">
													
													<tr>
														<!-- Input parameter -->
														<th><spring:message code="input_parameters"/></th>
														<!-- Value -->
														<th><spring:message code="value"/></th>
														<!-- Unit -->
														<th><spring:message code="unit"/></th>
													</tr>
													
													<!-- Tooltip -->
													<c:set var="tooltip_edit_inputparameter"><spring:message code="tooltip_edit_scenario_parameter"/></c:set>
													
													<!-- Edit input parameterForm -->
													<c:forEach items="${inputParamVals}" var="inputParamVal">
													<tr>
														<td>${inputParamVal.inputparameter.name}</td>
														<td>
															<c:choose>
													    		<c:when test="${disableEdit}">
													    			${inputParamVal.value}
													    		</c:when>
													    		<c:otherwise>
														    		<form:input type="text" 
														    			title="${tooltip_edit_inputparameter}" 
														    			value="${inputParamVal.value}" 
														    			path="valueByInputId[${inputParamVal.inputparamvalid}]"/>
														    	</c:otherwise>
													    	</c:choose>
												    	</td>
												    	<td>${inputParamVal.inputparameter.unit.name}</td>
												   	</tr>
													</c:forEach>
													<c:set var="tooltip_update"><spring:message code="tooltip_update"/></c:set>										
																							
												</table>
											</td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td align="right">
												<!-- Update -button -->
												<c:choose>
													<c:when test="${disableEdit}">
										    		</c:when>
										    		<c:otherwise>
														<input style="width:100px" title="${tooltip_update}"  type="submit" value="Update"/>
											    	</c:otherwise>
												</c:choose>
										    </td>
										</tr>
									</table>
									</form:form>
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