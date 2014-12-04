<%--@elvariable id="userForm" type="com.cityopt.controller.UserForm"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt edit user</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>
		<td width=30></td>
		<td>
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<form:form method="post" action="edituser.html?action=save" modelAttribute="userForm">
			<h2>Edit user</h2>

			<table align="center">
				<col style="width:150px">
				<col style="width:300px">
				<tr>
					<td>
						Name
					</td>
					<td>
						<form:input style="width:300px" type="text" path="name"/>
					</td>
				</tr>
				<tr>
					<td>
						Company
					</td>
					<td>
						<form:input style="width:300px" type="text" path="company"/>
					</td>
				</tr>
				<tr>
					<td>
						User name
					</td>
					<td>
						<form:input style="width:300px" type="text" path="userName"/>
					</td>
				</tr>
				<tr>
					<td>
						Password
					</td>
					<td>
						<form:input style="width:300px" type="text" path="password"/>
					</td>
				</tr>
				<tr>
					<td>
						Email
					</td>
					<td>
						<form:input style="width:300px" type="text" path="email"/>
					</td>
				</tr>
				<tr>
					<td>
						User role
					</td>
					<td>
						<form:input style="width:300px" type="text" path="userRole"/>
					</td>
				</tr>
				<tr>
					<td>
						Start rights
					</td>
					<td>
						<form:input style="width:300px" type="text" path="startRights"/>
					</td>
				</tr>
				<tr>
					<td>
						Finish rights
					</td>
					<td>
						<form:input style="width:300px" type="text" path="finishRights"/>
					</td>
				</tr>
				<tr>
					<td>
						Project
					</td>
					<td>
						<form:input style="width:300px" type="text" path="project"/>
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="right"><input style="width:100px" type="submit" value="Update"/>
					<a href="usermanagement.html"><button style="width:100px" type="button" value="Cancel">Cancel</button></a></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>