<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="inputParamVal" type="eu.cityopt.DTO.InputParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
						<h2>Optimization results</h2>
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
								<td>Scenario name:</td>
								<td>${scenario.name}</td>
								<td>Description:</td>
								<td>${scenario.description}</td>
							</tr>
							<tr>						
								<td>User:</td>
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
								<td><b>Components</b></td>
								<td></td>
								<td><b>Input parameters</b></td>
								<td></td>
								<td><b>Output variables</b></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<th>Select</th>
											<th>Component</th>
										</tr>
							
										<c:forEach items="${components}" var="component">
										<tr>
											<c:if test="${selectedcompid == component.componentid}">
												<tr style="background-color: #D4D4D4"><td>Selected</td>
											</c:if>
											<c:if test="${selectedcompid != component.componentid}">
												<tr>
												<td><a href="<c:url value='showresults.html?selectedcompid=${component.componentid}&scenarioid=${scenario.scenid}'/>">Select</a></td>
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
											<th>Input parameter</th>
											<th>Value</th>
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
									<!-- <table class="tablestyle">
										<col style="width:250px">
										<tr>
											<th>Output variable</th>
										</tr>
					
										<c:forEach items="${outputVars}" var="outputVar">
										<tr>
											<td>${outputVar.name}</td>
										</tr>
										</c:forEach>
									</table>-->
								</td>
							</tr>
							<tr height="20"></tr>
							<!-- <tr>
								<td><b>Metrics</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr>
											<th>Metric</th>
										</tr>
					
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
							</tr>-->
							<tr height="10"></tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td align="right"><a href="editoptimizationset.html"><button type="button">Back</button></a></td>
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