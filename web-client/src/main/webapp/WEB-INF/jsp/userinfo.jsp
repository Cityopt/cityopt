<%--@elvariable id="user" type="eu.cityopt.DTO.AppUserDTO"--%>
<%--@elvariable id="userRole" type="eu.cityopt.DTO.UserGroupProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="user_info"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="user_info"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<tr>
								<td class="infosmall">
									<spring:message code="name"/>
								</td>
							</tr>
							<tr>
								<td class="activeline">
									${user.name}
								</td>
							</tr>
							<tr class="spacerowbig"></tr>
							<tr>
								<td class="active">
									<!-- User roles -->
									<spring:message code="roles"/>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="400">
										<col style="width:150px">	
										<col style="width:150px">	
										
										<tr height="20">
										    <th><spring:message code="user_role"/></th>
										    <th><spring:message code="project"/></th>
										</tr>
										
										<c:forEach items="${userRoles}" var="userRole">
											<tr>
												<td>							    	
										    		${userRole.usergroup.name}
										   		</td> 	
												<td>
													${userRole.project.name}					
												</td>
										   	</tr>
										</c:forEach>
									
									</table>
								</td>
							</tr>
							<tr height="40"></tr>
							<tr>
								<td class="info"><spring:message code="change_password" /></td>
							</tr>
							<tr><td class="activeline"></td></tr>
							<form:form action="confirmpassword.html?userid=${user.userid}" method="post" modelAttribute="passwordForm">
							<tr>
								<td class="infosmall">
									<spring:message code="old_password"/>*
								</td>
							</tr>
							<tr>
								<td>
									<form:input style="width:300px" title="${tooltip_edit}" type="password" path="oldPassword"/>
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td align="left">
									<button class="activebutton" type="submit" style="width:160px"><spring:message code="change_password" /></button>
								</td>
							</tr>
							</form:form>
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>