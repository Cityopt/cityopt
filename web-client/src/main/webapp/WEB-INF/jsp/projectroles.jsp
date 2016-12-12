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
<title>CityOpt <spring:message code="project_roles"/></title>

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
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="project_roles"/>
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
									<spring:message code="project"/>
								</td>
							</tr>
							<tr>
								<td class="activeline">
									${project.name}
								</td>
							</tr>
							<tr class="spacerowbig"></tr>
							<tr>
								<td class="active">
									<spring:message code="project_roles"/>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="450">
										<col style="width:150px">	
										<col style="width:150px">	
										<col style="width:120px">	
										<col style="width:120px">	
										<col style="width:120px">	
										<col style="width:120px">	
										<col style="width:100px">	
										
										<tr height="20">
										    <th><spring:message code="user"/></th>
										    <th><spring:message code="role"/></th>
										    <th></th>
										    <th></th>
										    <th></th>
										    <th></th>
										    <th></th>
										</tr>
										
										<c:forEach items="${projectRoles}" var="projectRole">
											<tr>
												<td>							    	
										    		${projectRole.user.name}
										   		</td> 	
												<td>							    	
										    		${projectRole.projectRole}
										   		</td> 	
												<td>
													<a href="changeprojectrole.html?userid=${projectRole.user.userid}&role=admin">
														<button type="button" style="width: 140px"><spring:message code="make_admin" /></button>
													</a>
												</td>
												<td>
													<a href="changeprojectrole.html?userid=${projectRole.user.userid}&role=expert">
														<button type="button" style="width: 140px"><spring:message code="make_expert" /></button>
													</a>
												</td>
												<td>
													<a href="changeprojectrole.html?userid=${projectRole.user.userid}&role=standard">
														<button type="button" style="width: 140px"><spring:message code="make_standard" /></button>
													</a>
												</td>
												<td>
													<a href="changeprojectrole.html?userid=${projectRole.user.userid}&role=guest">
														<button type="button" style="width: 140px"><spring:message code="make_guest" /></button>
													</a>
												</td>
												<td>
													<a href="changeprojectrole.html?userid=${projectRole.user.userid}&role=remove">
														<button type="button" style="width: 110px"><spring:message code="remove_role" /></button>
													</a>
												</td>
										   	</tr>
										</c:forEach>
									
									</table>
								</td>
							</tr>
							<tr height="40"></tr>
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