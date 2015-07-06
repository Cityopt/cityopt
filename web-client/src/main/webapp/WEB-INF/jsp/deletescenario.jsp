<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Delete scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<!--Delete scenario title-->	
			<div style="overflow:scroll;height:400px;width:600px;overflow:auto">
			<form:form method="post" action="deletescenario.html">			
			<h2><spring:message code="delete_scenario"/></h2>
			<table class="tablestyle" width="600" border="1">
				<col style="width:200px">	
				<col style="width:50px">
				<col style="width:300px">
				<col style="width:50px">
				
				<tr height="20">
					<!-- Name -->
				    <th><spring:message code="name"/></th>
				    <!-- ID -->
				    <th><spring:message code="id"/></th>
				    <!-- Description -->
				    <th><spring:message code="description"/></th>
				    <!-- Delete -->
				    <th><spring:message code="delete"/></th>
				</tr>
				
			<c:forEach items="${scenarios}" var="scenario">
				<tr>
					<td>${scenario.name}</td>
					<td>${scenario.scenid}</td>			
					<td>${scenario.description}</td>
			    	<td>
			    		<!-- delete button -->
						<a href="<c:url value='deletescenario.html?scenarioid=${scenario.scenid}'/>">
							<button align="right"  type="button" value="Delete">
							<spring:message code="delete"/></button>
						</a>
					</td>
			   	</tr>
			</c:forEach>
			</table>
			</form:form>	
			</div>
		</td>
	</tr>
</table>
</body>
</html>