<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="create_component"/></title>

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
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<form:form method="post" action="createcomponent.html" modelAttribute="component">
			<h2 class="error">${error}</h2>
			<h1><spring:message code="create_component"/></h1>


			<!-- Tool tips -->
			<c:set var="tooltip_name"><spring:message code="tooltip_name"/></c:set>
			<c:set var="tooltip_create"><spring:message code="tooltip_create"/></c:set>
			<c:set var="tooltip_cansel"><spring:message code="tooltip_cansel"/></c:set>

			<table align="center">
				<col style="width:150px">
				<col style="width:80px">
				<col style="width:80px">
				<tr>
					<td>
						<!-- Name -->
						<spring:message code="name"/>*
					</td>
					<td>						
						<form:input style="width:300px" title="${tooltip_name}" type="text" path="name"/>
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<!-- Create & Cancel -button -->
					<td align="right">
						<input title="${tooltip_create}" style="width:100px" type="submit" value="<spring:message code="create"/>"/>
						<input type="submit" style="width:100px" name="cancel" value="cancel" />
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