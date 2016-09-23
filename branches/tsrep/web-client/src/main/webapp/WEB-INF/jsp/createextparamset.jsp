<%--@elvariable id="extParamValSet" type="eu.cityopt.DTO.ExtParamValSetDTO"--%>
<%--@elvariable id="extParamVal" type="eu.cityopt.DTO.ExtParamValDTO"--%>
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
<title>CityOpt <spring:message code="create_external_param_set"/></title>

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
								<td><spring:message code="create_external_param_set"/></td>
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
						<form:form method="post" action="createextparamset.html" modelAttribute="extParamValSet">
						<table>
							<tr>
								<td>
									<table>
										<col style="width:150px">
										<col style="width:150px">
										<tr>
											<td class="infosmall">
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
										<tr height="20">
										    <th><spring:message code="name"/></th>
										    <th><spring:message code="value"/></th>
										</tr>
										
										<c:forEach items="${extParamVals}" var="extParamVal">
										<tr>
											<td>${extParamVal.extparam.name}</td>
									    	<td>${extParamVal.value}</td>
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
									<button class="activebutton" type="submit"><spring:message code="save"/></button>
									<!-- <a href="projectparameters.html"><button type="button">Cancel</button></a>-->
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