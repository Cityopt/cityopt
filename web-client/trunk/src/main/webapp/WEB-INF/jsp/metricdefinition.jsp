<%--@elvariable id="metric" type="com.cityopt.model.Metric"--%>
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
		<td>
			<div style="overflow:scroll;height:400px;width:500px;overflow:auto">
			<h2>Project metrics</h2>
			<table class="tablestyle" width="400" border="1">
			
			<tr height="20">
			    <th>Name</th>
			    <th>Id</th>
			    <th>Expression</th>
			    <th>Clone</th>
			    <th>Edit</th>
			    <th>Delete</th>
			</tr>
			
			<c:forEach items="${project.metrics}" var="metric">
				<tr>
				<td>${metric.name}</td>
		    	<td>${metric.metid}</td>
		    	<td>${metric.expression}</td>
				<td>
					<a href="<c:url value='editmetric.html?metricid=${metric.metid}'/>">
						<button align="right" type="button" value="Clone">Clone</button>
					</a>
				</td>
				<td>
					<a href="<c:url value='editmetric.html?metricid=${metric.metid}'/>">
						<button align="right" type="button" value="Edit">Edit</button>
					</a>
				</td>
				<td>
					<a href="<c:url value='editmetric.html?metricid=${metric.metid}'/>">
						<button align="right" type="button" value="Delete">Delete</button>
					</a>
				</td>
		   	</tr>
			</c:forEach>
			</table>
			
			<br>
			
			<table width="400">
			
			<tr>
				<td align="left">
					<input align="left" type="submit" value="Upload the metrics"/>
				</td>
				<td align="right">
					<a href="createmetric.html"><button type="button">Create</button></a>
			    </td>
			</tr>
			      
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>