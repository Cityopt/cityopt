<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="metric" type="com.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project metrics</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openInfoWindow() {
    	   window.open("createproject_info.html",'Info: Create Project','width=600,height=800');
    }
</script>
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="50"></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
			<table>
				<tr>
				<!-- Project metrics -->
					<td><h2><spring:message code="project_metrics"/></h2></td>
					
					<td align="right"><div class="round-button">
							<div class="round-button-circle" onclick="openInfoWindow()">
								<a>?</a>		
							</div> 
					</div></td>					
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="800px">
							<col style="width: 150px">
							<col style="width: 100px">
							<col style="width: 250px">
							<col style="width: 100px">
							<col style="width: 100px">
							<col style="width: 100px">

							<tr height="20">
							<!-- Name -->
							    <th><spring:message code="name"/></th>
							<!-- Id -->
							    <th><spring:message code="id"/></th>
						    <!-- Expressions -->
							    <th><spring:message code="expressions"/></th>
							<!-- Clone -->
							     <th><spring:message code="clone"/></th>
							<!-- Edit -->
							     <th><spring:message code="edit"/></th>
							<!-- Delete -->
							     <th><spring:message code="delete"/></th>
							</tr>
							
							<c:forEach items="${metrics}" var="metric">
							<tr>
								<td>${metric.name}</td>
						    	<td>${metric.metid}</td>
						    	<td>${metric.expression}</td>
								<td>
									<!-- Clone button -->
									<c:set var="tooltipclone"><spring:message code="tooltip_clone"/></c:set>
									<a href="<c:url value='metricdefinition.html?metricid=${metric.metid}&action=clone'/>">
										<button align="right" title="${tooltipclone}" type="button" value="Clone">
										<spring:message code="clone"/></button>
									</a>
								</td>
								<td>
									<!-- Edit button -->
									<c:set var="tooltipedit"><spring:message code="tooltip_edit"/></c:set>
									<a href="<c:url value='editmetric.html?metricid=${metric.metid}'/>">
										<button align="right" title="${tooltipedit}" type="button" value="Edit">
										<spring:message code="edit"/></button>
									</a>
								</td>
								<td>
									<!-- Delete button -->
									<c:set var="tooltipdelete"><spring:message code="tooltip_delete"/></c:set>
									<a href="<c:url value='metricdefinition.html?metricid=${metric.metid}&action=delete'/>" 
									onclick="return confirm('Are you sure you want to delete metric')">
										<button align="right" title="${tooltipdelete}" type="button" value="Delete">
										<spring:message code="delete"/></button>
									</a>
								</td>
						   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				<tr>
				<!--Create metric-->
					<td align="right">
						<a href="exportmetrics.html"><button type="button">Export metrics</button></a>
			   			<c:set var="tooltipcreatemetric"><spring:message code="tooltip_create_metric"/></c:set>
			   			<a href="createmetric.html?reset=true"><button title="${tooltipcreatemetric}" type="button">
			   			<spring:message code="create_metric"/></button></a>
			   		</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>