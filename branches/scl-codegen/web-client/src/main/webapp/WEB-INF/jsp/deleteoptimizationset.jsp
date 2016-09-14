<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="delete_optimization_set"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td><%@ include file="mainmenu.inc"%></td>
			<td valign="top">
				<div style="overflow: auto; height: 100%; width: 1100px; overflow: auto;">
				<table class="maintable">			
					<%@ include file="toprow.inc"%>
					<tr class="titlerow">
						<td class="spacecolumn"></td>
						<td>
							<table width="100%">
								<tr>
									<td>
	                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="delete_optimization_set"/>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="spacecolumn"></td>
						<td class="error">${error}</td>
					</tr>
					<tr>
						<td class="spacecolumn"></td>
						<td class="info">${info}</td>
					</tr>
					<tr height="20px">
						<td class="spacecolumn"></td>
						<td class="active"><spring:message code="optimization_sets" /></td>
					</tr>
					<tr>
						<td class="spacecolumn"></td>
						<td valign="top">
							<table class="tablestyle" width="500">
								<col style="width: 250px">
								<col style="width: 150px">
								<col style="width: 80px">
							
								<tr height="20">
									<!-- Name -->
								    <th><spring:message code="name"/></th>
								    <!-- Type -->
								    <th><spring:message code="type"/></th>
								    <!-- Open -->
								    <th><spring:message code="open"/></th>
								</tr>
								<!-- Tool tip -->
								<c:set var="delete_optimizationset"><spring:message code="tooltip_delete"/></c:set>
								<!-- Table elements -->
								<c:forEach items="${openoptimizationsets}" var="openoptimizationset">
								<tr>
									<td>${openoptimizationset.name}</td>
									<c:choose>
										<c:when test="${openoptimizationset.isDatabaseSearch()}">
											<td><spring:message code="database_optimization"/></td>
											<td>
												<a href="<c:url value='deleteoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=db'/>"
												onclick="return confirm('<spring:message code="confirm_optimizationset_deletion"/>')">
												<button align="right" title="${delete_optimizationset}" type="button" value="Delete"><spring:message code="delete"/></button>
											</a>
											</td>
										</c:when>
										<c:otherwise>
											<td><spring:message code="genetic_algorithm"/></td>
											<td>
											<!-- Delete -->
											<a href="<c:url value='deleteoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=ga'/>"
												onclick="return confirm('<spring:message code="confirm_optimizationset_deletion"/>')">
												<button align="right" title="${delete_optimizationset}"  type="button" value="Delete">
												<spring:message code="delete"/></button>
											</a>
											</td>
										</c:otherwise>
									</c:choose>
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
</body>
</html>