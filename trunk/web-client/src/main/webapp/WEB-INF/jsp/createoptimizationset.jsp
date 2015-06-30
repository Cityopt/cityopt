<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt Database optimization</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellpadding="0" cellspacing="0">
<tr>
	<td>
		<%@ include file="mainmenu.inc"%>
	</td>
	<td width="20"></td>
	<td valign="top">
		<form:form method="post" action="createoptimizationset.html" modelAttribute="openoptimizationset">
		<table>
			<col style="width: 400px;">
			<col style="width: 450px;">
			<tr><td colspan="2"><h2>Create database optimization set</h2></td></tr>
			<tr>
				<td colspan="2">
					<table>
						<col style="width: 80px;">
						<col style="width: 200px;">
						<col style="width: 80px;">
						<col style="width: 300px;">
						<col style="width: 175px;">
						<tr>
							<td>Name:</td>
							<td><form:input type="text" id="name" path="name" style="width:200px"/></td>
							<td>Description:</td>
							<td rowspan="2"><textarea id="description" rows="2" style="width: 300px"></textarea></td>
							<td align="right"><input type="submit" value="Create" style="width: 100px"></td>
						</tr>
						<tr>						
							<td>User:</td>
							<td><input type="text" id="user" style="width:200px"></td>
							<td></td>
							<td align="right"></td>
						</tr>
						<tr>
							<td>Type:</td>
							<td>
								<select name="type" id="type" size="1">
									<option value="1" selected>Database search</option>
									<option value="2">Genetic algorithm</option>
								</select>
							</td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</form:form>
		</td>
	</tr>
</table>
</body>
</html>