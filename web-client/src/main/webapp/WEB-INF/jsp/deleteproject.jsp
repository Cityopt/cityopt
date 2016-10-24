<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="delete_project"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

</head>

<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td valign="top"><%@ include file="mainmenu.inc"%></td>
			<td valign="top">
				<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
				<table class="maintablenarrow">
					<col style="width:500px">
					<col style="width:500px">
					<%@ include file="toprow.inc"%>
					<tr class="titlerow">
						<td colspan="2">
							<table width="100%">
								<tr>
									<td class="spacecolumn"></td>
									<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="delete_project_small"/></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
							<table>
								<tr>
									<td class="spacecolumn"></td>
									<td>
										<table>				
											<form:form method="post" action="deleteproject.html">
											<tr>
												<td class="error">
													${error}
												</td>
											</tr>
											<tr>
												<td class="active"><spring:message code="projects"/></td>
											</tr>
											<tr>
												<td valign="top">
													<table class="tablestyle" width="400">
														<tr height="20">
															<!-- Name -->
															<th><spring:message code="name"/></th>
															<!-- Location -->
															<th><spring:message code="location"/></th>
															<!-- Description -->
															<th><spring:message code="description"/></th>
															<!-- Delete -->
															<th><spring:message code="delete"/></th>
														</tr>
							
														<c:forEach items="${projects}" var="project">
															<tr>
																<td>${project.name}</td>
																<td>${project.location}</td>
																<td>${project.description}</td>
																<td><a
																	<c:set var="tooltip_delete"><spring:message code="tooltip_delete_project"/></c:set>										
																	href="<c:url value='deleteproject.html?prjid=${project.prjid}'/>" title="${tooltip_delete}"
																	 onclick="return confirm('<spring:message code="confirm_project_deletion"/>')">
																		<button type="button" value="Delete">
																		<!-- Delete button -->
																		<spring:message code="delete"/></button>
																</a></td>
															</tr>
														</c:forEach>
													</table>
												</td>
											</tr>
										</form:form>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr style="height: 100%">
						<td></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>