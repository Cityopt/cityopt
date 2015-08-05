<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="newScenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create scenario</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createscenario.html?action=create" modelAttribute="newScenario">
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
						<!-- Create scenario -->
						<h2><spring:message code="create_scenario"/></h2>																								
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<!-- Scenario name -->
								<td><spring:message code="scenario_name"/>:</td>
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
							
							<tr>
								<!-- Description -->						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width: 200px"></form:textarea></td>
								<td></td>
								<td></td>
							</tr>
							<tr height="10"></tr>
							<tr>						
								<td></td>
								<!-- Create scenario button -->
								<td align="right"><input type="submit" value="<spring:message code="create_scenario"/>" style="width:120px"></td>
								<td></td>
								<td></td>
							</tr>
						</table>
						<!-- error message if error -->
						<element><h2 class="error"></element>${errorMessage}</h2><element>
						<!-- Information message if scenario is created -->
						<element><element><h2 class="successful"></element></element>${successful}</h2><element>				
					<c:choose>
						 <c:when test="${success!=null && success==true}">
						<a href="editscenario.html"><button style="width:100px" type="button" value="Next">
            			   <spring:message code="next"/></button></a>
            			</c:when>
            		</c:choose>	
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>