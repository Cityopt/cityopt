<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="com.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="show_scenarios" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:100%;overflow:auto">
			<table class="maintable">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="scenario_data_small"/></td>
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
				<tr class="spacerowbig"></tr>
				<tr>
					<td valign="top">
						<table>
							<tr>
								<td class="spacecolumn"></td>
								<td valign="top">
									<b><spring:message code="scenarios"></spring:message></b>
									<table class="tablestyle" width="600">
										<col style="width:200px">	
										<col style="width:250px">
																															
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										    <!-- Description -->
										    <th><spring:message code="description"/></th>
										</tr>
														
										<c:forEach items="${scenarios}" var="scenario">
										<tr>
											<td>${scenario.name}</td>
											<td>${scenario.description}</td>			
									   	</tr>
										</c:forEach>				
									</table>
								</td>
								<td class="spacecolumn"></td>
						    </tr>
					       	<tr>
					       		<td class="spacecolumn"></td>
						   		<td align="right">
						   			<a href="importdata.html"><button type="button">
						   			<spring:message code="back"/></button></a>
						   		</td>
								<td class="spacecolumn"></td>
						   	</tr>
						   	<tr height="100%"><td></td></tr>
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