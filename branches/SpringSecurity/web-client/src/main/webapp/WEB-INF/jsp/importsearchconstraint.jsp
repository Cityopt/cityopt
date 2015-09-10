<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="com.cityopt.DTO.OptSearchConstDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt import search constraints</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<!-- Search constraints -->
						<h2><spring:message code="search_constraints"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:300px">

							<tr>
								<td><b><spring:message code="search_constraints"/></b></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:240px">
										<col style="width:300px">
										<tr>
											<!-- Select, Search constraint and Expression -->
											<th><spring:message code="select"/></th>
											<th><spring:message code="search_constraints"/></th>
											<th><spring:message code="expression"/></th>
										</tr>
							
										<c:forEach items="${constraints}" var="constraint">
											<tr>
												<td><a href="<c:url value='importsearchconstraint.html?constraintid=${constraint.optconstid}'/>"><spring:message code="select"/></a></td>
												<td>${constraint.name}</td>
										    	<td>${constraint.expression}</td>
										   	</tr>
											</c:forEach>
									</table>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<td align="right"><a href="editoptimizationset.html"><button type="button"><spring:message code="close"/></button></a></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height=20></tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>