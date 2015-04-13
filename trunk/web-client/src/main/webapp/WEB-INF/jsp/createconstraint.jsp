<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="function" type="eu.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create optimization constraint</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createconstraint.html?action=create" modelAttribute="function">
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
						<h2>Create constraint</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:80px">
							<col style="width:250px">

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
							<tr>
								<td>Lower bound</td>
								<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
								<td><a href=""><button type="button">F(x)</button></a></td>
							</tr>
							<tr>
								<td>Expression</td>
								<td><form:input style="width:400px" type="text" path="expression"/></td>
								<td><a href=""><button type="button">F(x)</button></a></td>
							</tr>
							<tr>
								<td>Upper bound</td>
								<td><form:input style="width:400px" type="text" path="upperbound"/></td>
								<td><a href=""><button type="button">F(x)</button></a></td>
							</tr>
							<tr>
								<td>Type</td>
								<td><form:input style="width:400px" type="text" path="type"/></td>
								<td>Unit</td>
								<td><form:input style="width:400px" type="text" path="unit"/></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td><a href=""><button type="button"></button></a>
								<a href=""><button type="button"></button></a></td>
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