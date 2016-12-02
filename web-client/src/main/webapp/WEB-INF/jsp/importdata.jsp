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
    	   window.open("importdata_info.html",'<spring:message code="import_data"/> info','width=510,height=600,scrollbars=yes');
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
			<div style="overflow:scroll;height:100%;width:100%;overflow:auto">
			<table class="maintablewide">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="import_data_small"/></td>
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
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<col style="width:700px">
							<col style="width:200px">
							<tr>
								<td>
									<table>
										<col style="width:100px">
										<col style="width:300px">
										<col style="width:300px">
										<tr>
											<td colspan="2" class="info">
												<!-- Import project data -->
												<spring:message code="import_project_data"/>
											</td>
										</tr>
										<tr><td colspan="3" class="activeline"></td></tr>
										<form:form method="POST" action="importstructurefile.html" enctype="multipart/form-data">
											<tr>
												<td></td>
				        						<td></td>
												<td class="infosmall"><spring:message code="project_file"/> (CSV)</td>
											</tr>
				        					<tr>
				        						<!-- Import project file (CSV) -->
			        							<td></td>
				        						<td></td>
												<td class="infosmall"><input id="file" name="file" type="file"/></td>
											</tr>
											<tr class="spacerow"></tr>
											<tr>	
												<td></td>
				        						<td></td>
				        						<td>
				        							<!--Import file-->
				        							<input type="submit" style="width: 155px" value="<spring:message code="import_file"/>">
				       							</td>
				   							</tr>	
			    						</form:form>
		    							<tr class="spacerowbig"></tr>
										<tr><td colspan="2" class="info"><spring:message code="import_scenarios"/></td></tr>
			    						<tr><td colspan="3" class="activeline"></td></tr>
			    						<tr>
											<td></td>
											<td class="infosmall"><spring:message code="scenario_file"/> (CSV)</td>
											<td class="infosmall"><spring:message code="time_series_file"/> (CSV)</td>
										</tr>
										<form:form method="POST" action="importscenarios.html" enctype="multipart/form-data">
				        					<tr>
				        						<td></td>
												<td class="infosmall"><input class="inactivebutton" id="file" name="file" type="file"/></td>
												<td class="infosmall"><input id="timeSeriesFile1" name="timeSeriesFile1" type="file"/></td>
											</tr>
											<tr class="spacerow"></tr>
											<tr>	
			       								<td></td>
				        						<td></td>
												<td>
				        							<!-- Import file -->
				        							<input type="submit" style="width: 155px" value="<spring:message code="import_files"/>"/>
			        							</td>
		        							</tr>
			    							</form:form>
			    							
			    						<tr class="spacerowbig"></tr>
			    						<tr><td colspan="2" class="info"><spring:message code="import_database_optimization_set"/></td></tr>
			    						<tr><td colspan="3" class="activeline"></td></tr>
										<form:form method="POST" action="importoptimizationset.html" enctype="multipart/form-data">
				        					<tr>
				        						<td></td>
												<!-- Import optimization set file CSV -->
				        						<td class="infosmall"><spring:message code="database_optimization_set_file"/> (CSV)</td>
												<td class="infosmall"><spring:message code="time_series"/> (CSV)</td>
											</tr>
											<tr>
				        						<!-- Import external parameter sets (CSV) -->
				        						<td></td>
												<td class="infosmall"><input id="file" name="file" type="file"/></td>
												<td class="infosmall"><input id="fileTimeSeries" name="fileTimeSeries" type="file"/></td>
											</tr>
											<tr class="spacerow"></tr>
											<tr>	
				       							<td></td>
			        							<td></td>
				        						<td>
				        							<input type="submit" value="<spring:message code="import_files"/>">
				       							</td>
				   							</tr>	
			    						</form:form>
			    						<tr class="spacerowbig"></tr>
			    						<tr><td colspan="2" class="info"><spring:message code="import_genetic_optimization_set"/></td></tr>
			    						<tr><td colspan="3" class="activeline"></td></tr>
										<form:form method="POST" action="importoptimizationproblem.html" enctype="multipart/form-data">
				        					<tr>
				        						<td></td>
												<td class="infosmall"><spring:message code="genetic_optimization_file"/> (CSV)</td>
												<td class="infosmall"><spring:message code="time_series"/> (CSV)</td>
											</tr>
											<tr>
				        						<!-- Import external parameter sets (CSV) -->
				        						<td></td>
												<td class="infosmall"><input id="fileProblem" name="fileProblem" type="file"/></td>
												<td class="infosmall"><input id="fileTimeSeries" name="fileTimeSeries" type="file"/></td>
											</tr>
											<tr class="spacerow"></tr>
											<tr>	
			       								<td></td>
				        						<td></td>
				        						<td>
				        							<input type="submit" value="<spring:message code="import_files"/>">
				       							</td>
				   							</tr>	
			    						</form:form>
									</table>
								</td>
								<td valign="top">
									<table style="width: 100%">
										<tr>
											<td class="info">
												<spring:message code="project_name"/>
											</td>
										</tr>
										<tr>
											<td class="activeline">
												<c:if test="${not empty project}">
													${project.name}
												</c:if>
											</td>
										</tr>
										<tr height="5"></tr>
										<tr>
											<!-- Location: -->						
											<td class="info"><spring:message code="location"/></td>
										</tr>
										<tr>
											<td class="activeline">
												<c:if test="${not empty project}">
													${project.location}
												</c:if>
											</td>
										</tr>
										<tr height="5"></tr>
										<tr>
											<!-- Design target: -->						
											<td class="info"><spring:message code="design_target"/></td>
										</tr>
										<tr>
											<td class="activeline">
												<c:if test="${not empty project}">
													${project.designtarget}
												</c:if>
											</td>
										</tr>
										<tr height="5"></tr>
										<tr>
											<!-- Description: -->						
											<td class="info">
												<spring:message code="description"/></td>
										</tr>
										<tr>
											<td class="regular">
												<textarea readonly="readonly" style="overflow:hidden; margin-top: 2px" rows="6" cols="24">
													<c:if test="${not empty project}">
														${project.description}
													</c:if>
												</textarea>
											</td>
										</tr>
			    						<tr class="spacerowbig"></tr>
   										<tr>
											<td class="info">
												<spring:message code="project_templates"/>
											</td>
										</tr>
										<tr>
											<td class="activeline">
												<a href="exportprojecttemplate.html">						
													<button style="width: 200px; margin-top: 7px"><spring:message code="download"/></button>
												</a>
											</td>
										</tr>
										<tr height="10"></tr>
   										<tr>
											<td class="info">
												<spring:message code="scenario_templates"/>
											</td>
										</tr>
										<tr>
											<td class="activeline">
												<a href="exportscenariotemplate.html">
													<button style="width: 200px; margin-top: 7px"><spring:message code="download"/></button>
												</a>
											</td>
										</tr>
										<tr style="height: 10px"></tr>
			    						<tr>
											<td class="info">
												<spring:message code="project_data"/>
											</td>
										</tr>
										<tr>
			    							<td class="activeline">
												<a href="projectdata.html">
													<button style="width: 200px; margin-top: 7px"><spring:message code="show_project_data"/></button>
												</a>
											</td>
										</tr>
										<tr style="height: 10px"></tr>
		    							<tr>
											<td class="info">
												<spring:message code="scenario_data"/>
											</td>
										</tr>
										<tr>
		    								<td class="activeline">
				       							<a href="showscenarios.html">
													<button style="width: 200px; margin-top: 7px">
														<spring:message code="show_scenarios"/>
													</button>
												</a>
											</td>
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