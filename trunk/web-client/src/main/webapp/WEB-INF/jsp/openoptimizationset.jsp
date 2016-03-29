<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="open_optimization_set"/></title>
<script>
    function openInfoWindow() {
    	   window.open("createproject_info.html",'<spring:message code="open_optimization_set"/> info','width=600,height=800');
    }
</script>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:600px;overflow:auto">
			
			<!-- Open optimization set -->
			<h2><spring:message code="open_optimization_set"/></h2>
			
			
			<table class="tablestyle" width="500" border="1">
				<col style="width: 250px">
				<col style="width: 150px">
				<col style="width: 80px">
			
				<tr height="20">
					<!-- name -->
				    <th><spring:message code="name"/></th>
				    <!-- type -->
				    <th><spring:message code="type"/></th>
				    <!-- open -->
				    <th><spring:message code="open"/></th>
				    <!-- clone -->
				    <th><spring:message code="clone"/></th>
				</tr>
				<!-- tool tips -->
				<c:set var="open_optimizationset"><spring:message code="tooltip_open_optimizationset"/></c:set>
				<c:set var="clone_optimizationset"><spring:message code="tooltip_clone"/></c:set>
				<!-- Tables -->
				<c:forEach items="${openoptimizationsets}" var="openoptimizationset">
				<tr>
					<td>${openoptimizationset.name}</td>
						<c:choose>
							<c:when test="${openoptimizationset.isDatabaseSearch()}">
								<td>Database search</td>
								<td>
								<a href="<c:url value='openoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=db'/>">
									<button align="right" title="${open_optimizationset}"  type="button" value="Open"><spring:message code="open"/></button>
								</a>
								</td>
									<td><a
											href="<c:url value='cloneoptimizer.html?optimizerid=${openoptimizationset.id}&optsettype=db'/>">
												<button align="right" title="${clone_optimizationset}" type="button" value="Clone"><spring:message code="clone"/></button>
									</a></td>

							</c:when>
							<c:otherwise>
								<td><spring:message code="genetic_algorithm"/></td>
								<td>
								<a href="<c:url value='openoptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=ga'/>">
									<button align="right" title="${open_optimizationset}"  type="button" value="Open"><spring:message code="open"/></button>
								</a></td>
								<td><a	href="<c:url value='cloneoptimizer.html?optimizerid=${openoptimizationset.id}&optsettype=ga'/>">
												<button align="right" title="${clone_optimizationset}" type="button" value="Clone"><spring:message code="clone"/></button>
									</a></td>						
								
							</c:otherwise>
						</c:choose>
			   	</tr>
				</c:forEach>
			</table>
			<element><element><h2 class="successful"></element></element>${successful}</h2><element>			
			</div>
		</td>
	</tr>
</table>
</body>
</html>