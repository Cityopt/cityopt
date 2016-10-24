<%--@elvariable id="unitForm" type="eu.cityopt.web.UnitForm"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt create unit</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td><%@ include file="mainmenu.inc"%></td>
		<td valign="top">
			<form:form method="post" action="createunit.html" modelAttribute="unitForm">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="create_unit"/>
								</td>
								<td align="left" width="40">
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
						<table>
							<tr>
								<td class="infosmall">
									<spring:message code="name"/>*
								</td>
							</tr>
							<tr>
								<td>
									<form:input style="width:300px" type="text" path="name"/>
								</td>
							</tr>
							<tr>
								<td class="infosmall">
									<spring:message code="type"/>
								</td>
							</tr>
							<tr>
								<td>
									<form:select path="type" items="${types}" style="width: 300px" />
								</td>
							</tr>
							<tr>
								<td align="right"><button class="activebutton" style="width:100px" type="submit"><spring:message code="create"/></button>
									<a href="units.html"><button style="width:100px" type="button" value="Cancel"><spring:message code="cancel"/></button></a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			</div>
			</form:form>
		</td>
     </tr>
</table>
</body>
</html>