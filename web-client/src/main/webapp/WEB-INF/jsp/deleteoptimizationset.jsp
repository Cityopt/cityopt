<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Delete optimization set</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>

<div style="overflow:scroll;height:400px;width:600px;overflow:auto">
<form method="get" action="OptimizationController">
<h2>Delete optimization set</h2>
<table class="tablestyle" width="600" border="1">

<tr height="20">
    <th>Name</th>
    <th>Type</th>
    <th>User</th>
    <th>Description</th>
    <th>Creation date</th>
    <th>Setting</th>
    <th>Run</th>
    <th>Delete</th>
</tr>

	<c:forEach items="${optsets}" var="optset">
	<tr>
		<td>${optset.getOptconstraint().name}</td>
   	</tr>
	</c:forEach>
	
</table>

<table width="600">

<tr>
	<td align="right">
		<input align="right" type="submit" value="Delete optimization set"/>
    </td>
</tr>
      
</table>
</form>
</div>
</body>
</html>