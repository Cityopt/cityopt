<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create scenario</title>
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
						<h2>Results</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Scenario name:</td>
								<td>${scenario.name}</td>
								<td>Description:</td>
								<td>${scenario.description}</td>
							</tr>
							<tr>						
								<td>User:</td>
								<td>${user.name}</td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<c:forEach items="${components}" var="component">
										<tr>
											<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<th>Input parameter</th>
										</tr>
					
										<c:forEach items="${inputParams}" var="inputParam">
										<tr>
											<td>${inputParam.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<th>Output variable</th>
										</tr>
					
										<c:forEach items="${outputVars}" var="outputVar">
										<tr>
											<td>${outputVar.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
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
								<td align="right"><a href="editoptimizationset.html"><button type="button">Back</button></a></td>
								<td></td>
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