<%--@elvariable id="inputParamForm" type="eu.cityopt.web.ParamForm"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="edit_output_variable"/></title>

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
			<div style="overflow:scroll;height:1000px;width:1000px;overflow:auto">
			<form:form method="post" action="editoutputvariable.html?outputvarid=${outputVar.outvarid}" modelAttribute="paramForm">
			<h1><spring:message code="edit_output_variable"/></h1>
		
			<table>
				<col style="width:100px">
				<col style="width:250px">
				<tr>
					<td>
						<spring:message code="name"/>
					</td>
					<td>
						${outputVar.name}
					</td>
				</tr>
				<tr>
					<td>					
						<spring:message code="unit"/>
					</td>
					<td>					
						<form:select path="unit">
							<option value="${outputVar.unit.name}" selected>${outputVar.unit.name}</option>
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
					<td align="right"><input style="width:100px" type="submit" value="Update"/>
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