<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="inputParamVal" type="eu.cityopt.DTO.InputParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt show optimization results</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<!-- Optimization results -->
						<h2><spring:message code="optimization_results"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:100px">
							<col style="width:200px">
							<col style="width:100px">
							<col style="width:300px">
							<tr>
								<!-- Scenario name: -->
								<td><spring:message code="scenario_name"/>:</td>
								<td>${scenario.name}</td>
								<!-- Description: -->
								<td><spring:message code="description"/>:</td>
								<td>${scenario.description}</td>
							</tr>
							<tr>
								<!-- User: -->						
								<td><spring:message code="user"/>:</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:250px">
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
												<td><a href="<c:url value='showresults.html?selectedcompid=${component.componentid}&scenarioid=${scenario.scenid}'/>">
												<spring:message code="select"/></a></td>
											</c:if>
												<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:150px">
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
							<tr height="20"></tr>
							<tr>
								<!-- Metrics -->
								<td><b><spring:message code="metrics"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr>
											<!-- Metric -->
											<th><spring:message code="metric"/></th>
										</tr>
					
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td></td>
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
</body>
</html>