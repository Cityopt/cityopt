<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="com.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt show scenarios</title>

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
			<div style="overflow:scroll;height:700px;width:900px;overflow:auto">
			<table>
				<tr>
					<td>
						<h2>Show scenarios</h2>
						<table class="tablestyle" width="600" border="1">
							<col style="width:200px">	
							<col style="width:250px">
																												
							<tr height="20">
							    <th>Name</th>
							    <th>Description</th>
							</tr>
											
							<c:forEach items="${scenarios}" var="scenario">
							<tr>
								<td>${scenario.name}</td>
								<td>${scenario.description}</td>			
						   	</tr>
							</c:forEach>				
						</table>
					</td>
			    </tr>
		       	<tr>
			   		<td align="right">
			   			<a href="importdata.html"><button type="button">Back</button></a>
			   		</td>
			   	</tr>
		    </table>
			</div>
	    </td>
    </tr>
</table>	  
</body>
</html>