<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Metric definition</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>

<div style="overflow:scroll;height:400px;width:500px;overflow:auto">
<form method="get" action="MetricController">
<h2>Metric definition</h2>
<table class="tablestyle" width="400" border="1">

<tr height="20">
    <th>Name</th>
    <th>Equation</th>
    <th>Unit</th>
    <th>Selected</th>
</tr>

<tr height="20">
    <td>project 1</td>
    <td>x</td>
    <td>x</td>
    <td> <input type="radio"/> </td>
</tr>

<tr height="20">
    <td>project 2</td>
    <td>x</td>
    <td>x</td>
    <td> <input type="radio"/> </td>
</tr>

<% //for(int i=0; i<l.size();i++) {%>
<!--    <tr>
        <td> <%//out.print(b.get(i).getIsbn());%> </td>
        <td> <%//out.print(b.get(i).getTitle());%> </td>
        <td> <%//out.print(b.get(i).getAuthor());%> </td>
        <td> <%//out.print(b.get(i).getPrice());%> </td>
        <td> <!-- <input type="radio" name="project" value="<%//Integer.toString(i);%>"/>--> </td>
    <!-- </tr>-->
<% //} %>
</table>

<table width="400">

<tr>
	<td align="left">
		<input align="left" type="submit" value="Upload the metrics"/>
	</td>
	<td align="right">
		<input align="right" type="submit" value="Create"/>
		<input align="right" type="submit" value="Clone"/>
		<input align="right" type="submit" value="Edit"/>
		<input align="right" type="submit" value="Delete"/>
    </td>
</tr>
      
</table>
</form>
</div>
</body>
</html>