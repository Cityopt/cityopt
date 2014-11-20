<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt GIS Coordinates</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>

<div style="overflow:scroll;height:500px;width:900px;overflow:auto">
<form method="get" action="CoordinateController">
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
		<td>
			<img src="assets/img/test_map.jpg"/>
		</td>			
		<td>
			<table class="tablestyle" height="300">
				<col style="width: 150px">
				<col style="width: 100px">
				<col style="width: 100px">
				<tr height="20">
    				<th>Component</th>
    				<th>Geometry</th>
    				<th>Type</th>
				</tr>
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
				<tr>
					<td>x</td>			
					<td>x</td>			
					<td>x</td>			
				</tr>			
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
	    	<input type="button" value="Accept">
	    	<input type="button" value="Cancel">
    	</td>
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
</form>
</div>
</body>
</html>