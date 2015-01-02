<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Output variables</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td>
			<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
			<table>
				<col style="width:40px">
				<col style="width:30px">
				<col style="width:850px">	
				<tr>
					<td>
						<b>External parameters</b>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="800">
						
							<tr height="20">
							    <th>Name</th>
							    <th>Id</th>
							    <th>Default value</th>
							</tr>
							
							<!--forEach items="{extparams}" var="extParam">
							<tr>
								<td>${extParam.name}</td>
						    	<td>${extParam.extparamid}</td>
						    	<td>${extParam.defaultvalue}</td>
								<td>
									<a href="<c:url value='editextparam.html?extparamid=${extParam.extparamid}'/>">
										<button align="right" type="button" value="Edit">Edit</button>
									</a>
								</td>
								<td>Delete</td>
						   	</tr>
							</forEach>-->
						</table>
						
						<table width="100%">
						
							<tr height="30">
								<td></td>
							</tr>
							<tr>
								<td align="right">
									<a href="editscenario.html"><button type="button">Close</button></a>
							    </td>
							</tr>
							      
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>