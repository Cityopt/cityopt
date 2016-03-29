<%--@elvariable id="extParamValSet" type="eu.cityopt.DTO.ExtParamValSetDTO"--%>
<%--@elvariable id="extParamVal" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="extParam" type="eu.cityopt.DTO.ExtParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="create_external_param_set"/></title>

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
			<h1><spring:message code="create_external_param_set"/></h1>
			<div style="overflow:scroll;height:500px;width:900px;overflow:auto">
			<form:form method="post" action="createextparamset.html" modelAttribute="extParamValSet">
			
			<table>
				<tr>
					<td>
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<tr>
								<td>
									<spring:message code="name"/>*
								</td>
								<td>
									<form:input style="width:150px" type="text" path="name"/>
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<b><spring:message code="external_parameters"/></b>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="400px">
							<col style="width:250px">
							<col style="width:100px">
							<!--<col style="width:50px">-->
							<tr height="20">
							    <th><spring:message code="name"/></th>
							    <th><spring:message code="value"/></th>
							</tr>
							
							<c:forEach items="${extParamVals}" var="extParamVal">
							<tr>
								<td>${extParamVal.extparam.name}</td>
						    	<td>${extParamVal.value}</td>
						    	<!-- <td>
									<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}'/>">
										<button align="right" type="button" value="Edit">Edit</button>
									</a>
								</td>-->
						   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				<tr height="20">
					<td>
					</td>
				</tr>
				<tr>
					<td align="right">
						<input type="submit" value="Save">
						<!-- <a href="projectparameters.html"><button type="button">Cancel</button></a>-->
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