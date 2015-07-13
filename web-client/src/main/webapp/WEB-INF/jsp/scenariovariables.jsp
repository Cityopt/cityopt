<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Output variables</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
			<table>
				<col style="width:40px">
				<col style="width:30px">
				<col style="width:850px">	
				<tr>
					<td>
						<!-- External parameters -->
						<h2><spring:message code="external_parameters"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="450">
							<col style="width:200px">
							<col style="width:100px">
							<col style="width:100px">
							<col style="width:50px">
									
							<tr height="20">
								<!-- Name -->
							    <th><spring:message code="name"/></th>
							    <!-- ID -->
							    <th><spring:message code="id"/></th>
							    <!-- Value -->
								<th><spring:message code="value"/></th>
								<!-- Edit -->
							    <th><spring:message code="edit"/></th>
							</tr>
							
							<c:forEach items="${extParamVals}" var="extParamVal">
							<tr>
								<td>${extParamVal.extparam.name}</td>
						    	<td>${extParamVal.extparamvalid}</td>
						    	<td>${extParamVal.value}</td>
						    	<td>
									<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}'/>">
										<button align="right" type="button" value="Edit"><spring:message code="edit"/></button>
									</a>
								</td>
						   	</tr>
							</c:forEach>
						</table>
						
						<table width="100%">
						
							<tr height="30">
								<td></td>
							</tr>
							<tr>
								<!-- Close -button -->
								<td align="right">
									<a href="editscenario.html"><button type="button">
									<spring:message code="close"/></button></a>
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