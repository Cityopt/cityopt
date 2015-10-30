<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt GA chart</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="20"></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1200px;overflow:auto">
			<table width="1100px">
				<col style="width:300px">
				<col style="width:30px">
				<col style="width:800px">
				<tr>
					<td><h2>Genetic optimization results</h2>
						<p>To draw a scatter plot, please select 1 or more scenarios and 2 objective functions.</p></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<!-- Scenarios --><b><spring:message code="scenarios"/></b>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="360">
										<col style="width:120px">
										<col style="width:240px">	
																															
										<tr height="20">
											<!-- Select --><th><spring:message code="select"/></th>
										    <!-- Name --> <th><spring:message code="name"/></th>
										</tr>
														
										<c:forEach items="${scenarios}" var="scenario">
										<tr>
											<c:choose>
												<c:when test="${usersession.hasSelectedGAScenarioId(scenario.scenid)}">
													<tr style="background-color: #D4D4D4">													
													<td>
													<!-- Remove button -->
													<spring:message code="added"/> 
													(<a href="gachart.html?action=remove&scenarioid=${scenario.scenid}">
													<spring:message code="remove"/></a>)
													</td>
												</c:when>
												<c:otherwise>
													<tr>
													<!-- Add -button -->
													<td><a href="gachart.html?action=add&scenarioid=${scenario.scenid}">
													<spring:message code="add"/></a></td>
												</c:otherwise>
											</c:choose>
											<td>${scenario.name}</td>
									   	</tr>
										</c:forEach>				
									</table>
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr>
								<td>
									<!-- Objective functions -->
									<b>Objective functions</b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle" style="width:360px">
										<col style="width:120px">
										<col style="width:240px">
										<tr>
											<!-- Select --><th><spring:message code="select"/></th>
											<!-- Objective function --><th>Objective function</th>
										</tr>
										<c:forEach items="${objFuncs}" var="objFunc">
											<c:choose>
												<c:when test="${usersession.hasSelectedGAObjFuncId(objFunc.obtfunctionid)}">
													<tr style="background-color: #D4D4D4">
													<td>Selected (<a href="gachart.html?action=remove&objfuncid=${objFunc.obtfunctionid}">Remove</a>)</td>
												</c:when>
												<c:otherwise>
													<tr>
														<td><a href="gachart.html?action=add&objfuncid=${objFunc.obtfunctionid}">Select</a></td>
												</c:otherwise>
											</c:choose>
											
											<td>${objFunc.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>		
						</table>
					</td>
					<td></td>
					<td valign="top">
						<table>
							<tr>
								<td valign="top" style="width: 750px; height: 400px; border-style: solid; border-radius: 1em">
									<img src="gachart.png">
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td align="right">
											</td>
											<td align="right">
												<table>
													<tr>
														<td>
															<!-- Remove selections -->
															<a href="gachart.html?action=removeall"><button type="button" style="width: 150px">
															<spring:message code="remove_selection"/></button></a>
														</td>
													</tr>
													<tr>
														<td>
															<!-- Refresh chart -->
															<a href="gachart.html?action=refreshchart">
															<button type="button" style="width: 150px">
															<spring:message code="refresh_chart"/></button></a>
														</td>
													</tr>
													<tr>
														<td>
															<!--Open chart window -->
															<a href="gachart.html?action=openwindow"><button type="button" style="width: 150px">
															<spring:message code="open_chart_window"/></button></a>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
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