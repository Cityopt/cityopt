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
<title>CityOpt <spring:message code="output_variables" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
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
								<td><spring:message code="external_parameters"/></td>
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
					<td valign="top">
						<table>
							<col style="width:40px">
							<col style="width:30px">
							<col style="width:850px">	
							<tr class="external_parameter_sets">
								<td><b><spring:message code="selected_external_parameter_set" />:</b>
									${extParamValSet.name} <br>
								</td>
							</tr>
							<tr>
								<td>
								<table class="tablestyle" width="750">
									<col style="width: 200px">
									<col style="width: 200px">
									<col style="width: 100px">
									<col style="width: 100px">
									<col style="width: 100px">
									<tr height="20">
										<!-- Name -->
										<th><spring:message code="name" /></th>
										<!-- Comment -->
										<th><spring:message code="comment" /></th>
										<!-- Type -->
										<th><spring:message code="type" /></th>
										<!-- Unit -->
										<th><spring:message code="unit" /></th>
										<!-- Value -->
										<th><spring:message code="value" /></th>
									</tr>
			
									<c:forEach items="${extParamVals}" var="extParamVal">
										<tr>
											<td>${extParamVal.extparam.name}</td>
											<td>${extParamVal.comment}</td>
											<td>${extParamVal.extparam.getType().getName()}</td>
											<td>${extParamVal.extparam.unit.name}</td>
											<td>${extParamVal.value}</td>
										</tr>
									</c:forEach>
								</table>
									<table width="100%">
									
										<tr height="10">
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
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>