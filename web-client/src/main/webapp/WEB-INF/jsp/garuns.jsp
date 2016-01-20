<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt running genetic optimizations</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:800px;overflow:auto">
			<h2>Running genetic optimizations</h2>
			<table>
				<tr>
					<td>
						<table class="tablestyle" width="650">
							<col style="width: 100px">
							<col style="width: 150px">
							<col style="width: 150px">
							<col style="width: 150px">
							<col style="width: 100px">
						
							<tr height="20">
							    <th><spring:message code="id"/></th>
								<th>Started</th>
							    <th>Deadline</th>
							    <th>Status</th>
							    <th>Abort</th>
							</tr>
						
							<c:forEach items="${optRuns}" var="optRun">
							<tr>
								<td>${optRun.id}</td>
								<td>${optRun.started}</td>
								<td>${optRun.deadline}</td>
								<td>${optRun.status}</td>
								<td>
									<a onclick="return confirm('Are you sure you want to delete')" 
										href="abortgarun.html?id=${optRun.id}">
										<button align="right"  type="button" value="Abort">
										Abort</button>
									</a>
								</td>
						   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</div>
</body>
</html>