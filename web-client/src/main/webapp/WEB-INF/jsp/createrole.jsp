<%--@elvariable id="user" type="eu.cityopt.DTO.AppUserDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="create_user"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<form:form method="post" action="createrole.html?userid=${user.userid}">
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
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
                           			<spring:message code="create_role"/>
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
						<form:form method="post" modelAttribute="user">
						<table align="center">
							<col style="width:100px">
							<col style="width:250px">
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
									<select name="roleType" id="roleType" size="1" style="width: 200px">
										<option value="Guest" selected><spring:message code="guest"/></option>
										<option value="Standard"><spring:message code="standard"/></option>
										<option value="Expert"><spring:message code="expert"/></option>
										<option value="Administrator"><spring:message code="administrator"/></option>
									</select>
								</td>
							</tr>
							<tr>
								<td>
									<!--Project-->
									<spring:message code="project"/>
								</td>
								<td>
									<select name="roleProjectId" id="roleProjectId" size="1" style="width: 200px">
										<c:forEach items="${projects}" var="project">
											<option value="${project.prjid}">${project.name}</option>
										</c:forEach>	
									</select>
								</td>
							</tr>
							<tr class="spacerowbig"></tr>
							<tr>
								<td></td>
								<td align="left">
									<button class="activebutton" style="width:100px" type="submit"><spring:message code="create_role"/></button>
									<a href="editroles.html?userid=${user.userid}"><button style="width:100px" type="button" value="Cancel">
									<spring:message code="cancel"/></button></a></td>
							</tr>
						</table>
						</form:form>
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