<%--@elvariable id="user" type="eu.cityopt.DTO.AppUser"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="create_user"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td><%@ include file="mainmenu.inc"%></td>
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
                           			<spring:message code="account_creation"/>
								</td>
								<td align="left" width="40">
									<div class="round-button">
										<div class="round-button-circle">
											<a href="" onclick="openInfoWindow()">?</a>
										</div>
									</div>
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
						<form:form modelAttribute="UserForm" method="post">
						<!-- csrt support (version 4.0) -->
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

						<table align="left">
							<col style="width: 150px">
							<col style="width: 300px">
							
							<tr class="username">								
								<td class="infosmall"><label for="projectname"><spring:message code="username" />*:</label></td>
								<c:set var="tooltip_name"><spring:message code="tooltip_create_user_name"/></c:set>									
								<td><form:input style="width:300px" type="text" path="name" title="${tooltip_name}"/></td>
								<td><form:errors path="name" cssClass="error"/></td>								
							</tr>
							
							<tr class="password">							
								<td class="infosmall"><label for="location"><spring:message code="password" />*:</label></td>
								<c:set var="tooltip_password"><spring:message code="tooltip_create_user_password"/></c:set>								
								<td><form:input style="width:300px" type="text"	path="password" title="${tooltip_password}" /></td>
								<td><form:errors path="password" cssClass="error"/></td>
							</tr>
							
							<tr class="enabled">	
								<td class="infosmall"><spring:message code="activate" />
								<c:set var="tooltip_enabled"><spring:message code="tooltip_enable_user"/></c:set>
								<td><form:radiobutton path="enabled" value="true" checked="checked" title="${tooltip_enabled}" /> 
								<spring:message code="yes" /> 
									<form:radiobutton path="enabled" value="false" title="${tooltip_enabled}" /> 
								<spring:message code="no" />
								</td>
								<td>
							</tr>
							
							<tr>							
								<td class="infosmall"><spring:message code="role" />:</td>
								<td>
								<c:set var="tooltip_role"><spring:message code="tooltip_role"/></c:set>	
								<form:select path="role" title="${tooltip_role}" >
									<c:forEach items="${userGroups}" var="userGroup">																																
										<option value="${userGroup.usergroupid}">${userGroup.name}</option>
									</c:forEach>
								</form:select>
								</td>								
							</tr>						
							<tr class="submit" height=50px;>
								<td></td>
								<!-- Create submit and Cancel button -->
								<td align="right"><input style="width: 100px" type="submit"
									value="<spring:message code="create"/>" /> <td>
									<a href="usermanagement.html">
										<button	style="width: 100px" type="button" value="Cancel">
											<spring:message code="cancel" />
										</button>
									</a>
								</td>
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
</body>
</html>