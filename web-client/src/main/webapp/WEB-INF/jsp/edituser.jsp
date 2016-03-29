<%--@elvariable id="user" type="eu.cityopt.DTO.AppUserDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="edit_user"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
<form:form action="edituser.html?userid=${user.userid}" method="post" modelAttribute="user">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:800px;overflow:auto">
			<!--Edit user  -->
			<h1><spring:message code="edit_user"/></h1>

			<table>
				<tr>
					<td>
						<table align="left">
							<col style="width:80px">
							<col style="width:250px">
						
							<tr>
								<td>
									<!--Name-->
									<spring:message code="name"/>:
								</td>
								<td>
									${user.name}
								</td>
							</tr>
							<tr>
								<td>
									<spring:message code="password"/>*:
								</td>
								<td>
									<form:input style="width:300px" title="${tooltip_edit}" type="text" path="password"/>
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="right">
						<input type="submit" style="width:100px" value="Update"/>
						<a href="usermanagement.html"><button style="width:100px" type="button" value="Back"><spring:message code="back"/></button></a>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
   </form:form>
</table>
</body>
</html>