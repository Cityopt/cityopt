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
<title>CityOpt <spring:message code="export_data" /></title>

<script>
    function openInfoWindow() {
    	   window.open("exportdata_info.html",'<spring:message code="export_data" />','width=600,height=800');
    }
</script>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td valign="top"><%@ include file="mainmenu.inc"%>
			</td>
			<td valign="top">
				<div style="overflow:scroll;height:100%;width:820px;overflow:auto">
				<table class="maintable">
					<col style="width:410px">
					<col style="width:410px">
					<%@ include file="toprow.inc"%>
					<tr class="titlerow">
						<td colspan="2">
							<table width="100%">
								<tr>
									<td class="spacecolumn"></td>
									<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="export_data"/></td>
									
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="error">${error}</td>
						<td class="spacecolumn"></td>
					</tr>
					<tr>
						<td valign="top">
							<table style="width:100%">
								<tr height="20"></tr>
								<tr>
									<td class="spacecolumn"></td>
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
											<tr height="20"></tr>
											<tr>
												<!-- Export project file (CSV) -->
												<td><spring:message code="export_project_file_CSV" /></td>
												<td><a href="exportstructurefile.html"><button>
															<spring:message code="export" />
														</button></a></td>
		
											</tr>
											<tr>
												<!-- Export scenario file CSV -->
												<td><spring:message code="export_scenario_file_CSV" /></td>
												<td>
													<!-- Export -button --> <a href="exportscenarios.html"><button>
															<spring:message code="export" />
														</button></a>
												</td>
		
											</tr>
											<tr>
												<td>
													<spring:message	code="export_external_parameter_sets_file_CSV" />
												</td>
												<td>
													<c:choose>
														<c:when test="${enableExtParamSetExport}">
															<a href="exportextparamsets.html">
																<button type="button">
																	<spring:message code="export" />
																</button>
															</a>
														</c:when>
														<c:otherwise>
															<a href="exportdata.html?error=true">
																<button type="button">
																	<spring:message code="export" />
																</button>
															</a>
														</c:otherwise>
													</c:choose>
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