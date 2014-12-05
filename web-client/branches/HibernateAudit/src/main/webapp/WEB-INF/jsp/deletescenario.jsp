<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Delete scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">	
			<div style="overflow:scroll;height:400px;width:500px;overflow:auto">
			<form:form method="post" action="deletescenario.html">
			<h2>Delete scenario</h2>
			<table class="tablestyle" width="400" border="1">
			
			<tr height="20">
			    <th>Name</th>
			    <th>Id</th>
			    <th>Description</th>
			    <th>Creation date</th>
			    <th>Setting</th>
			    <th>Run</th>
			    <th>Open</th>
			</tr>
			
			<c:forEach items="${scenarios}" var="scenario">
				<tr>
					<td>${scenario.name}</td>
					<td>${scenario.scenid}</td>			
					<td>${scenario.description}</td>
					<td></td>
					<td></td>
					<td></td>
			    	<td>
						<a href="<c:url value='deletescenario.html?scenarioid=${scenario.scenid}'/>">
							<button align="right"  type="button" value="Delete">Delete</button>
						</a>
					</td>
			   	</tr>
			</c:forEach>
			</table>
			</form:form>	
			</div>
		</td>
	</tr>
</table>
</body>
</html>