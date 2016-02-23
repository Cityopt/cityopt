<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt Database optimization</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openInfoWindow() {
   		window.open("createoptimizationset_info.html",'Create optimization set info','width=600,height=800');
    }
</script>

</head>
<body>
<table cellpadding="0" cellspacing="0">
<tr>
	<td>
		<%@ include file="mainmenu.inc"%>
	</td>
	<td width="20"></td>
	<td valign="top">
		<form:form method="post" action="createoptimizationset.html" modelAttribute="openoptimizationset">
		<table>
			<col style="width: 400px;">
			<col style="width: 450px;">
			<h2 class="error">${error}</h2>
			<c:choose>
   				<c:when test="${success!=null && success==true}">
     			   <h2 class="successful">Optimization set created</h2>
     			   <c:set var="tooltip_next"><spring:message code="tooltip_next"/></c:set>
     			   <a href='editoptimizationset.html'><button style="width:100px" class="rewardGreen" type="button" value="Next" title="${tooltip_next}">
     			   <spring:message code="next"/></button></a>
     		</c:when>
           		<c:when test="${success!=null && success==false}">
           			   <h2 class="error">Name already exists</h2>
           		</c:when>            	
       		</c:choose>		
       							
			<!--Title Create database optimization set-->
			<tr>
				<td colspan="2"><h2><spring:message code="create_optimization_set"/></h2></td>
				<td align="right">
					<div class="round-button">
						<div class="round-button-circle">
							<a href="" onclick="openInfoWindow()">?</a>		
						</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<table>
						<col style="width: 80px;">
						<col style="width: 200px;">
						<col style="width: 80px;">
						<col style="width: 300px;">
						<col style="width: 175px;">
						<tr>
							<!-- Name -->
							<c:set var="tooltip_name"><spring:message code="tooltip_create_optimizationset_name"/></c:set>
							<td><spring:message code="name"/>*:</td>
							<td><form:input type="text" id="name" title="${tooltip_name}" path="name" style="width:200px"/></td>
							<!-- Description -->
							<c:set var="tooltip_description"><spring:message code="tooltip_create_optimizationset_description"/></c:set>
							<td><spring:message code="description"/>:</td>
							<td rowspan="2"><textarea id="description" title="${tooltip_description}" rows="2" style="width: 300px"></textarea></td>
							<!-- Create -->
							<c:set var="tooltip_create"><spring:message code="tooltip_create_optimizationset"/></c:set>
							<td align="right"><input type="submit" title="${tooltip_create}" value="Create" style="width: 100px"></td>
						</tr>
						<tr>
						<!-- Type -->
							<c:set var="tooltip_type"><spring:message code="tooltip_create_optimizationset_type"/></c:set>
							<td><spring:message code="type"/>:</td>
							<td>
								<select name="type" title="${tooltip_type}" id="type" size="1">
									<!-- option Database search -->
									<option value="1" selected><spring:message code="database_search"/></option>
									<!-- Genetic algorithm -->
									<option value="2"><spring:message code="genetic_algorithm"/></option>
								</select>
							</td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</form:form>
		</td>
	</tr>
</table>
</body>
</html>