<%--@elvariable id="inputParamForm" type="eu.cityopt.model.InputParameter"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt edit input parameter</title>

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
			<form:form method="post" action="editinputparameter.html" modelAttribute="inputParamForm">
			<h2>Edit input parameter</h2>

			<table align="center">
				<col style="width:150px">
				<col style="width:80px">
				<col style="width:80px">
				<tr>
					<td>
						Name
					</td>
					<td>
						<form:input style="width:300px" type="text" path="name"/>
					</td>
				</tr>
				<tr>
					<td>
						Id
					</td>
					<td>
						<form:input style="width:300px" type="text" path="inputid"/>
					</td>
				</tr>
				<tr>
					<td>
						Default value
					</td>
					<td>
						<form:input style="width:300px" type="text" path="defaultvalue"/>
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="right"><input style="width:100px" type="submit" value="Update"/>
					<a href="editproject.html"><button style="width:100px" type="button" value="Cancel">Cancel</button></a></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>