<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="delete_scenario"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="delete_scenario_small"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr height="20">
					<td class="spacecolumn"></td>
					<td class="active"><spring:message code="scenarios" /></td>
				</tr>
				<form:form method="post" action="deletescenario.html" modelAttribute="checkForm">
				<tr height="300">
					<td class="spacecolumn"></td>
					<td valign="top">
						<div style="overflow: scroll; height: 400px; width: 935px; overflow: auto;">
						<table class="tablestyle" width="915">
							<col style="width:35px">
							<col style="width:200px">
							<col style="width:425px">
							<col style="width:60px">
							
							<tr height="20">
							    <th><spring:message code="select"/></th>
								<!-- Name -->
							    <th><spring:message code="name"/></th>
							    <!-- Description -->
							    <th><spring:message code="description"/></th>
							    <th>Pareto</th>
							</tr>
						<c:forEach items="${scenarioForms}" var="scenarioForm">
							<tr>
								<td class="checkboxstyle">
									<form:checkbox path="checkById[${scenarioForm.id}].checked" value="true"/>
								</td>
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
						   	</tr>
						</c:forEach>
						</table>
						</div>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top" align="right">
						<table>
							<tr>
								<td>
									<a href="deletescenario.html?action=selectnonpareto">
										<button type="button" style="width: 200px">
											<spring:message code="select_non_pareto"/>
										</button>
									</a>
								</td>
								<td>
						    		<c:set var="tooltip_delete"><spring:message code="tooltip_delete"/></c:set>
									<button class="activebutton" type="submit" onclick="return confirm('<spring:message code="confirm_scenario_deletion"/>')">
										 <spring:message code="delete"/>
									 </button>
								</td>
								<td width="20"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td></td></tr>
				</form:form>
				<tr><td></td></tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>