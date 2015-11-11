<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Delete project</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<style type="text/css">	
	table.tablestyle{
		margin-left: 35%;
		margin-top: 5%;
		width: 500px;
		border: 1px;	
	}
	
	td.mainmenu{
	position: absolute;
    top: 0px;
    left: 0px;
    width: 260px;
	}
	
	h2{
	margin-left: 35%;
	margin-top: 5%;
	}
	
</style>

</head>

<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td class="mainmenu" ><%@ include file="mainmenu.inc"%></td>
			<td width="30"></td>
			<td valign="top">
				<div
					style="overflow: scroll; height: 100%; width: 100%; overflow: auto">
					<form:form method="post" action="deleteproject.html">
						<!-- Delete Project -->
						<h2><spring:message code="delete_project"/></h2>
						
						
						<table class="tablestyle" width="400" border="1">

							<tr class="DeleteTables" height="20">
								<!-- Name -->
								<th><spring:message code="name"/></th>
								<!-- ID -->
								<th><spring:message code="id"/></th>
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
									<td>${project.prjid}</td>
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
				</div>
			</td>
		</tr>
	</table>
</body>
</html>