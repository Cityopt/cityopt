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
			<div style="overflow:scroll;height:800px;width:1200px;overflow:auto">
			<table>
				<col style="width:500px">	
				<col style="width:700px">	
			
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
					<td valign="top">
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
							
							<form:form method="POST" action="uploadFile.html?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
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
							
							<tr>
								<td>
									<br>
									<!-- Import project data -->
									<b><spring:message code="import_project_data"/></b>
								</td>
							</tr>
							<form:form method="POST" action="importstructurefile.html" enctype="multipart/form-data">
	        					<tr>
	        						<!-- Import project file (CSV) -->
	        						<td><spring:message code="import_project_file_CSV"/></td>
									<td><input id="file" name="file" type="file"/></td>
								</tr>
								<tr>	
	       							<td></td>
	        						<td>
	        							<!--Import file-->
	        							<input type="submit" value="<spring:message code="import_file"/>">
	       							</td>
	   							</tr>	
    						</form:form>
        					<form:form method="POST" action="importextparamsets.html" enctype="multipart/form-data">
	        					<tr>
	        						<td></td>
									<td>File</td>
									<td>Time series</td>
								</tr>
								<tr>
	        						<!-- Import external parameter sets (CSV) -->
	        						<td><spring:message code="import_external_parameter_sets_CSV"/></td>
									<td><input id="file" name="file" type="file"/></td>
									<td><input id="fileTimeSeries" name="Time series file" type="file"/></td>
								</tr>
	        					<tr>	
	       							<td></td>
	        						<td>
	        							<input type="submit" value="Import files">
	       							</td>
	   							</tr>	
    						</form:form>
    						<tr>
								<!-- Show project data & Import buttons -->		
								<td>
									<a href="projectdata.html">
										<button><spring:message code="show_project_data"/></button>
									</a>
								</td>
								<td align="right"></td>
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
								<td><!-- href="uploaddiagram.html"><button type="button">
								<spring:message code="upload_diagram"/></button></a>--></td>
							</tr>
							<tr>
								<td>
									<table>
										<col style="width:150px">	
										<col style="width:200px">	
										<col style="width:200px">	
										<tr>
											<td>
											<!-- Import scenarios -->					
												<b><spring:message code="import_scenarios"/></b>
											</td>
											<td>Scenario file</td>
											<td>Time series file</td>
										</tr>
										<form:form method="POST" action="importscenarios.html" enctype="multipart/form-data">
				        					<tr>
				        						<td><spring:message code="import_scenarios"/> (CSV)</td>
												<td><input id="file" name="file" type="file"/></td>
												<td><input id="timeSeriesFile1" name="timeSeriesFile1" type="file"/></td>
											</tr>
				        					<tr>
				        						<td></td>
												<td></td>
												<td><input id="timeSeriesFile2" name="timeSeriesFile2" type="file"/></td>
											</tr>
				        					<tr>
				        						<td></td>
												<td></td>
												<td><input id="timeSeriesFile3" name="timeSeriesFile3" type="file"/></td>
											</tr>
				        					<tr>
				        						<td></td>
												<td></td>
												<td><input id="timeSeriesFile4" name="timeSeriesFile4" type="file"/></td>
											</tr>
				        					<tr>
				        						<td></td>
												<td></td>
												<td><input id="timeSeriesFile5" name="timeSeriesFile5" type="file"/></td>
											</tr>
											<tr>	
				       							<td></td>
												<td>
				        							<!-- Import file -->
				        							<input type="submit" value="Import files">
				       							</td>
				   							</tr>	
			    						</form:form>
			    						<tr align="right">						
											<!-- Show scenarios -->
											<td><a href="showscenarios.html"><button>
											<spring:message code="show_scenarios"/></button></a></td>
										</tr>
									</table>
								</td>
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