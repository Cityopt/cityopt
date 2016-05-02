<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="delete_scenario"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 820px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="delete_scenario"/></td>
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
					<td valign="top">
						<form:form method="post" action="deletescenario.html">			
						<table class="tablestyle" width="700">
							<col style="width:200px">	
							<col style="width:450px">
							<col style="width:50px">
							
							<tr height="20">
								<!-- Name -->
							    <th><spring:message code="name"/></th>
							    <!-- Description -->
							    <th><spring:message code="description"/></th>
							    <!-- Delete -->
							    <th><spring:message code="delete"/></th>
							</tr>
							
						<c:forEach items="${scenarios}" var="scenario">
							<tr>
								<td>${scenario.name}</td>
								<td>${scenario.description}</td>
						    	<td>
						    		<!-- delete button -->
						    		<c:set var="tooltip_delete"><spring:message code="tooltip_delete"/></c:set>
									<a href="<c:url value='deletescenario.html?scenarioid=${scenario.scenid}'/>"
									onclick="return confirm('<spring:message code="confirm_scenario_deletion"/>')">
										<button align="right" title="${tooltip_delete}"  type="button" value="Delete">
										<spring:message code="delete"/></button>
									</a>
								</td>
						   	</tr>
						</c:forEach>
						</table>
						</form:form>	
					</td>
				</tr>
				<tr height="10"></tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>