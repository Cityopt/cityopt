<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%--@elvariable id="scenario" type="eu.cityopt.model.Scenario"--%>
<%--@elvariable id="component" type="com.cityopt.model.Component"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.model.InputParamVal"--%>
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
	<col style="width:100px">
	<col style="width:30px">
	<col style="width:800px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:800px;overflow:auto">
			<form:form method="post" action="editscenario.html?action=update" modelAttribute="scenario">
			<table>
				<tr>
					<td>
						<h2>Edit scenario</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<td><form:input type="text" path="name"/></td>
								<td></td>
								<td><input type="submit" value="Update scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td><input type="submit" value="Run scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description"/></td>
								<td></td>
								<td><input type="submit" value="Abort run scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td><input type="submit" value="Clone scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td>Input parameters:</td>
								<td><a href="scenarioparameters.html"><button type="button" style="width:150px">Set</button></a></td>
							</tr>
							<tr>						
								<td>External parameters:</td>
								<td><a href="scenariovariables.html"><button type="button" style="width:150px">Set</button></a></td>
							</tr>
							<tr height="10"></tr>
							<tr>						
								<td valign="top">
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:50px">
										<tr>
											<th>Component</th>
											<th>Id</th>
										</tr>

										<c:forEach items="${project.components}" var="component">
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
										<c:forEach items="${scenario.inputparamvals}" var="inputParamVal">
										<tr>
											<td>${inputParamVal.inputparameter.name}</td>
									    	<td>${inputParamVal.value}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td>
									<b>Parameters selection</b><br>
									<input type="radio" >All parameters<br>	
									<input type="radio">Completed parameters<br>	
									<input type="radio">Empty parameters	
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</form:form>
			</div>
		</td>
	</tr>
</table>
</body>
</html>