<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Open project</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>
		<td width=30></td>
		<td>
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<form:form method="post" modelAttribute="projectSelected">
			<h2>Open project</h2>
			<table class="tablestyle" width="400" border="1">
			
			<tr height="20">
			    <th>Name</th>
			    <th>Id</th>
			    <th>Location</th>
			    <th>Description</th>
			    <th>Open</th>
			</tr>
			
			<c:forEach items="${projects}" var="project">
				<tr>
					<td>${project.name}</td>
			    	<td>${project.prjid}</td>
					<td>${project.location}</td>			
					<td></td>
					<td>
						<a href="<c:url value='openproject.html?prjid=${project.prjid}'/>">
							<button align="right"  type="button" value="Open">Open</button>
						</a>
					</td>
			   	</tr>
			</c:forEach>
			
			
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
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>