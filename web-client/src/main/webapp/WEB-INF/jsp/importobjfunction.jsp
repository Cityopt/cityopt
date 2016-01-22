<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="objectivefunction" type="com.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt import objective function</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<!--Import objective function-->
						<h2><spring:message code="import_objective_function"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:700px">

							<tr>
								<!-- Objective functions -->
								<td><b><spring:message code="objective_functions"/></b></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:200px">
										<col style="width:540px">
										<tr>
											<!-- Select, Objective function, Expression -->
											<th><spring:message code="select"/></th>
											<th><spring:message code="objective_function"/></th>
											<th><spring:message code="expression"/></th>
										</tr>
							
										<c:forEach items="${objFuncs}" var="objectivefunction">
										<tr>
											<td><a href="<c:url value='importobjfunction.html?objectivefunctionid=${objectivefunction.obtfunctionid}'/>"><spring:message code="select"/></a></td>
											<td>${objectivefunction.name}</td>
									    	<td>${objectivefunction.expression}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<!-- Close -button -->
								<td align="right"><a href="editoptimizationset.html"><button type="button"><spring:message code="close"/></button></a></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height=20></tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>