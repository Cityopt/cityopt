<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="scenarioForm" type="com.cityopt.web.ScenarioForm"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="open_scenario"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1100px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="open_scenario_small"/></td>
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
					<td valign="top">
						<table class="tablestyle" width="750">
							<col style="width:200px">	
							<col style="width:350px">
							<col style="width:50px">
							<col style="width:50px">
							<col style="width:50px">
							<col style="width:50px">
																												
							<tr height="20">
								<!--Name-->
							    <th><spring:message code="name"/></th>
							    <!--Description-->
							    <th><spring:message code="description"/></th>
							    <th>Pareto</th>
							    <!--Status-->    
							    <th><spring:message code="status"/></th>				    
							    <!--Open-->    
							    <th><spring:message code="open"/></th>
							    <!-- Clone-->				
							    <th><spring:message code="clone"/></th>
							</tr>
											
							<c:forEach items="${scenarioForms}" var="scenarioForm">
							<tr>
								<td>${scenarioForm.name}</td>
						    	<td>${scenarioForm.description}</td>
						    	<c:choose>
						    		<c:when test="${scenarioForm.pareto}">
						    			<td align="center">X</td>
					    			</c:when>
					    			<c:otherwise>
					    				<td></td>
					    			</c:otherwise>
				    			</c:choose>
								<td>${scenarioForm.status}</td>			
								<td>
									<c:set var="tooltip_open_scenario"><spring:message code="tooltip_open_scenario"/></c:set>
									<a href="<c:url value='openscenario.html?scenarioid=${scenarioForm.id}'/>" title="${tooltip_open_scenario}">
										<button align="right" type="button" value="Open"><spring:message code="open"/>
										</button>
									</a>
								</td>
								<td>
									<c:set var="tooltip_clone"><spring:message code="tooltip_clone"/></c:set>
									<a href="<c:url value='clonescenario.html?scenarioid=${scenarioForm.id}'/>" title="${tooltip_clone}">
										<button align="right" type="button" value="Clone"><spring:message code="clone"/></button>
									</a>
								</td>
						   	</tr>
							</c:forEach>				
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="info">
						<table width="750">
							<col style="width:33%">	
							<col style="width:34%">	
							<col style="width:33%">	
							<tr>
								<td align="left">
									<c:choose>
										<c:when test="${pagenum > 1}">
											<a href="openscenario.html?pagenum=${(pagenum - 1)}">
												< <spring:message code="previous"/>
											</a>
										</c:when>
										<c:otherwise>
											 &nbsp;
										</c:otherwise>	
									</c:choose>
								</td>
								<td align="center">
									<spring:message code="page" /> ${pagenum}/${pages}
								</td>
								<td align="right">
									<c:if test="${pagenum < pages}">
										<a href="openscenario.html?pagenum=${(pagenum + 1)}">
											<spring:message code="next"/> >
										</a>
									</c:if>
								</td>
							</tr>
							<tr>
								<td></td>
								<form:form method="post" action="openscenario.html" modelAttribute="paramForm">
								<td align="center">
									<spring:message code="go_to_page" />&nbsp;<form:input type="text" path="value" style="width:20px"/>&nbsp;
									<input type="submit" value="<spring:message code="go"/>"/>
								</td>
								</form:form>
								<td></td>								
							</tr>
						</table>
					</td>
				</tr>
				<tr height="100%">
				</tr>
			</table>
			</div>
		</td>
    </tr>
</table>	  
</body>
</html>