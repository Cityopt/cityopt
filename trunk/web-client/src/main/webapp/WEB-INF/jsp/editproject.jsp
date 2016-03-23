<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="showInfo" type="boolean"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- CityOpt edit project -->
<title>CityOpt <spring:message code="editproject"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openModelInfoWindow() {
    	   window.open("infopage.html",'Model info','width=600,height=600,scrollbars=yes');
    }
    function openInfoWindow() {
 	   window.open("editproject_info.html",'Edit project info','width=600,height=600,scrollbars=yes');
 	}
</script>
</head>
<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
			<table style="width:1000px">
				<col style="width:500px">
				<col style="width:500px">
			
				<tr>
					<td>
						<h2 class="error">${error}</h2>
						<!-- EditProject -->
						<h1><spring:message code="editproject"/></h1>
					</td>
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
							<form:form method="post" action="editproject.html?action=update" modelAttribute="project">
							<tr>
								<!--Project name:-->
								<c:set var="tooltip_name"><spring:message code="tooltip_edit_project_name"/></c:set>
								<td><spring:message code="project_name"/>*:</td>
								<td><form:input type="text" path="name" title="${tooltip_name}" style="width:300px"/></td>
							</tr>
							<tr>
							
								<!-- Location: -->
								<c:set var="tooltip_location"><spring:message code="tooltip_create_project_location"/></c:set>						
								<td><spring:message code="location"/>:</td>
								<td><form:input type="text" path="location" title="${tooltip_location}" style="width:300px"/></td>
							</tr>
							<tr>
								<!-- Design target: -->
								<c:set var="tooltip_designtarget"><spring:message code="tooltip_edit_project_designtarget"/></c:set>						
								<td><spring:message code="design_target"/>:</td>
								<td><form:input type="text" path="designtarget" title="${tooltip_designtarget}" style="width:300px"/></td>
							</tr>
							<tr>
								<!-- Description -->
								<c:set var="tooltip_description"><spring:message code="tooltip_edit_project_description"/></c:set>						
								<td><spring:message code="description"/>:</td>
								<td><form:textarea title="${tooltip_description}" type="text" rows="3" path="description"  style="width:300px"></form:textarea></td>
							</tr>
							<tr>
								<td></td>								
								<!-- Save project -->
								<c:set var="tooltip_save"><spring:message code="tooltip_edit_project_save"/></c:set>
								<td><input type="submit" title="${tooltip_save}" value=<spring:message code="save_project"/> style="width:120px"></td>
							</tr>
							</form:form>
							<tr height=30></tr>	
							<tr>
								<td><spring:message code="loaded_energy_model"/>:</td>
								<td>${loadedEnergyModel}</td>
							</tr>
							<c:choose>
								<c:when test="${showInfo}">
									<tr>
										<td></td>
										<td>
												<button type="button" onmousedown="openModelInfoWindow()">
													<spring:message code="show_model_info"/>		
												</button> 
										</td>
									</tr>
								</c:when>
								<c:otherwise>									
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
											</td>
										</tr>
									</form:form>
								</c:otherwise>
							</c:choose>
								<tr>
									<td></td>
									<td>
												        							
	        							 <c:choose>
	        							 	<c:when test="${success!=null && success==true}">
	        							 		<spring:message code="simulation_uploaded"/>
	        							 	</c:when>
	        							 	<c:when test="${success!=null && success==false}">
	        							 		<h2 class="error"><spring:message code="simulation_upload_failed"/></h2>
	        							 	</c:when>
	        							 </c:choose>
	       							</td>
	   							</tr>	
								<tr height=30></tr>	
								<tr>
									<!--Input parameters -->
									<td><spring:message code="input_parameters"/></td>
								
									<!-- Set -->
									<c:set var="tooltip_set_input_parameter"><spring:message code="tooltip_edit_project_set_input_parameter"/></c:set>
									<td><a href="projectparameters.html"><button type="button" title="${tooltip_set_input_parameter}" style="width:120px">
									<spring:message code="set"/></button></a></td>
								</tr>
								<tr height=3></tr>	
								<tr>
									<!-- Output variables-->
									<td><spring:message code="output_variables"/></td>
								
									<!-- Set -->
									<c:set var="tooltip_outputparameters"><spring:message code="tooltip_edit_project_set_output_parameters"/></c:set>
									<td>
										<a href="outputvariables.html"><button type="button" title="${tooltip_outputparameters}" style="width:120px">
										<spring:message code="set"/></button></a>
									</td>
								</tr>
								<tr height=3></tr>	
								<tr>
									<td><spring:message code="external_parameters"/></td>
								
									<td><a href="extparams.html"><button type="button" title="" style="width:120px">
									<spring:message code="set"/></button></a></td>
								</tr>
						</table>	
					</td>
					<td align="right">
						<img style="width:500px" src="overview.png" border="1"/>
					</td>
				</tr>
				<tr>
					<td>
						<!-- Close project -->
						<c:set var="tooltip_closeproject"><spring:message code="tooltip_edit_project_close_project"/></c:set>
						<td align="right"><a href="closeproject.html"><button type="button"  title="${tooltip_closeproject}" style="width:120px"
						 onclick="return confirm('<spring:message code="confirm_closing_project"/>')">								
						<spring:message code="close_project"/></button></a>
					</td>
				</tr>									
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>