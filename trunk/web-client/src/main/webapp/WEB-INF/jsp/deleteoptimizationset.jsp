<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
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
<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td><%@ include file="mainmenu.inc"%></td>
			<td width="30"></td>
			<td valign="top">
			<div style="overflow:scroll;height:400px;width:600px;overflow:auto">
			<form method="get" action="OptimizationController">
			<h2>Delete optimization set</h2>
			<table class="tablestyle" width="500" border="1">
				<col style="width: 250px">
				<col style="width: 150px">
				<col style="width: 80px">
			
				<tr height="20">
				    <th>Name</th>
				    <th>Type</th>
				    <th>Open</th>
				</tr>
			
				<c:forEach items="${openoptimizationsets}" var="openoptimizationset">
				<tr>
					<td>${openoptimizationset.name}</td>
						<c:choose>
							<c:when test="${openoptimizationset.isDatabaseSearch()}">
								<td>Database search</td>
								<td>
								<a href="<c:url value='deleteoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=db'/>">
									<button align="right"  type="button" value="Delete">Delete</button>
								</a>
								</td>
							</c:when>
							<c:otherwise>
								<td>Genetic algorithm</td>
								<td>
								<a href="<c:url value='deleteoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=ga'/>">
									<button align="right"  type="button" value="Delete">Delete</button>
								</a>
								</td>
							</c:otherwise>
						</c:choose>
			   	</tr>
				</c:forEach>
			</table>
			</form>
			</div>
		</td>
	</tr>
</table>
</body>
</html>