<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="import_data"/></title>

<script>
    function openInfoWindow() {
    	   window.open("importdata_info.html",'<spring:message code="import_data"/> info','width=500,height=600,scrollbars=yes');
    }
</script>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1200px;overflow:auto">
			<table class="maintable">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="import_data"/></td>
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
					<td class="spacecolumn"></td>
					<td align="right">
						<spring:message code="download_project_templates"/>
						<a href="">						
							<button><spring:message code="download"/></button>
						</a>
						<spring:message code="download_scenario_templates"/>
						<a href="">
							<button><spring:message code="download"/></button>
						</a>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="successful">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<tr>
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
								<td><spring:message code="design_target"/>:</td>
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
    						<tr class="spacerowbig"></tr>
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
    						<tr class="spacerowbig"></tr>
    						<form:form method="POST" action="importoptimizationset.html" enctype="multipart/form-data">
	        					<tr>
	        						<td><b><spring:message code="import_database_optimization_set"/></b></td>
									<!-- Import optimization set file CSV -->
	        						<td><spring:message code="database_optimization_set_file"/> (CSV)</td>
									<td><spring:message code="time_series"/></td>
								</tr>
								<tr>
	        						<!-- Import external parameter sets (CSV) -->
	        						<td></td>
									<td><input id="file" name="file" type="file"/></td>
									<td><input id="fileTimeSeries" name="fileTimeSeries" type="file"/></td>
								</tr>
								<tr>	
	       							<td></td>
	        						<td>
	        							<input type="submit" value="<spring:message code="import_files"/>">
	       							</td>
	   							</tr>	
    						</form:form>
    						<tr class="spacerowbig"></tr>
    						<form:form method="POST" action="importoptimizationproblem.html" enctype="multipart/form-data">
	        					<tr>
	        						<td><b><spring:message code="import_genetic_optimization_set"/></b></td>
									<td><spring:message code="genetic_optimization_file"/> (CSV)</td>
									<td><spring:message code="time_series"/> (CSV)</td>
								</tr>
								<tr>
	        						<!-- Import external parameter sets (CSV) -->
	        						<td></td>
									<td><input id="fileProblem" name="fileProblem" type="file"/></td>
									<td><input id="fileTimeSeries" name="fileTimeSeries" type="file"/></td>
								</tr>
								<tr height="20"></tr>
								<tr>	
	       							<td></td>
	        						<td>
	        							<input type="submit" value="<spring:message code="import_files"/>">
	       							</td>
	   							</tr>	
    						</form:form>
    						<tr>
								<td></td>
								<td></td>
								<!-- Show project data & Import buttons -->		
								<td align="right">
									<a href="projectdata.html">
										<button><spring:message code="show_project_data"/></button>
									</a>
								</td>
							</tr>
    						<tr class="spacerowbig"></tr>
    						<tr>
								<td class="active">
									<spring:message code="import_scenarios"/>
								</td>
								<td><spring:message code="scenario_file"/></td>
								<td><spring:message code="time_series_file"/></td>
							</tr>
							<form:form method="POST" action="importscenarios.html" enctype="multipart/form-data">
	        					<tr>
	        						<td><spring:message code="import_scenarios"/> (CSV)</td>
									<td><input id="file" name="file" type="file"/></td>
									<td><input id="timeSeriesFile1" name="timeSeriesFile1" type="file"/></td>
								</tr>
								<tr>	
	       							<td></td>
									<td>
	        							<!-- Import file -->
	        							<input type="submit" value="<spring:message code="import_files"/>">
	       							</td>
	   							</tr>	
    						</form:form>
							<tr>
								<td></td>
    							<td></td>					
								<td align="right">
									<a href="showscenarios.html">
										<button style="width:150px">
											<spring:message code="show_scenarios"/>
										</button>
									</a>
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