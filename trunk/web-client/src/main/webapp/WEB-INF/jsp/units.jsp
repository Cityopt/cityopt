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
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:600px;overflow:auto">
			<table>
				<tr>
					<td>
						<h1><spring:message code="units"/></h1>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="400px">
							<col style="width:150px">	
							<col style="width:150px">	
							<col style="width:100px">	
							
						<tr height="20">
							<!-- Name -->
						    <th><spring:message code="name"/></th>
						    <!-- Data reliability -->
						    <th><spring:message code="data_reliability"/></th>
						    <!-- Delete -->
						    <th><spring:message code="delete"/></th>
						</tr>
						
						<c:forEach items="${units}" var="unit">
						<tr>
							<td>${unit.name}</td>
							<td>
							</td>
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
					<td valign="top">
						<!-- Create unit -button -->
						<a href="createunit.html"><button>
						<spring:message code="create_unit"/></button></a>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>