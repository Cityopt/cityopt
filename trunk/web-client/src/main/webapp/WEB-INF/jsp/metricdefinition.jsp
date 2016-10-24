<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="metric" type="com.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="metrics"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openInfoWindow() {
    	   window.open("metric_info.html",'<spring:message code="metrics"/> info','width=600,height=800');
    }
</script>
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td valign="top">
			<div style="overflow:scroll;width:820px;overflow:auto">
			<table class="maintable">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="project_metrics_small"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height="20"></tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="active"><spring:message code="metrics"/>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table class="tablestyle" width="760px">
							<col style="width: 100px">
							<col style="width: 300px">
							<col style="width: 90px">
							<col style="width: 90px">
							<col style="width: 90px">
							<col style="width: 90px">

							<tr height="20">
							<!-- Name -->
							    <th><spring:message code="name"/></th>
						    <!-- Expressions -->
							    <th><spring:message code="expressions"/></th>
						    <!-- Unit -->
							    <th><spring:message code="unit"/></th>
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
						    	<td>${metric.expression}</td>
						    	<td>${metric.unit.name}</td>
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
									onclick="return confirm('<spring:message code="confirm_delete" />')">
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
					<td class="spacecolumn"></td>
					<td align="left" valign="top">
						<table style="width: 760px">
							<tr>
								<td align="right">
									<a href="exportmetrics.html">
										<button type="button"><spring:message code="export_metrics"/></button>
									</a>
						   			<c:set var="tooltipcreatemetric"><spring:message code="tooltip_create_metric"/></c:set>
						   			<a href="createmetric.html?reset=true"><button title="${tooltipcreatemetric}" type="button">
						   				<spring:message code="create_metric"/></button>
					   				</a>
				   				</td>
			   				</tr>
		   				</table>
			   		</td>
				</tr>
				<tr height="100%"><td></td></tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>