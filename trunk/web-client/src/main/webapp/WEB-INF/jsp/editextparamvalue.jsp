<%--@elvariable id="extParamVal" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="edit_external_parameter_value"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<form:form method="post" action="editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}" modelAttribute="paramForm">
			<!--Edit external parameter value-->
			<h1><spring:message code="edit_external_parameter_value"/></h1>

			<table align="center">
				<col style="width:150px">
				<col style="width:80px">
				<col style="width:80px">
				<tr>
					<td>
						<!-- Name -->
						<spring:message code="name"/>
					</td>
					<td>
						${paramForm.name}
					</td>
				</tr>
				<tr>
					<td>
						<!-- Value -->
						<spring:message code="value"/>
					</td>
					<td>
						<form:input style="width:300px" type="text" path="value"/>
					</td>
				</tr>
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
					<td align="right"><input style="width:100px" type="submit" value="<spring:message code="update"/>"/>
					<input style="width:100px" type="submit" name="cancel" value="<spring:message code="cancel"/>"></td>
				</tr>
			</table>
			
			</form:form>
			</div>
		</td>
     </tr>
</table>
</body>
</html>