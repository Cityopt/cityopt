<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenarioForm" type="com.cityopt.web.ScenarioForm"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="open_scenario"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
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
								<td><spring:message code="open_scenario"/></td>
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
						<table class="tablestyle" width="750">
							<col style="width:200px">	
							<col style="width:400px">
							<col style="width:50px">
							<col style="width:50px">
							<col style="width:50px">
																												
							<tr height="20">
								<!--Name-->
							    <th><spring:message code="name"/></th>
							    <!--Description-->
							    <th><spring:message code="description"/></th>
							    <!--Status-->    
							    <th><spring:message code="status"/></th>				    
							    <!--Open-->    
							    <th><spring:message code="open"/></th>
							    <!-- Clone-->				
							    <th><spring:message code="clone"/></th>
							</tr>
											
							<c:forEach items="${scenarioForms}" var="scenarioForm">
							<tr>
								<td>${scenarioForm.name}</td>
						    	<td>${scenarioForm.description}</td>
								<td>${scenarioForm.status}</td>			
								<td>
									<c:set var="tooltip_open_scenario"><spring:message code="tooltip_open_scenario"/></c:set>
									<a href="<c:url value='openscenario.html?scenarioid=${scenarioForm.id}'/>" title="${tooltip_open_scenario}">
										<button align="right" type="button" value="Open"><spring:message code="open"/>
										</button>
									</a>
								</td>
								<td>
									<c:set var="tooltip_clone"><spring:message code="tooltip_clone"/></c:set>
									<a href="<c:url value='clonescenario.html?scenarioid=${scenarioForm.id}'/>" title="${tooltip_clone}">
										<button align="right" type="button" value="Clone"><spring:message code="clone"/></button>
									</a>
								</td>
						   	</tr>
							</c:forEach>				
						</table>
					</td>
				</tr>
				<tr class="spacerowbig">
				</tr>
			</table>
			</div>
		</td>
    </tr>
</table>	  
</body>
</html>