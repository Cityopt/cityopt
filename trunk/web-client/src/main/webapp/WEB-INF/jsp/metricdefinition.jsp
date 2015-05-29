<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="metric" type="com.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project metrics</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="50"></td>
		<td valign="top">
			<div style="overflow:scroll;height:400px;width:1000px;overflow:auto">
			<table>
				<tr>
					<td><h2>Project metrics</h2></td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="800px">
							<col style="width: 150px">
							<col style="width: 100px">
							<col style="width: 250px">
							<col style="width: 100px">
							<col style="width: 100px">
							<col style="width: 100px">

							<tr height="20">
							    <th>Name</th>
							    <th>Id</th>
							    <th>Expression</th>
							    <th>Clone</th>
							    <th>Edit</th>
							    <th>Delete</th>
							</tr>
							
							<c:forEach items="${metrics}" var="metric">
							<tr>
								<td>${metric.name}</td>
						    	<td>${metric.metid}</td>
						    	<td>${metric.expression}</td>
								<td>
									<a href="<c:url value='metricdefinition.html?metricid=${metric.metid}&action=clone'/>">
										<button align="right" type="button" value="Clone">Clone</button>
									</a>
								</td>
								<td>
									<a href="<c:url value='editmetric.html?metricid=${metric.metid}'/>">
										<button align="right" type="button" value="Edit">Edit</button>
									</a>
								</td>
								<td>
									<a href="<c:url value='metricdefinition.html?metricid=${metric.metid}&action=delete'/>" onclick="return confirm('Are you sure you want to delete metric')">
										<button align="right" type="button" value="Delete">Delete</button>
									</a>
								</td>
						   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				<tr>
					<td align="right">
						<a href="createmetric.html"><button type="button">Create metric</button></a>
			   		</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>