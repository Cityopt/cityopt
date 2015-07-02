<%--@elvariable id="user" type="eu.cityopt.DTO.AppUserDTO"--%>
<%--@elvariable id="userRole" type="eu.cityopt.DTO.UserGroupProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
		<td width=30></td>
		<td>
			<div style="overflow:scroll;height:800px;width:800px;overflow:auto">
			<h2>Edit user</h2>

			<table>
				<tr>
					<td>
						<table align="left">
							<col style="width:80px">
							<col style="width:250px">
							<tr>
								<td>
									Name
								</td>
								<td>
									${user.name}
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
					<td>
						<b>User roles</b>
					</td>
					<td>
						
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="600">
							<col style="width:100px">	
							<col style="width:150px">	
							<col style="width:50px">	
							
							<tr height="20">
							    <th>Role</th>
							    <th><spring:message code="project"/></th>
							    <th>Remove</th>
							</tr>
							
							<c:forEach items="${projectRoles}" var="projectRole">
								<tr>
							    	<td>${projectRole.usergroup.name}</td>
									<td>${projectRole.project.name}</td>
									<td>
										<a href="<c:url value='removerole.html?roleid=${projectRole.usergroup.id}'/>">
											<button align="right" type="button"><spring:message code="remove"/></button>
										</a>
									</td>
							   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				<tr>
					<td align="right"><a href="createrole.html?userid=${user.userid}"><button style="width:100px" type="button" value="Create">Create role</button></a>
					<a href="usermanagement.html"><button style="width:100px" type="button" value="Back">Back</button></a></td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>