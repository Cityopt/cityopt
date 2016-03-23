<%--@elvariable id="newProject" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>CityOpt create project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<script>
    function openInfoWindow() {
    	   window.open("createproject_info.html",'Info: Create Project','width=600,height=600,scrollbars=yes');
    }
</script>

</head>
<body>
<%@ include file="mainmenu.inc"%>
   
<form:form method="post" action="createproject.html" modelAttribute="newProject">

<table>
	<tr>
		<td width=20></td>
		<td valign="top">
			<table style="width:900px" >			
			<tr>
				<td><h2 class="error">${error}</h2></td>
			</tr>
				<tr><td><h1><spring:message code="createproject"/></h1></td>
					<td align="right">
						<div class="round-button">
							<div class="round-button-circle">
								<a href="" onclick="openInfoWindow()">?</a>		
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<table>
							<tr class="project_name">								
								<td><label for="projectname"><spring:message code="project_name"/>*:</label></td>
								<c:set var="tooltip_name"><spring:message code="tooltip_create_project_name"/></c:set>
								<td><form:input type="text" path="name" title="${tooltip_name}" style="width:200px"/></td>
								<td><form:errors path="name" cssClass="error"/></td>																					
							</tr>
							<tr class="project_location">
								<td><label for="location"><spring:message code="location"/>:</label></td>
								<c:set var="tooltip_location"><spring:message code="tooltip_create_project_location"/></c:set>
								<td><form:input type="text" path="location" title="${tooltip_location}" style="width:200px"/></td>
							</tr>
							<tr class="project_design_target">												
								<td><label for="designtarget"><spring:message code="design_target"/>:</label></td>
								<c:set var="tooltip_desingtarget"><spring:message code="tooltip_design_target"/></c:set>
								<td><form:input type="text" path="designtarget" title="${tooltip_desingtarget}" style="width:200px"/></td>
							</tr>
							<tr class="project_description">					
								<td><label for="description"><spring:message code="description"/>:</label></td>
								<c:set var="tooltip_description"><spring:message code="tooltip_description"/></c:set>
								<td>
									<form:textarea type="text" rows="3" path="description" title="${tooltip_desingtarget}" style="width:200px"/>
								</td>
							</tr>					
							<tr class="create_project" height=10px>						
							<td align="right">							
								<c:set var="tooltip_create_project"><spring:message code="tooltip_create_project"/></c:set>
								<td align="right"><input type="submit" title="${tooltip_create_project}" 
								value="<spring:message code="createproject"/>" style="width:120px"></td>							
							</tr>							
						</table>						
							<!-- Success // failure message -->
				<c:choose>
          			<c:when test="${success!=null && success==true}">
            			<h2 class="successful"> <spring:message code="projectSuccess"/></h2>
            			<c:set var="tooltip_next"><spring:message code="tooltip_next"/></c:set>
            			<input type="submit" name="nextpage" style="width:100px" class="big" value="Next" title="${tooltip_next}">
            		</c:when>
            		<c:when test="${success!=null && success==false}">
            			<h2 class="error"> <spring:message code="projectExists" /> </h2>
            		</c:when>            	
        		</c:choose>						
				
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>