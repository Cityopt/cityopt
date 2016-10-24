<%--@elvariable id="extParam" type="eu.cityopt.DTO.ExtParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="create_external_parameter"/></title>

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
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="create_external_parameter_small"/></td>
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
						<form:form method="post" action="createextparam.html" modelAttribute="extParam">
						
						<table align="left" style="width: 400px">
							<tr>
								<td class="infosmall"><spring:message code="parameter_name_requirements"/></td>
							</tr>
							<tr class="spacerow"></tr>
							<tr>
								<td class="infosmall">
									<spring:message code="name"/>*
								</td>
							</tr>
							<tr>
								<td>
									<form:input style="width:400px" type="text" path="name"/>
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td align="right"><button style="width:100px" type="submit" class="activebutton"><spring:message code="create"/></button>
									<input type="submit" value="Cancel" name="cancel">
								</td>
							</tr>
						</table>
						</form:form>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>