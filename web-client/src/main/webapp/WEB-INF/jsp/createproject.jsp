<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>

<form:form method="post" action="createproject.html" modelAttribute="project">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table style="width:900px">			
				<!-- create project -->
				<tr><td><h2><spring:message code="createproject"/></h2></td></tr>	
				<tr>
					<td>
						<table>
							<tr>
								<!--Project name:-->
								<td><spring:message code="project_name"/>:</td>
								<td><form:input type="text" path="name"/></td>
								<td><form:errors path="name" cssClass="error"/></td>
							</tr>
							<tr>
								<!--Location:-->						
								<td><spring:message code="location"/>:</td>
								<td><form:input type="text" path="location"/></td>
								<td><form:errors path="location" cssClass="error"/></td>
							</tr>
							<tr>
								<!--Design target:-->						
								<td><spring:message code="design_target"/>:</td>
								<td><form:input type="text" path="designtarget"/></td>
								<td><form:errors path="designtarget" cssClass="error"/></td>
							</tr>
							<tr>
								<!--Description -->						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea type="text" rows="3" path="description"></form:textarea></td>
							    <td><form:errors path="description" cssClass="error"/></td>
							</tr>
						</table>
				
				 <c:choose>
          			  <c:when test="${success!=null && success==true}">
            			   <h2 class="successful"> <spring:message code="projectSuccess" /> </h2>
            		</c:when>
            		<c:when test="${success!=null && success==false}">
            			   <h2 class="error"> <p><spring:message code="projectExists" /> </p></h2>
            		</c:when>            	
        		</c:choose>			
				
						
					</td>
					<td align="right">
						<img src="assets/img/test_map.jpg"/>
					</td>
				</tr>
				<tr>
					<!--Upload diagram-->
					<td colspan="2" align="Right">
						<input type="button" id="uploadDiagram" value=<spring:message code="upload_diagram"/> style="width:120px"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<col style="width:600px">
							<tr>
								<td></td>
								<td></td>
								<!-- Create project -->
								<td align="right"><input type="submit" value="<spring:message code="createproject"/>" style="width:120px"></td>
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