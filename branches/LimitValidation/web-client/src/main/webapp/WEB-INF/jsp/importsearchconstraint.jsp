<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="com.cityopt.DTO.OptSearchConstDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="import_search_constraints"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
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
								<td><spring:message code="import_constraint"/></td>
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
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<tr>
								<td>
									<table>
										<col style="width:300px">
			
										<tr>
											<td class="infosmall"><spring:message code="search_constraints"/></td>
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
															<td><a href="<c:url value='importsearchconstraint.html?constraintid=${constraint.optconstid}'/>"><button type="button"><spring:message code="select"/></button></a></td>
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
			</div>
		</td>
	</tr>
</table>
</body>
</html>