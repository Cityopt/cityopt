<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Run Multi-Optimization Set</title>

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
<h2>Run Multi-Optimization Set</h2>
<table class="tablestyle" width="600" border="1">

<tr height="20">
    <th>Name</th>
    <th>Type</th>
    <th>User</th>
    <th>Description</th>
    <th>Creation date</th>
    <th>Setting</th>
    <th>Run</th>
    <th>Select</th>
</tr>

<tr height="20">
    <td>optimization set 1</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td> <input type="checkbox"/> </td>
</tr>

<tr height="20">
    <td>optimization set 2</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td>x</td>
    <td> <input type="checkbox"/> </td>
</tr>
</table>

<table width="600">

<tr>
	<td align="right">
		<input align="right" type="submit" value="Run Multi-Optimization Set"/>
    </td>
</tr>
      
</table>
</form>
</div>
</body>
</html>