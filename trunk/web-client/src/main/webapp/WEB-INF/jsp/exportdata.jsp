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
<title>CityOpt export data</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td valign="top"><%@ include file="mainmenu.inc"%>
			</td>

			<td width=30></td>
			<td valign="top">
			
			<!--style="background-color:#FBBA00"  -->
			<!--style="background-color:#FFFFFF"-->
				<div
					style="overflow: scroll; height: 800px; width: 1100px; overflow: auto">
					<table style="width:100%">
						<col  style="width: 100%">						
						<col  style="width: 100%">
						<col  style="width: 100%">
						
						<tr style="height: 54px;"></tr>												
						<tr style= background-color:#FBBA00>
							<td>														
								<!-- Export data --><!--  style="background-color:#FBBA00"-->								
								<h2><spring:message code="export_data" /></h2>												
							</td>
						</tr>
						<tr>
							<td valign="top">
								<table>
									<tr>
										<!-- Project name: -->
										<td><b><spring:message code="project_name" /></b>:
											${project.name}</td>
										<td><c:if test="project != null">
										${project.name}
									</c:if></td>
									</tr>
									<tr>
										<!-- Location: -->
										<td><b><spring:message code="location" /></b>:
											${project.location}</td>
										<td><c:if test="project != null">
										${project.location}
									</c:if></td>
									</tr>
									<tr>
										<!-- Design target: -->
										<td><b><spring:message code="design_target" /></b>:
											${project.description}</td>
										<td></td>
									</tr>
									<tr>
										<!-- Description: -->
										<td><b><spring:message code="description" /></b>:
											${project.description}</td>
										<td><c:if test="project != null">
										${project.description}
									</c:if></td>
									</tr>
									<tr>
										<!-- Export project file (CSV) -->
										<td><spring:message code="export_project_file_CSV" /></td>
										<td><a href="exportstructurefile.html"><button>
													<spring:message code="export" />
												</button></a></td>

										<!-- Show project data & Import buttons -->
										<td><a href="projectdata.html"><button>
													<spring:message code="show_project_data" />
												</button></a></td>
										<td align="right"></td>
									</tr>
									<tr>
										<!-- Export scenario file CSV -->
										<td><spring:message code="export_scenario_file_CSV" /></td>
										<td>
											<!-- Export -button --> <a href="exportscenarios.html"><button>
													<spring:message code="export" />
												</button></a>
										</td>

										<!-- Show scenarios -->
										<td><a href="showscenarios.html"><button>
													<spring:message code="show_scenarios" />
												</button></a></td>
									</tr>
									<tr>
										<td><spring:message
												code="export_external_parameter_sets_file_CSV" /></td>
										<td><a href="exportextparamsets.html"><button>
													<spring:message code="export" />
												</button></a></td>
									</tr>
								</table>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>