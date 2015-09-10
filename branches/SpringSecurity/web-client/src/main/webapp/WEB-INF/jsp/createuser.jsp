<%--@elvariable id="user" type="eu.cityopt.DTO.AppUser"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt create user</title>

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
			<div style="overflow:scroll;height:800px;width:800px;overflow:auto">
			<form:form method="post" modelAttribute="user">
			<h2>Create user</h2>

			<table align="center">
				<col style="width:150px">
				<col style="width:300px">
				<tr>
					<td>
						<!--Name-->
						<spring:message code="name"/>
					</td>
					<td>
						<form:input style="width:300px" type="text" path="name"/>
					</td>
				</tr>
				<tr>
					<td>
						<!--Password-->
						<spring:message code="password"/>
					</td>
					<td>
						<form:input style="width:300px" type="text" path="password"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<!-- Create submit and Cancel button -->
					<td align="right"><input style="width:100px" type="submit" value="<spring:message code="create"/>"/>
					<a href="usermanagement.html"><button style="width:100px" type="button" value="Cancel">
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