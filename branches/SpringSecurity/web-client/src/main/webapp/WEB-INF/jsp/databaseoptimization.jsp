<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt Database optimization</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form>
<table>
	<col style="width: 400px;">
	<col style="width: 450px;">
	
	<!-- Edit database optimization set title -->
	<tr><td colspan="2"><h2><spring:message code="edit_database_optimization_set"/></h2></td></tr>
	<tr>
		<td colspan="2">
			<table>
				<col style="width: 80px;">
				<col style="width: 200px;">
				<col style="width: 80px;">
				<col style="width: 300px;">
				<col style="width: 175px;">
				<tr>
					<!-- Name -->
					<td><spring:message code="name"/>:</td>
					<td><input type="text" id="name" style="width:200px"></td>
					<!-- Description -->
					<td><spring:message code="description"/>:</td>
					<td rowspan="2"><textarea id="description" rows="2" style="width: 300px"></textarea></td>
					<td align="right"><input type="submit" value="Abort search" style="width: 100px"></td>
				</tr>
				<tr>
					<!-- User -->						
					<td><spring:message code="user"/>:</td>
					<td><input type="text" id="user" style="width:200px"></td>
					<td></td>
					<td align="right"><input type="submit" value="Run search" style="width: 100px"></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>						
		<td>
			<table>
				<tr>
					<!-- Objective function -->
					<td><b><spring:message code="objective_function"/></b></td>
				</tr>
				<tr>
					<td><input type="text" id="function"></td>
				</tr>
				<tr>
					<!-- Optimization sense:  Maximize & Minimize-->
					<td><spring:message code="optimize_sence"/>:
					<input type="radio">
					<spring:message code="maximize"/>
					<input type="radio">
					<spring:message code="minimize"/></td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="Create">
						<input type="submit" value="Delete">
						<input type="submit" value="Import">
					</td>
				</tr>
				<tr height="20"></tr>
				<tr>
					<!-- Searching constraint -->
					<td><b><spring:message code="searching_constraint"/></b></td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" style="width: 390px">
							<tr>
								<!-- Name -->
								<th><spring:message code="name"/></th>
								<!-- Type -->
								<th>Type</th>
								<!-- Unit -->
								<th>Unit</th>
							</tr>
							<tr>
								<td>x</td>
								<td>x</td>
								<td>x</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="Add">
						<input type="submit" value="Delete">
						<input type="submit" value="Import">
					</td>
				</tr>
			</table>
		</td>
		<td>
			<table style="width: 440px">
				<col style="width: 120px;">
				<col style="width: 180px;">
				<col style="width: 140px;">
				<tr>
					<td><b>Results</b></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>Scenario name</td>
					<td>${resultScenario.name}</td>
					<td align="right"><input type="submit" value="Show scenario"></td>
				</tr>
				<tr>
					<td>Optimization info</td>
					<td>${usersession.getOptResultString()}</td>
					<td align="right"><input type="submit" value="Show scenario"></td>
				</tr>
				<tr>
					<td>Objective function value</td>
					<td><input type="text" style="width:190px"></td>
					<td></td>
				</tr>
				<tr><td><br></td></tr>
				<tr>
					<td colspan="3">
						<table class="tablestyle" width="100%">
							<col style="width: 70%">
							<col style="width: 30%">

							<tr height="20">
							<!-- Name -->
							    <th><spring:message code="name"/></th>
							<!-- Value -->
							     <th>Value</th>
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
				<tr><td><br></td></tr>
			</table>
			<table>
				<col style="width: 220px;">
				<col style="width: 220px;">
				<tr>
					<td><input type="submit" value="Clone database optimization set"></td>
					<td align="right"></td>
				</tr>
				<tr>
					<td></td>
					<td align="right"></td>
				</tr>
			</table>
		</td>
	</tr>

</table>
</form>
</body>
</html>