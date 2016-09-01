<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="edit_component"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="edit_component"/></td>
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
					<td valign="top">
						<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
						<form:form method="post" action="editcomponent.html?componentid=${component.componentid}" modelAttribute="component">
			
						<!-- Tooltips -->
						<c:set var="tooltip_edit"><spring:message code="tooltip_edit_name"/></c:set>
						<c:set var="tooltip_update"><spring:message code="tooltip_update"/></c:set>
						<c:set var="tooltip_cancel"><spring:message code="tooltip_cancel"/></c:set>
			
						<table align="left">
							<col style="width:300px">
							<tr>
								<td class="infosmall">
									<spring:message code="name"/>*
								</td>
							</tr>
							<tr>
								<td>
									<form:input style="width:300px" title="${tooltip_edit}" type="text" path="name"/>
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td align="right">
									<button class="activebutton" title="${tooltip_update}" style="width:100px" type="submit"><spring:message code="update"/></button>
									<a href="projectparameters.html"><button title="${tooltip_cancel}" style="width:100px" type="button" value="Cancel">
									<spring:message code="cancel"/></button></a>
								</td>
							</tr>
						</table>
						</form:form>
						</div>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>