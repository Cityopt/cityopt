<%--@elvariable id="unit" type="com.cityopt.DTO.UnitDTO"--%>
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

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:600px;overflow:auto">
			<table>
				<tr>
					<td>			
						<h2>Units</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="400px">
							<col style="width:150px">	
							<col style="width:150px">	
							<col style="width:100px">	
							
						<tr height="20">
						    <th>Name</th>
						    <th>Data reliability</th>
						    <th>Type</th>
						</tr>
						
						<tr>
							<td>x</td>
							<td>x</td>
							<td>x</td>
						</tr>
						</table>
					</td>
					<td width="20">
					</td>
					<td>
						<a href=""><button>Create unit</button></a><br>
						<a href=""><button>Delete unit</button></a>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>