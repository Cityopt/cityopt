<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenario" type="com.cityopt.DTO.ScenarioDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Run Multi-scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
<tr>
	<td>
		<%@ include file="mainmenu.inc"%>
	</td>
	<td width="50"></td>
	<td valign="top">
		<div style="overflow:scroll;height:600px;width:600px;overflow:auto">
		<form:form method="post" action="runmultiscenario.html">
		<!-- Run Multi-scenario -->
		<h2><spring:message code="run_multi_scenario"/></h2>
		
		<table>
			<col style="width:500px">
			<tr>
				<td>
					<!-- Multi-scenarios -->
					<b><spring:message code="multi_scenarios"/></b>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<table class="tablestyle">
						<col style="width:100px">
						<col style="width:200px">
						<tr>
							<!-- Select -->
							<th><spring:message code="select"/></th>
							<!-- Name -->
							<th><spring:message code="name"/></th>
						</tr>
						
						<c:forEach items="${scenGens}" var="scenGen">
							<c:if test="${scenGen.algorithm.algorithmid == 1}">
								<c:choose>
									<c:when test="${usersession.hasSelectedScenGenId(scenGen.scengenid)}">
										<tr style="background-color: #D4D4D4">													
										<td>
										<!-- Remove button -->
										(<spring:message code="added"/>) 
										(<a href="runmultiscenario.html?action=remove&multiscenarioid=${scenGen.scengenid}">
										<spring:message code="remove"/></a>)
										</td>
									</c:when>
									<c:otherwise>
										<tr>
										<!-- Add -button -->
										<td><a href="runmultiscenario.html?action=add&multiscenarioid=${scenGen.scengenid}">
										<spring:message code="add"/></a></td>
									</c:otherwise>
								</c:choose>
								
								<td>${scenGen.name}</td>
						   		</tr>
					   		</c:if>
						</c:forEach>
					</table>
				</td>
			</tr>
		</table>

		<table width="600">
			<tr>
				<td align="right">
					<input align="right" type="submit" value="<spring:message code="run_multi_scenario"/>"/>
			    </td>
			</tr>
      	</table>
		</form:form>
		</div>
	</td>
</tr>
</table>
		
</body>
</html>