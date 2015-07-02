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
<title>CityOpt create user</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<form:form method="post" action="createrole.html?userid=${user.userid}">
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<form:form method="post" modelAttribute="user">
			<h2>Create role</h2>

			<table align="center">
				<col style="width:100px">
				<col style="width:300px">
				<tr>
					<td>
						User
					</td>
					<td>
						${user.name}
					</td>
				</tr>
				<tr>
					<td>
						Role
					</td>
					<td>
						<select name="roleType" id="roleType" size="1">
							<option value="Guest" selected>Guest</option>
							<option value="Standard">Standard</option>
							<option value="Expert">Expert</option>
							<option value="Administrator">Administrator</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						Project
					</td>
					<td>
						<select name="roleProjectId" id="roleProjectId" size="1">
							<c:forEach items="${projects}" var="project">
								<option value="${project.prjid}">${project.name}</option>
							</c:forEach>	
						</select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="right"><input style="width:100px" type="submit" value="Create role"/>
					<a href="edituser.html?userid=${user.userid}"><button style="width:100px" type="button" value="Cancel">Cancel</button></a></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</form:form>
</body>
</html>