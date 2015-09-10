<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt GIS Coordinates</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<div style="overflow:scroll;height:500px;width:900px;overflow:auto">
<table>
<col style="width: 50px">
<col style="width: 300px">
<col style="width: 300px">

	<tr>
		<td></td>
		<td>
			<h2>GIS Coordinates</h2>
		</td>
	</tr>
	<tr>
		<td></td>
		<td valign="top">
			<img src="assets/img/test_map.jpg"/>
		</td>			
		<td valign="top">
			<table class="tablestyle">
				<col style="width: 150px">
				<col style="width: 60px">
				<col style="width: 100px">
				<tr height="20">
    				<th>Component</th>
    				<th>Id</th>
    				<th>Type</th>
				</tr>
				<c:forEach items="${components}" var="component">
				<tr>
					<td>${component.name}</td>
			    	<td>${component.componentid}</td>
					<td>			
					</td>
			   	</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<tr height="20">
	    <td></td>
	    <td align="right"><input type="button" value="Visualize"></td>
	    <td align="right"><input type="button" value="Upload the values"></td>
	</tr>
	
	<tr height="40">
	    <td></td>
   	</tr>

	<tr height="20">
	    <td></td>
	    <td></td>
	    <td align="right">
	    	<a href="editproject.html"><button>Close</button></a>
    	</td>
	</tr>

</table>
</div>
</body>
</html>