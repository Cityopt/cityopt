
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
</head>
<body>
<%@ include file="mainmenu.inc"%>

<form:form method="post" action="createproject.html" modelAttribute="newProject">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table class="ProjectCreationForm" style="width:900px" >			
				<!-- create project -->
				<tr><td><h2><spring:message code="createproject"/></h2></td></tr>	
				<tr valing="top">
					<td valing="top">
						<table >
							<tr>
								<!--Project name:-->
								<td><spring:message code="project_name"/>:</td>
								<c:set var="tooltip_name"><spring:message code="tooltip_create_project_name"/></c:set>
								<td><form:input type="text" path="name" title="${tooltip_name}"/></td>
								<td><form:errors path="name" cssClass="error"/></td>															
							</tr>
							<tr>
								<!--Location:-->						
								<td><spring:message code="location"/>:</td>
								<c:set var="tooltip_location"><spring:message code="tooltip_create_project_location"/></c:set>
								<td><form:input type="text" path="location" title="${tooltip_location}"/></td>
								<td><form:errors path="location" cssClass="error"/></td>
							</tr>
							<tr>
								<!--Design target:-->						
								<td><spring:message code="design_target"/>:</td>
								<c:set var="tooltip_desingtarget"><spring:message code="tooltip_design_target"/></c:set>
								<td><form:input type="text" path="designtarget" title="${tooltip_desingtarget}"/></td>
								<td><form:errors path="designtarget" cssClass="error"/></td>
							</tr>
							<tr>
								<!--Description -->						
								<td><spring:message code="description"/>:</td>
								<c:set var="tooltip_description"><spring:message code="tooltip_description"/></c:set>
								<td><form:textarea type="text" rows="3" path="description" title="${tooltip_desingtarget}"></form:textarea></td>
							    <td><form:errors path="description" cssClass="error"/></td>
							</tr>					
							<tr height=10px></tr>						
							<td align="right">
							<!-- Create project -->
								<c:set var="tooltip_create_project"><spring:message code="tooltip_create_project"/></c:set>
								<td align="right"><input type="submit" title="${tooltip_create_project}" 
								value="<spring:message code="createproject"/>" style="width:120px"></td>
							</td>							
						</table>						
							<!-- Success // failure message -->
				 <c:choose>
          			  <c:when test="${success!=null && success==true}">
            			   <h2 class="successful"> <spring:message code="projectSuccess"/></h2>
            			   <c:set var="tooltip_next"><spring:message code="tooltip_next"/></c:set>
            			   <a href="editproject.html"><button style="width:100px" type="button" value="Next" title="${tooltip_next}">
            			   <spring:message code="next"/></button></a>
            		</c:when>
            		<c:when test="${success!=null && success==false}">
            			   <h2 class="error"> <spring:message code="projectExists" /> </h2>
            		</c:when>            	
        		</c:choose>						
				
					</td><td align="right"><img src="assets/img/test_map.jpg"/></td>				
				
				<tr>
					<td colspan="2">
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<col style="width:500px">
							<tr>
								<td></td>							
								<!--Upload diagram-->
								<td colspan="2" align="Right"><input type="button" id="uploadDiagram" 
								value=<spring:message code="upload_diagram"/> style="width:100px"/></td>
								</tr>				
								<!-- Create project -->
								<!-- 
								<c:set var="tooltip_create_project"><spring:message code="tooltip_create_project"/></c:set>
								<td align="right"><input type="submit" title="${tooltip_create_project}" value="<spring:message code="createproject"/>" style="width:120px"></td>
								-->
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>