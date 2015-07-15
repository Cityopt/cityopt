<%--@elvariable id="user" type="com.cityopt.DTO.AppUserDTO"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Virtual City Login</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<form:form action="index.html" method="post" modelAttribute="user">
<table height="600px" align="center">
	<tr height="200px">
	</tr>
	<tr>
		<td>
			<div class="login">
			<table>
				<tr>
					<td align="center">
						<img src="assets/img/icon_logo_big.jpg"/>
					</td>
				</tr>
				<tr>
					<td>
						<h2 align="center" class="error">${errorMsg}</h2>
					</td>
				</tr>
				<tr>
					<td align="center"><form:input class="login" id="name" path="name" type="text" value="admin" style="width: 100px"/></td>
				</tr>
				<tr>
					<td align="center"><form:input class="login" id="password" path="password" type="password" value="admin" style="width: 100px"/></td>
				</tr>

				<tr height="10"></tr>
				<tr>
					<td align="center">
						<input type="submit" style="width: 100px" value="<spring:message code="login"/>"/>       
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

