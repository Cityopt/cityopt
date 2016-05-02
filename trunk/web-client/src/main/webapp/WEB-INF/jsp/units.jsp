<%--@elvariable id="unit" type="com.cityopt.DTO.UnitDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="units"/></title>

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
			<table class="maintablenarrow">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="units"/>
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
									<table class="tablestyle" width="250px">
										<col style="width:150px">	
										<col style="width:100px">	
										
									<tr height="20">
										<!-- Name -->
									    <th><spring:message code="name"/></th>
									    <!-- Delete -->
									    <th><spring:message code="delete"/></th>
									</tr>
									
									<c:forEach items="${units}" var="unit">
									<tr>
										<td>${unit.name}</td>
										<td><a href="<c:url value='deleteunit.html?unitid=${unit.unitid}'/>">
												<!-- Delete button -->
												<button align="right" type="button" value="Delete">
												<spring:message code="delete"/></button>
											</a>
										</td>
								   	</tr>
									</c:forEach>
									</table>
								</td>
								<td width="20">
								</td>
							</tr>
							<tr>
								<td valign="top" align="right">
									<!-- Create unit -button -->
									<a href="createunit.html"><button>
									<spring:message code="create_unit"/></button></a>
								</td>
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