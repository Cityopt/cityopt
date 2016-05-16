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
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="create_scenario"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<tr>
								<td>
									<table>
										<tr class="infosmall">															
											<td><spring:message code="scenario_name"/>*</td>
										</tr>
										<tr>
											<c:set var="tooltip_name"><spring:message code="tooltip_create_scenario_name"/></c:set>
											<td><form:input type="text" path="name"  title="${tooltip_name}" style="width: 300px"/></td>
											<td><form:errors path="name" cssClass="error"/></td>
											<td></td>
											<td></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr class="infosmall">
											<!-- Description -->						
											<td><spring:message code="description"/></td>
										</tr>
										<tr>
											<c:set var="tooltip_description"><spring:message code="tooltip_create_scenario_description"/></c:set>
											<td><form:textarea type="text" rows="3" title="${tooltip_description}" path="description" style="width: 300px"></form:textarea></td>
											<td></td>
											<td></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>						
											<c:set var="tooltip_create_scenario"><spring:message code="tooltip_create_scenario"/></c:set>
											<td align="right"><input type="submit" title="${tooltip_create_scenario}" value="<spring:message code="create_scenario"/>" style="width:120px"></td>
											<td></td>
											<td></td>
										</tr>
									</table>
									<!-- Information message if scenario is created -->
									<element><element><h2 class="successful"></element></element>${successful}</h2><element>				
									
									<c:if test="${success!=null && success==true}">
									<a href="editscenario.html"><button style="width:100px" type="button" value="Next">
			            			   <spring:message code="next"/></button></a>
			            			</c:if>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>