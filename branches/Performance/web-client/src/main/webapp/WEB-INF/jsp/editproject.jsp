<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="edit_project"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openModelInfoWindow() {
   		window.open("simmodelinfo.html",'<spring:message code="model_info"/>','width=1200,height=600,scrollbars=yes');
    }
    function openInfoWindow() {
 		window.open("editproject_info.html",'<spring:message code="edit_project_info"/>','width=600,height=600,scrollbars=yes');
 	}
</script>
</head>
<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
			<table class="maintable" style="width:1000px">
				<col style="width:500px">
				<col style="width:500px">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="editproject"/></td>
								<td align="left" width="40">
									<div class="round-button">
										<div class="round-button-circle">
											<a href="" onclick="openInfoWindow()">?</a>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="error">${error}</td>
				</tr>
				<c:if test="${success != null}">
 					<tr height="20">
 						<td>
 							<table>
 								<tr>
 									<td class="spacecolumn"></td>
 									<c:if test="${success == true}">
										<td class="successful">
			 								<spring:message code="simulation_uploaded"/>
		 								</td>
	 								</c:if>
	 								<c:if test="${success == false}">
	 								 	<td class="error">
											<h2 class="error"><spring:message code="simulation_upload_failed"/></h2>
 										</td>
	 								</c:if>
 								</tr>
							</table>
 						</td>
					</tr>
				</c:if>
 				
				<tr height="400px">
					<td valign="top">
						<table>
							<tr>
								<td class="spacecolumn"></td>
								<td>
									<form:form method="post" action="editproject.html?action=update" modelAttribute="project">
									<table>
										<tr>
											<c:set var="tooltip_name"><spring:message code="tooltip_edit_project_name"/></c:set>
											<td class="infosmall"><spring:message code="project_name"/>*</td>
										</tr>
										<tr>
											<td><form:input type="text" path="name" title="${tooltip_name}" style="width:300px"/></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>
											<c:set var="tooltip_location"><spring:message code="tooltip_create_project_location"/></c:set>						
											<td class="infosmall"><spring:message code="location"/></td>
										</tr>
										<tr>
											<td><form:input type="text" path="location" title="${tooltip_location}" style="width:300px"/></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>
											<c:set var="tooltip_designtarget"><spring:message code="tooltip_edit_project_designtarget"/></c:set>						
											<td class="infosmall"><spring:message code="design_target"/></td>
										</tr>
										<tr>
											<td><form:input type="text" path="designtarget" title="${tooltip_designtarget}" style="width:300px"/></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>
											<!-- Description -->
											<c:set var="tooltip_description"><spring:message code="tooltip_edit_project_description"/></c:set>						
											<td class="infosmall"><spring:message code="description"/></td>
										</tr>
										<tr>
											<td><form:textarea title="${tooltip_description}" type="text" rows="3" path="description"  style="width:300px"></form:textarea></td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>
											<!-- Save project -->
											<c:set var="tooltip_save"><spring:message code="tooltip_edit_project_save"/></c:set>
											<td><input type="submit" title="${tooltip_save}" value=<spring:message code="save_project"/> style="width:120px"></td>
										</tr>
									</table>
									</form:form>
									<table>
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
										<tr height=30></tr>	
										<tr>
											<td><spring:message code="input_parameters"/></td>
										
											<c:set var="tooltip_set_input_parameter"><spring:message code="tooltip_edit_project_set_input_parameter"/></c:set>
											<td><a href="projectparameters.html"><button type="button" title="${tooltip_set_input_parameter}" style="width:120px">
											<spring:message code="set"/></button></a></td>
										</tr>
										<tr height=3></tr>	
										<tr>
											<td><spring:message code="output_variables"/></td>
										
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
							</tr>
						</table>	
					</td>
					<td valign="top" align="right">
						<img style="width:500px" src="overview.png" border="1"/>
					</td>
				</tr>
				<tr height="20">
					<td valign="top">
						<c:set var="tooltip_closeproject"><spring:message code="tooltip_edit_project_close_project"/></c:set>
						<td align="right"><a href="closeproject.html"><button type="button"  title="${tooltip_closeproject}" style="width:120px"
						 onclick="return confirm('<spring:message code="confirm_closing_project"/>')">								
						<spring:message code="close_project"/></button></a>
					</td>
				</tr>									
				<tr></tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>