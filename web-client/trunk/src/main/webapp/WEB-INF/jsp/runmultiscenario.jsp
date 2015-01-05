<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%--@elvariable id="scenario" type="com.cityopt.model.Scenario"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Run Multi-scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
<tr>
	<td>
		<%@ include file="mainmenu.inc"%>
	</td>
	<td width="50"></td>
	<td>
		<div style="overflow:scroll;height:400px;width:600px;overflow:auto">
		<form method="post" action="runmultiscenario.html">
		<h2>Run Multi-scenario</h2>
		<table class="tablestyle" width="600" border="1">
			<tr height="20">
			    <th>Name</th>
			    <th>Id</th>
			    <th>Description</th>
			    <th>User</th>
			    <th>Creation date</th>
			    <th>Setting</th>
			    <th>Run</th>
			    <th>Select</th>
			</tr>

		<c:forEach items="${project.scenarios}" var="scenario">
		<tr>
			<td>${scenario.name}</td>
		   	<td>${scenario.scenid}</td>
		    <td>${scenario.description}</td>
		    <td>x</td>
	    	<td>x</td>
	   		<td>x</td>
	    	<td>x</td>
	    	<td> <input type="checkbox"/> </td>
	  	</tr>
	</c:forEach>
		</table>

		<table width="600">
			<tr>
				<td align="right">
					<input align="right" type="submit" value="Run Multi-scenario"/>
			    </td>
			</tr>
      	</table>
		</form>
		</div>
	</td>
</tr>
</table>
		
</body>
</html>