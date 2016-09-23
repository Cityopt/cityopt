<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt Parameter reliability</title>

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
			<table>
				<tr>
					<td>
						<!-- Input parameters Reliability -->
						<h1><spring:message code="input_parameters_reliability"/></h1>
					</td>
				</tr>
				<tr>
					<td>
						<div style="overflow:scroll;height:500px;width:600px;overflow:auto">
						<table class="tablestyle" width="400px" border="1">
							<col style="width:400px">	
							
							<tr height="20">
							<!-- Data reliability -->
							    <th><spring:message code="data_reliability"/></th>
							</tr>
						
							<tr>
								<td>x</td>
							</tr>	
							<tr>
								<td>x</td>
							</tr>	
							<tr>
								<td>x</td>
							</tr>	
							<tr>
								<td>x</td>
							</tr>	
						</table>
						</div>
					</td>
					<td valign="top">
						<!-- Create new entry -button -->
						<a href=""><button style="width: 150px">
						<spring:message code="create_new_entry"/></button></a><br>
						
						<!-- Delete entry -button -->
						<a href=""><button style="width: 150px">
						<spring:message code="delete_entry"/></button></a>
						
					</td>
				</tr>
			</table>
		</td>
     </tr>
</table>
</body>
</html>