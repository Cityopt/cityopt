<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create scenario</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createscenario.html?action=create" modelAttribute="scenario">
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
						<h2>Create scenario</h2>
																								
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Scenario name:</td>
								<td><form:input type="text" path="name"  style="width: 200px"/></td>
								<td></td>
								<td></td>
							</tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<element><h2 class="error"></element>${errorMessage}</h2>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width: 200px"></form:textarea></td>
								<td></td>
								<td></td>
							</tr>
							<tr height="10"></tr>
							<tr>						
								<td></td>
								<td align="right"><input type="submit" value="Create scenario" style="width:120px"></td>
								<td></td>
								<td></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>