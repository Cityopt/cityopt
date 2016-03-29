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
<title>CityOpt <spring:message code="edit_component"/></title>

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
			<form:form method="post" action="editcomponent.html?componentid=${component.componentid}" modelAttribute="component">
			<!--Edit component-->
			<h1><spring:message code="edit_component"/></h1>

			<!-- Tooltips -->
			<c:set var="tooltip_edit"><spring:message code="tooltip_edit_name"/></c:set>
			<c:set var="tooltip_update"><spring:message code="tooltip_update"/></c:set>
			<c:set var="tooltip_cansel"><spring:message code="tooltip_cansel"/></c:set>

			<table align="center">
				<col style="width:150px">
				<col style="width:80px">
				<col style="width:80px">
				<tr>
					<td>
					<!--Name-->
					<spring:message code="name"/>*
					</td>
					<td>
						<form:input style="width:300px" title="${tooltip_edit}" type="text" path="name"/>
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<!-- Update submit and Cancel -button -->
					<td align="right"><input title="${tooltip_update}" style="width:100px" type="submit" value="<spring:message code="update"/>"/>
					<a href="projectparameters.html"><button title="${tooltip_cansel}" style="width:100px" type="button" value="Cancel">
					<spring:message code="cancel"/></button></a></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>