<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- CityObt edit project -->
<title>CityOpt <spring:message code="editproject"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:1000px;overflow:auto">
			<table style="width:900px">
				<tr>
					<td>
						<h2 class="error">${errorMessage}</h2>
						<!-- EditProject -->
						<h2><spring:message code="editproject"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<form:form method="post" action="editproject.html?action=update" modelAttribute="project">
							<tr>
								<!--Project name:-->
								<c:set var="tooltip_name"><spring:message code="tooltip_edit_project_name"/></c:set>
								<td><spring:message code="project_name"/>:</td>
								<td><form:input type="text" path="name" title="${tooltip_name}" style="width:250px"/></td>
							</tr>
							<tr>
							
								<!-- Location: -->
								<c:set var="tooltip_location"><spring:message code="tooltip_create_project_location"/></c:set>						
								<td><spring:message code="location"/>:</td>
								<td><form:input type="text" path="location" title="${tooltip_location}" style="width:250px"/></td>
							</tr>
							<tr>
								<!-- Design target: -->
								<c:set var="tooltip_designtarget"><spring:message code="tooltip_edit_project_designtarget"/></c:set>						
								<td><spring:message code="design_target"/>:</td>
								<td><form:input type="text" path="designtarget" title="${tooltip_designtarget}" style="width:250px"/></td>
							</tr>
							<tr>
								<!-- Description -->
								<c:set var="tooltip_description"><spring:message code="tooltip_edit_project_description"/></c:set>						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea title="${tooltip_description}" type="text" rows="3" path="description"  style="width:250px"></form:textarea></td>
							</tr>
							<tr>
								<td></td>
								<!-- Save project -->
								<c:set var="tooltip_save"><spring:message code="tooltip_edit_project_save"/></c:set>
								<td><input type="submit" title="${tooltip_save}" value=<spring:message code="save_project"/> style="width:120px"></td>
							</tr>
							</form:form>							
							<form:form method="POST" action="uploadFile.html?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
	        					<tr>
	        					<!-- Energy model to upload: -->
	        						<c:set var="tooltip_energymodel"><spring:message code="tooltip_edit_project_energymodel"/></c:set>
									<td><spring:message code="energy_model_to_upload"/>:</td>
									<td><input type="file" name="file" title="${tooltip_energymodel}"></td>
								</tr>
	       						<tr>	
	       							<td></td>
	        						<td>
	        						<!-- Load File -->
	        						
	        							<c:set var="tip_load_energymodel"><spring:message code="tooltip_edit_project_load_energymodel"/></c:set>
	        							<input type="submit" title="${tip_load_energymodel}" value="<spring:message code="load_file"/>">
	        							
	        							 <c:choose>
	        							 	<c:when test="${success!=null && success==true}">
	        							 	<br><br><spring:message code="simulation_uploaded"/>
	        							 	</c:when>
	        							 	<c:when test="${success!=null && success==false}">
	        							 	<br><br><h2 class="error"><spring:message code="simulation_upload_failed"/></h2>
	        							 	</c:when>
	        							 </c:choose>
	        							     	
	        							
	       							</td>
	   							</tr>	
   							</form:form>
						</table>
					</td>
					<td align="right">
						<img src="assets/img/test_map.jpg"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<col style="width:600px">
							<tr>
								<!--Input parameters -->
								<td><spring:message code="input_and_external_parameters"/></td>
								
								<!-- Set -->
								<c:set var="tooltip_set_input_parameter"><spring:message code="tooltip_edit_project_set_input_parameter"/></c:set>
								<td><a href="projectparameters.html"><button type="button" title="${tooltip_set_input_parameter}" style="width:120px">
								<spring:message code="set"/></button></a></td>
								
								<!-- Close project -->
								<c:set var="tooltip_closeproject"><spring:message code="tooltip_edit_project_close_project"/></c:set>
								<td align="right"><a href="closeproject.html"><button type="button"  title="${tooltip_closeproject}" style="width:120px"
								 onclick="return confirm('<spring:message code="confirm_closing_project"/>')">								
								<spring:message code="close_project"/></button></a></td>
							</tr>									
							<tr>
								<!-- Output variables-->
								<td><spring:message code="output_variables"/></td>
								
								<!-- Set -->
								<c:set var="tooltip_outputparameters"><spring:message code="tooltip_edit_project_set_output_parameters"/></c:set>
								<td><a href="outputvariables.html"><button type="button" title="${tooltip_outputparameters}" style="width:120px">
								<spring:message code="set"/></button></a></td>
								<td align="right"></td>
							</tr>
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