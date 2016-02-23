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
						<!-- User -->
						<spring:message code="user"/>
					</td>
					<td>
						${user.name}
					</td>
				</tr>
				<tr>
					<td>
						<!-- Role -->
						<spring:message code="role"/>
					</td>
					<td>
						<!-- Roletypes: Guest, Standard, Expert, Administrator -->
						<select name="roleType" id="roleType" size="1">
							<option value="Guest" selected><spring:message code="guest"/></option>
							<option value="Standard"><spring:message code="standard"/></option>
							<option value="Expert"><spring:message code="expert"/></option>
							<option value="Administrator"><spring:message code="administrator"/></option>
						</select>
					</td>
				</tr>
				<c:forEach items="${userRoles}" var="projectRole">
								<tr>
									<td>							    	
							    		<form:select path="role">
							    			<option value="${projectRole.usergroup.name}" selected>${projectRole.usergroup.name}</option>
							    				<c:forEach items="${userGroups}" var="userGroup">
							    					<option value="${userGroup.usergroupid}">${userGroup.name}</option>							    			
							    				</c:forEach>
							    		</form:select>
							   		</td> 	
							    								    	
									<td>
										<form:select path="project">
												<option value="${projectRole.project.name}"	selected>${projectRole.project.name}</option>
										<c:forEach items="${projects}" var="project">
												<option value="${project.name}">${project.name}</option>
										</c:forEach>								
										</form:select>								
									</td>
									
									<td>
										<c:set var="tooltip_delete"><spring:message code="tooltip_delete"/></c:set>	
										<a href="<c:url value='removerole.html?userid=${user.userid}&projectid=${projectRole.usergroupprojectid}'/>" title="${tooltip_delete}"
										 onclick="return confirm('<spring:message code="confirm_project_deletion"/>')">
											<button align="right" type="button"><spring:message code="remove"/></button>
										</a>
									</td>
							   	</tr>
							</c:forEach>
				<tr>
					<td>
						<!--Project-->
						<spring:message code="project"/>
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
					<!-- Create role submit and Cancel -button -->
					<td align="right"><input style="width:100px" type="submit" value="<spring:message code="create_role"/>"/>
					<a href="editroles.html?userid=${user.userid}"><button style="width:100px" type="button" value="Cancel">
					<spring:message code="cancel"/></button></a></td>
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