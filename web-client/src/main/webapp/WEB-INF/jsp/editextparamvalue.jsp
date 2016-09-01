<%--@elvariable id="extParamVal" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="edit_external_parameter_value"/></title>

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
								<td><spring:message code="edit_external_parameter_value"/></td>
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
						<form:form method="post" action="editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}" modelAttribute="paramForm">
			
						<table align="left">
							<col style="width:150px">
							<col style="width:250px">
							<tr>
								<td>
									<!-- Name -->
									<spring:message code="name"/>
								</td>
								<td>
									<form:label style="width:300px" type="text" path="name"/>${paramForm.name}
								</td>
							</tr>
							<tr>
								<td>
									<spring:message code="comment"/>
								</td>
								<td>
									<form:input style="width:300px" type="text" path="comment"/>
								</td>
							</tr>
							
        					<c:if test="${extParamVal.getExtparam().getType().getTypeid() < 4}">
        					<tr>
								<td>
									<!-- Value -->
									<spring:message code="value"/>
								</td>
								<td>
									<form:input style="width:300px" type="text" path="value"/>
								</td>
							</tr>
							</c:if>
        					
        					<tr>
								<td>					
									<spring:message code="unit"/>
								</td>
								<td>					
									<form:select path="unit">
										<option value="${paramForm.unit}" selected>${paramForm.unit}</option>
										<c:forEach items="${units}" var="unit">																																
											<option value="${unit.name}">${unit.name}</option>
										</c:forEach>
									</form:select>				
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td></td>
								<!-- Update and Cancel -buttons -->
								<td align="right">
									<button class="activebutton" style="width:100px" type="submit"><spring:message code="update"/></button>
									<input style="width:100px" type="submit" name="cancel" value="<spring:message code="cancel"/>">
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