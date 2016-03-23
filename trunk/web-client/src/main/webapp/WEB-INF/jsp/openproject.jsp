<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="openproject"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<style type="text/css">
table.tablestyle{
margin: 5%;
}

h2{
margin-left: 5%;
}
</style>


</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:900px;overflow:auto">
			<h2 class="error">${error}</h2>
			<h1><spring:message code="openproject"/></h1>
			<table class="tablestyle" width="600">
				<col style="width:150px">	
				<col style="width:50px">	
				<col style="width:100px">	
				<col style="width:250px">	
				<col style="width:50px">	
				
				<tr height="20">
				    <th><spring:message code="name"/></th>
				    <th>Id</th>
				    <th><spring:message code="location"/></th>
				    <th><spring:message code="description"/></th>
				    <th><spring:message code="open"/></th>
				    <th><spring:message code="clone"/></th>
				</tr>
			
				<c:forEach items="${projects}" var="project">
				
					<tr>
						<td>${project.name}</td>
				    	<td>${project.prjid}</td>
						<td>${project.location}</td>			
						<td>${project.description}</td>			
						
						<td>
							<c:set var="tooltip_open"><spring:message code="tooltip_open_project"/></c:set>
							<a href="<c:url value='editproject.html?prjid=${project.prjid}'/>" title="${tooltip_open}">
								<button align="right"  type="button" value="Open"><spring:message code="open"/></button>
							</a>
						</td>
						
						<td>
							<c:set var="tooltip_clone"><spring:message code="tooltip_clone_project"/></c:set>
							<a href="<c:url value='cloneproject.html?projectid=${project.prjid}'/>"title=" ${tooltip_clone}">
								<button align="right" type="button" value="Clone"><spring:message code="clone"/></button>
							</a>
						</td>
				   	</tr>
				  
				</c:forEach>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>