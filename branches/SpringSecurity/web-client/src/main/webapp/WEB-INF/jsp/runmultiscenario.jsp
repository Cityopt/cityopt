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
<title>CityOpt Run Multi-scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
<tr>
	<td>
		<%@ include file="mainmenu.inc"%>
	</td>
	<td width="50"></td>
	<td valign="top">
		<div style="overflow:scroll;height:400px;width:600px;overflow:auto">
		<form:form method="post" action="runmultiscenario.html">
		<!-- Run Muli -scenario -->
		<h2><spring:message code="run_multi_scenario"/></h2>
		<table class="tablestyle" width="600" border="1">
		
			<!-- Table(Name,Id,Description,Users,Creation Date,Setting,Run,Select) -->
			<tr height="20">				
			    <th><spring:message code="name"/></th>
			    <th><spring:message code="id"/></th>
			    <th><spring:message code="description"/></th>
			    <th><spring:message code="users"/></th>
			    <th><spring:message code="creation_date"/></th>
			    <th><spring:message code="setting"/></th>
			    <th><spring:message code="run"/></th>
			    <th><spring:message code="select"/></th>
			</tr>

		<c:forEach items="${scenarios}" var="scenario">
		<tr>
			<td>${scenario.name}</td>
		   	<td>${scenario.scenid}</td>
		    <td>${scenario.description}</td>
		    <td>x</td>
	    	<td>x</td>
	   		<td>x</td>
	    	<td>x</td>
	    	<td> </td>
	  	</tr>
	</c:forEach>
		</table>

		<table width="600">
			<tr>
				<td align="right">
					<input align="right" type="submit" value="<spring:message code="run_multi_scenario"/>"/>
			    </td>
			</tr>
      	</table>
		</form:form>
		</div>
	</td>
</tr>
</table>
		
</body>
</html>