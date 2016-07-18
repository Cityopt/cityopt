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
<title>CityOpt <spring:message code="edit_user_roles"/></title>

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
                           			<spring:message code="edit_user_roles"/>
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
										<tr height="10">
											<td>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td class="active">
									<!-- User roles -->
									<spring:message code="user_roles"/>
								</td>
								<td>
									
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="400">
										<col style="width:150px">	
										<col style="width:150px">	
										<col style="width:50px">	
										
										<tr height="20">
											<!-- Role, Project & Remove -->
										    <th><spring:message code="user_role"/></th>
										    <th><spring:message code="project"/></th>
										    <th><spring:message code="remove"/></th>
										</tr>
										
										<c:forEach items="${userRoles}" var="userRole">
											<tr>
												<td>							    	
										    		${userRole.usergroup.name}
										   		</td> 	
										    								    	
												<td>
													${userRole.project.name}					
												</td>
												
												<td>
													<c:set var="tooltip_delete"><spring:message code="tooltip_delete"/></c:set>	
													<a href="<c:url value='removerole.html?userid=${user.userid}&ugpid=${userRole.usergroupprojectid}'/>" title="${tooltip_delete}"
													 onclick="return confirm('Are you sure you want to remove this role?')">
														<button align="right" type="button"><spring:message code="remove"/></button>
													</a>
												</td>
										   	</tr>
										</c:forEach>
									
									</table>
								</td>
							</tr>
							<tr>
								<!-- Create role and back -buttons -->
								<td align="right"><a href="createrole.html?userid=${user.userid}"><button style="width:100px" type="button" value="Create"><spring:message code="create_role"/></button></a>
								<a href="usermanagement.html"><button style="width:100px" type="button" value="Back"><spring:message code="back"/></button></a></td>
							</tr>
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