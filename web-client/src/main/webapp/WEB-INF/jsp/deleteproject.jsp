<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="delete_project"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

</head>

<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td valign="top"><%@ include file="mainmenu.inc"%></td>
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
									<td><spring:message code="deleteproject"/></td>
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
						<td>
							<table>
								<tr>
									<td class="spacecolumn"></td>
									<td>				
										<form:form method="post" action="deleteproject.html">
										<h2 class="error">
											${error}
										</h2>
										
										<table class="tablestyle" width="400">
											<tr class="DeleteTables" height="20">
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
										</form:form>
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