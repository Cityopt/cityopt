<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
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
						<h2>External parameters</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="450">
							<col style="width:200px">
							<col style="width:100px">
							<col style="width:100px">
							<col style="width:50px">
									
							<tr height="20">
							    <th>Name</th>
							    <th>Id</th>
								<th>Value</th>
							    <th>Default value</th>
							    <th>Edit</th>
							</tr>
							
							<c:forEach items="${extParamVals}" var="extParamVal">
							<tr>
								<td>${extParamVal.extparam.name}</td>
						    	<td>${extParamVal.extparamvalid}</td>
						    	<td>${extParamVal.value}</td>
						    	<td>${extParamVal.extparam.defaultvalue}</td>
						    	<td>
									<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParam.extparamid}'/>">
										<button align="right" type="button" value="Edit">Edit</button>
									</a>
								</td>
						   	</tr>
							</c:forEach>
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