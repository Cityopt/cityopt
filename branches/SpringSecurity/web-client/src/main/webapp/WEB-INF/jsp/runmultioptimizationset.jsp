<%--@elvariable id="openoptimizationset" type="eu.cityopt.DTO.OpenOptimizationSetDTO"--%>
<%--@elvariable id="userSession" type="eu.cityopt.web.UserSession"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Run Multi-Optimization Set</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:600px;overflow:auto">
			<h2>Run multi-optimization set</h2>
			<table>
				<tr>
					<td>
						<table class="tablestyle" width="500" border="1">
							<col style="width: 250px">
							<col style="width: 150px">
							<col style="width: 150px">
						
							<tr height="20">
								<!-- Name -->
							    <th><spring:message code="name"/></th>
							    <!-- Type -->
							    <th><spring:message code="type"/></th>
							    <!-- Select -->
							    <th><spring:message code="select"/></th>
							</tr>
						
							<c:forEach items="${openoptimizationsets}" var="openoptimizationset">
							<tr>
								<td>${openoptimizationset.name}</td>
									<c:choose>
										<c:when test="${openoptimizationset.isDatabaseSearch()}">
											<td>Database search</td>
											<c:choose>
												<c:when test="${usersession.hasOptSetId(openoptimizationset.id) == true}">
													<td>Added 
													<!-- Remove button -->
													(<a href="<c:url value='runmultioptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=db&action=remove'/>">
														<button align="right"  type="button" value="Open">
														<spring:message code="remove"/></button>
													</a>)
													</td>
												</c:when>
												<c:otherwise>
													<td>
													<!-- Add button -->
													<a href="<c:url value='runmultioptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=db&action=add'/>">
														<button align="right"  type="button" value="Open">
														<spring:message code="add"/></button>
													</a>
													</td>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<td>Genetic algorithm</td>
											<c:choose>
												<c:when test="${usersession.hasScenGenId(openoptimizationset.id)}">
													<td>Added 
													(<a href="<c:url value='runmultioptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=ga&action=remove'/>">
														<button align="right"  type="button" value="Open">Remove</button>
													</a>)
													</td>
												</c:when>
												<c:otherwise>
													<td>
													<a href="<c:url value='runmultioptimizationset.html?optsetid=${openoptimizationset.id}&optsettype=ga&action=add'/>">
														<button align="right"  type="button" value="Open">Add</button>
													</a>
													</td>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
							   	</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				<tr>
					<!-- Run multi -optimization -button  -->
					<td align="right"><a href="runmultioptimizationset.html"><button type="button">
					<spring:message code="run_multi_optimization"/></button></a></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</div>
</body>
</html>