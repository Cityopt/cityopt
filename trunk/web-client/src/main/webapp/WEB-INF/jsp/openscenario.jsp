<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="com.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Open scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:600px;overflow:auto">
			
			<!-- Open scenario -->
			<h2> <spring:message code="open_scenario"/></h2>
		
			
			<table class="tablestyle" width="600" border="1">
				<col style="width:200px">	
				<col style="width:50px">
				<col style="width:250px">
				<col style="width:50px">
				<col style="width:50px">
																									
				<tr height="20">
					<!--Name-->
				    <th><spring:message code="name"/></th>
				    <!-- Id -->				    
				    <th><spring:message code="id"/></th>
				    <!--Description-->
				    <th><spring:message code="description"/></th>
				    <!--Open-->    
				    <th><spring:message code="open"/></th>
				    <!-- Clone-->				
				    <th><spring:message code="clone"/></th>
				</tr>
								
				<c:forEach items="${scenarios}" var="scenario">
				<tr>
					<td>${scenario.name}</td>
			    	<td>${scenario.scenid}</td>
					<td>${scenario.description}</td>			
					<td>
						<a href="<c:url value='openscenario.html?scenarioid=${scenario.scenid}'/>">
							<button align="right" type="button" value="Open"><spring:message code="open"/>
							</button>
						</a>
					</td>
					<td>
						<a href="<c:url value='clonescenario.html?scenarioid=${scenario.scenid}'/>">
							<button align="right" type="button" value="Clone"><spring:message code="clone"/></button>
						</a>
					</td>
			   	</tr>
				</c:forEach>				
			</table>
			</div>
		</td>
    </tr>
</table>	  
</body>
</html>