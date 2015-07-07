<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
								<td><spring:message code="project_name"/>:</td>
								<td><form:input type="text" path="name" style="width:250px"/></td>
							</tr>
							<tr>
								<!-- Location: -->						
								<td><spring:message code="location"/>:</td>
								<td><form:input type="text" path="location" style="width:250px"/></td>
							</tr>
							<tr>
								<!-- Design target: -->						
								<td><spring:message code="design_target"/>:</td>
								<td><form:input type="text" path="designtarget" style="width:250px"/></td>
							</tr>
							<tr>
								<!-- Description -->						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width:250px"></form:textarea></td>
							</tr>
							<tr>
								<td></td>
								<!-- Save project -->
								<td><input type="submit" value=<spring:message code="save_project"/> style="width:120px"></td>
							</tr>
							</form:form>							
							<form:form method="POST" action="uploadFile.html" enctype="multipart/form-data">
	        					<tr>
	        					<!-- Energy model to upload: -->
									<td><spring:message code="energy_model_to_upload"/>:</td>
									<td><input type="file" name="file"></td>
								</tr>
								<tr>
									<!-- Detail level -->
									<td><spring:message code="detail_level"/>:</td>
	        						<td>
	        							<input type="text" name="detailLevel"> 
	       							</td>
								</tr>
	       						<tr>	
	       							<td></td>
	        						<td>
	        						<!-- Load File -->
	        							<input type="submit" value="<spring:message code="load_file"/>">
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
								<td><spring:message code="input_parameters"/></td>
								
								<!-- Set -->
								<td><a href="projectparameters.html"><button type="button" style="width:120px">
								<spring:message code="set"/></button></a></td>
								
								<!-- Close project -->
								<td align="right"><a href="closeproject.html"><button type="button" style="width:120px">
								<spring:message code="close_project"/></button></a></td>
							</tr>
							<tr>
								<!-- Output variables-->
								<td><spring:message code="output_variables"/></td>
								
								<!-- Set -->
								<td><a href="outputvariables.html"><button type="button" style="width:120px">
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