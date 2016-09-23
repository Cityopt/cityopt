<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Access denied</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="20"></td>
		<td valign="top">
			<h1><spring:message code="access_is_denied"/></h1>
			<h1><spring:message code="no_authority"/></h1>			
		</td>
	</tr>
</table>
</body>
</html>