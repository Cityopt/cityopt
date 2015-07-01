<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt import data</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:1100px;overflow:auto">
			<table>
				<col style="width:400px">	
				<col style="width:400px">	
			
				<tr>
					<td>
						<!-- Import data -->
						<h2><spring:message code="import_data"/></h2>
					</td>
					<td align="right">						
						<!-- Download project templates & download button -->
						<p><spring:message code="download_project_templates"/><a href="">						
						<button><spring:message code="download"/></button></a></p>
						
						<!-- Download scenario templates & download button -->
						<p><spring:message code="download_scenario_templates"/><a href="">
						<button><spring:message code="download"/></button></a></p>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<!-- Project name: -->
								<td><spring:message code="project_name"/>:</td>
								<td>
									<c:if test="project != null">
										${project.name}
									</c:if>
								</td>
							</tr>
							<tr>
								<!-- Location: -->						
								<td><spring:message code="location"/>:</td>
								<td>
									<c:if test="project != null">
										${project.location}
									</c:if>
								</td>
							</tr>
							<tr>
								<!-- Design target: -->						
								<td><spring:message code="design_target"/>t:</td>
								<td></td>
							</tr>
							<tr>
								<!-- Description: -->						
								<td><spring:message code="description"/>:</td>
								<td>
									<c:if test="project != null">
										${project.description}
									</c:if>
								</td>
							</tr>
							<tr>
								<!-- Energy model -->						
								<td><spring:message code="energy_model"/>:</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<td>
								<!-- Parameter level: -->
									<spring:message code="parameter_level"/>:
								</td>
								<td>
							 		<select name="parameterLevel">
									  	<option value="1">1</option>
									  	<option value="2">2</option>
									  	<option value="3">3</option>
									  	<option value="4">4</option>
									</select>
								</td> 
							</tr>
							<tr>						
								<td></td>
								<td>
									<!-- Upload button -->
									<a href="uploaddiagram.html"><button type="button">
									<spring:message code="upload"/></button></a>
								</td>
							</tr>
							<tr>
								<td>
									<br>
									<!-- Import Project data -->
									<b><spring:message code="import_project_data"/></b>
								</td>
							</tr>
							<form:form method="POST" action="importcomponents.html" enctype="multipart/form-data">
	        					<tr>
	        						<!--Components-->
									<td><spring:message code="components"/></td>
									<td><input id="uploadFile" name="uploadFile" type="file"/></td>
								</tr>
								<tr>	
	       							<td></td>
	        						<td>
	        							<input type="submit" value="Load component file">
	       							</td>
	   							</tr>	
    						</form:form>
							<tr>
								<!-- Input parameters -->						
								<td><spring:message code="input_parameters"/></td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<!-- Output variables -->						
								<td><spring:message code="output_variables"/></td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<!-- External parameter sets -->						
								<td><spring:message code="external_parameter_sets"/></td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<!-- Show project data & Import buttons -->		
								<td><a href="projectdata.html"><button>
								<spring:message code="show_project_data"/></button></a></td>
								<td align="right"><a href=""><button>
								<spring:message code="import"/></button></a></td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table>
							<tr>
								<td>
									<img src="assets/img/test_map.jpg"/>
								</td>
							</tr>
							<tr align="right">
								<!-- Upload diagram -->
								<td><a href="uploaddiagram.html"><button type="button">
								<spring:message code="upload_diagram"/></button></a></td>
							</tr>
							<tr>
								<td>
								<!-- Import scenarios -->					
									<b><spring:message code="import_scenarios"/></b>
								</td>
							</tr>
							<tr>
								<!-- Import scenarios -->						
								<td><spring:message code="import_scenarios"/><a href=""><button>
								<spring:message code="import"/></button></a></td>
							</tr>
							<tr>						
								<td><a href="showscenarios.html"><button>
								<spring:message code="show_scenarios"/></button></a></td>
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