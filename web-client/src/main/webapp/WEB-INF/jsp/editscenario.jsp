<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.DTO.InputParamValDTO"--%>
<%--@elvariable id="simStart" type="java.lang.String"--%>
<%--@elvariable id="simEnd" type="java.lang.String"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit scenario</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td valign="top">
			<h2 class="error">${errorMessage}</h2>
			<div style="overflow:scroll;height:800px;width:1100px;overflow:auto">
			<table>
				<tr>
					<td>
						<h2>Edit scenario</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<form:form method="post" action="editscenario.html?action=update" modelAttribute="scenario">
							<tr>
								<td>Scenario name:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
								<td><input type="submit" value="Update scenario" style="width:150px"></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width:200px"/></td>
								<td valign="top"><a href="runscenario.html"><button type="button" style="width:150px">Run scenario</button></a></td>
							</tr>
							<tr>						
								<td>Input parameters:</td>
								<td><a href="scenarioparameters.html"><button type="button" style="width:100px">Set</button></a></td>
							</tr>
							<tr>						
								<td>External parameters:</td>
								<td><a href="scenariovariables.html"><button type="button" style="width:100px">Set</button></a></td>
							</tr>
							<tr>
								<td>Scenario simulation status:</td>
								<td>${scenario.status}</td>
							</tr>
							</form:form>
							
							<form:form method="post" action="setsimulationdate.html">
							<tr>
								<td>Dynamic simulation period from</td>
								<td><input name="simstart" type="text" style="width:180px" value="${simStart}"/>to
								<input name="simend" type="text" style="width:180px" value="${simEnd}" /></td>
							</tr>
							<tr>
								<td></td>
								<td><input type="submit" value="Save dates" style="width:100px"/></td>
							</tr>
							</form:form>
							
							<tr height="10"></tr>
							<tr>
								<td><b>Components</b></td>
								<td><b>Input parameter values</b></td>
							</tr>
							<tr>						
								<td valign="top">
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:50px">
										<tr>
											<th>Component</th>
											<th>Id</th>
										</tr>

										<c:forEach items="${components}" var="component">
										<tr>
											<td>${component.name}</td>
									    	<td>${component.componentid}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td valign="top">
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:50px">
										<tr>
											<th>Parameter name</th>
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
							</tr>
						</table>
					</td>
					<td valign="top">
						<table>
							<tr><td></td></tr>
							<tr><td></td></tr>
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